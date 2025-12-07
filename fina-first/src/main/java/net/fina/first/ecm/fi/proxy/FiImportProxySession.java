package net.fina.first.ecm.fi.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.AssociationBody;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.fi.imp.ImportFileReader;
import net.fina.first.ecm.fi.imp.model.RegistryModel;
import net.fina.first.ecm.fi.imp.model.RegistryValueModel;
import net.fina.first.ecm.node.api.NodeLocal;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class FiImportProxySession {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private NodeLocal nodeProxySession;

    @RolesAllowed(PermissionIdNames.FIRST_FI_REGISTRY_IMPORT)
    public List<RegistryModel> importExcelFile(InputStream fileInputStream) {

        List<RegistryModel> notImportedRows = new ArrayList<>();

        ImportFileReader importFileReader = new ImportFileReader();

        try {
            AlfrescoClient alfrescoClient = ecmClientProxySession.getAlfrescoClient();


            String registryRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ROOT_FOLDER_PATH_KEY);

            NodeRepresentation registryRootNode = alfrescoClient.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, registryRootFolderPath, null);


            List<RegistryModel> models = importFileReader.readFileByMapping(fileInputStream);

            log.info("Start Importing Financial Institution Information");
            for (RegistryModel model : models) {
                try {
                    AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();

                    TreeMap<String, Object> properties = convertProperties(model.getProperties());
                    properties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);
                    NodeBodyCreate registryNodeBodyCreate = new NodeBodyCreate(model.getProperties().get(EcmConstants.REGISTRY_PROP_CODE).getValue().toString(), model.getRegistryType(), properties, null);
                    NodeRepresentation newRegistryNode = nodeProxySession.createNode(registryRootNode.getId(), registryNodeBodyCreate, true, null, null, client);

                    String fiTypeId = getFiTypeIdByCode(client, model.getProperties().get(EcmConstants.REGISTRY_PROP_TYPE_CODE).getValue().toString());

                    AssociationBody fiTypeAssociationBody = new AssociationBody();
                    fiTypeAssociationBody.setAssocType(EcmConstants.REGISTRY_ASSOC_FI_TYPE);
                    fiTypeAssociationBody.setTargetId(fiTypeId);

                    client.getNodesAPI().createAssocationCall(newRegistryNode.getId(), fiTypeAssociationBody, null);

                    NodeBodyCreate actionFolderBodyCreate = new NodeBodyCreate("Actions", "cm:folder", null, null);
                    NodeRepresentation newActionFolderNode = nodeProxySession.createNode(newRegistryNode.getId(), actionFolderBodyCreate, true, null, null, client);

                    TreeMap<String, Object> actionProps = new TreeMap<>();
                    actionProps.put(EcmConstants.ACTION_PROP_CONTROL_STATUS, "ACCEPTED");
                    actionProps.put(EcmConstants.ACTION_PROP_REDACTING_STATUS, "ACCEPTED");
                    actionProps.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);

                    NodeBodyCreate actionNodeBodyCreate = new NodeBodyCreate("IMPORT", EcmConstants.ACTION_TYPE_ACTION, actionProps, null);
                    NodeRepresentation newActionNode = nodeProxySession.createNode(newActionFolderNode.getId(), actionNodeBodyCreate, true, null, null, client);

                    //for versioning, this property will be set correctly at the end of logic
                    properties.put(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID, "temp");

                    NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(newRegistryNode.getName(), null, properties, null);
                    newRegistryNode = client.getNodesAPI().updateNodeCall(newRegistryNode.getId(), nodeBodyUpdate).readEntity(NodeRepresentation.class);

                    for (Map.Entry<String, List<RegistryModel>> detailsEntry : model.getDetailsValue().entrySet()) {

                        TreeMap<String, Object> folderProps = convertProperties(model.getFolderProps().get(detailsEntry.getKey()));

                        NodeRepresentation folder = getOrCreateFolderForFiDetails(client, newRegistryNode, detailsEntry.getKey(), folderProps);

                        for (RegistryModel details : detailsEntry.getValue()) {
                            try {
                                TreeMap<String, Object> props = convertProperties(details.getProperties());
                                String status = getDetailFinalStatus(properties, props);
                                props.put(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS, status);
                                props.put(EcmConstants.COMMON_PROP_STATUS, status);
                                props.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);
                                props.put(EcmConstants.NODE_PROP_FI_PARENT_REGISTRY_ACTION_ID, newActionNode.getId());

                                client.getNodesAPI().createNodeCall(folder.getId(), new NodeBodyCreate(UUID.randomUUID().toString(), details.getRegistryType(), props, null), true, null, null);
                            } catch (Throwable t) {

                                System.out.println();
                                System.out.println(detailsEntry.getKey());
                                System.out.println();

                                log.error(t.getMessage(), t);
                                notImportedRows.add(model);
                            }
                        }
                    }
                    properties.put(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID, newActionNode.getId());

                    nodeBodyUpdate = new NodeBodyUpdate(newRegistryNode.getName(), null, properties, null);
                    client.getNodesAPI().updateNodeCall(newRegistryNode.getId(), nodeBodyUpdate).readEntity(NodeRepresentation.class);
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                    notImportedRows.add(model);
                }
            }
            
            log.info("Finished Importing Financial Institution Information");

        } catch (IOException | InvalidFormatException e) {
            log.error(e.getMessage(), e);
        }

        return notImportedRows;
    }

    private String getDetailFinalStatus(TreeMap<String, Object> fiProperties, TreeMap<String, Object> detailProperties) {
        Object res = detailProperties.get(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS);
        if ("CANCELLATION".equals(fiProperties.get(EcmConstants.REGISTRY_PROP_ACTION_TYPE))
                && "INACTIVE".equals(fiProperties.get(EcmConstants.REGISTRY_PROP_LICENSE_STATUS))) {
            res = "CANCELED";
        } else {
            Object finalStatus = detailProperties.get(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS);
            if (finalStatus == null || finalStatus.toString().isEmpty()) {
                res = "ACTIVE";
            }
        }

        return res.toString();
    }

    private TreeMap<String, Object> convertProperties(Map<String, RegistryValueModel> modelProperties) {
        TreeMap<String, Object> properties = new TreeMap<>();

        for (Map.Entry<String, RegistryValueModel> e : modelProperties.entrySet()) {
            properties.put(e.getKey(), e.getValue().getValue());
        }

        return properties;
    }

    private String getFiTypeIdByCode(AlfrescoClient client, String fiTypeCode) {

        //Find Fi Type
        ResultPaging<? extends NodeRepresentation> fiTypes = client.getNodesAPI()
                .listNodeChildrenCall(APIConstants.FOLDER_ROOT, null, null, new OrderByParam(Collections.singletonList("createdAt desc")), null,
                        new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_TYPE_ROOT_FOLDER_PATH_KEY), null, null);

        for (NodeRepresentation fiType : fiTypes.getObjects()) {
            if (fiType.getProperties().get(EcmConstants.PROP_CODE).equals(fiTypeCode)) {
                return fiType.getId();
            }
        }

        return null;
    }

    private NodeRepresentation getOrCreateFolderForFiDetails(AlfrescoClient client, NodeRepresentation parent, String name, TreeMap<String, Object> folderProps) throws NodeException {
        NodeBodyCreate create = new NodeBodyCreate(name, "cm:folder", folderProps, null);
        List<String> aspectNames = new ArrayList<>();
        aspectNames.add("fina:folderConfig");
        create.setAspectNames(aspectNames);
        return nodeProxySession.createNode(parent.getId(), create, true, null, null, client);
    }

}
