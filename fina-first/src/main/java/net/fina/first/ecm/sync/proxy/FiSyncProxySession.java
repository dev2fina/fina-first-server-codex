package net.fina.first.ecm.sync.proxy;


import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.fis.LicenceStatus;
import net.fina.common.server.fi.sync.model.FiBranchSyncMetaModel;
import net.fina.common.server.fi.sync.model.FiManagementSyncMetaModel;
import net.fina.common.server.fi.sync.model.FiSyncMetaModel;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.proxy.NodeProxySession;
import net.fina.first.ecm.registry.model.FiRegistryLicenseStatus;
import net.fina.first.ecm.registry.model.FiRegistryMetaModel;
import net.fina.first.ecm.registry.model.FiRegistryMetaModelHelper;
import net.fina.first.ecm.sync.model.FiAdministratorSyncModelHelper;
import net.fina.first.ecm.sync.model.FiBranchSyncModelHelper;
import net.fina.first.ecm.sync.util.FiSyncUtil;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Stateless
@Interceptors(FirstRecordingAuditor.class)
public class FiSyncProxySession {
    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EcmClientProxySession ecmClientProxySession;
    @Inject
    private NodeProxySession nodeProxySession;

    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 30)
    public List<FiSyncMetaModel> getSyncFis(String acceptLanguage, List<Long> languageIds, List<String> fiTypeCodes, long managementBodyId) throws FinATypeException {
        List<FiSyncMetaModel> models = new ArrayList<>();
        if (fiTypeCodes != null && !fiTypeCodes.isEmpty()) {

            try {
                String fiRegistryRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ROOT_FOLDER_PATH_KEY);
                NodeRepresentation registryRootNode = nodeProxySession.getNodeById(APIConstants.FOLDER_ROOT, null, fiRegistryRootFolderPath, null);

                List<NodeRepresentation> registryNodes = getFiRegistryNodes(acceptLanguage, registryRootNode.getId());

                if (!registryNodes.isEmpty()) {

                    for (NodeRepresentation registryNode : registryNodes) {
                        Map<String, Object> properties = registryNode.getProperties();
                        String fiTypeCode = FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_TYPE_CODE), String.class);

                        if (fiTypeCodes.contains(fiTypeCode)) {
                            FiRegistryMetaModel registryMetaModel = FiRegistryMetaModelHelper.getModel(registryNode);

                            String phone = null;
                            for (String key : properties.keySet()) {
                                if (key.endsWith("ContactPersonPhone")) {
                                    phone = FirstUtil.getValue(properties.get(key), String.class);
                                    break;
                                }
                            }

                            FiSyncMetaModel model = new FiSyncMetaModel();
                            model.setCode(FiSyncUtil.getFiNaturalCode(registryMetaModel));
                            model.setFiTypeCode(registryMetaModel.getFiTypeCode());
                            model.setIdentificationCode(registryMetaModel.getIdentity());
                            model.setStatus(registryMetaModel.getLicenseStatus() == FiRegistryLicenseStatus.ACTIVE ? LicenceStatus.ACTIVE : LicenceStatus.REVOKED);
                            model.setPhone(phone != null && !phone.trim().isEmpty() ? phone : registryMetaModel.getPhone());
                            model.setEmail(registryMetaModel.getMail());
                            model.setDescriptions(FiSyncUtil.getDescriptionsByLanguages(languageIds, registryMetaModel.getName()));
                            model.setAddressDescriptions(FiSyncUtil.getDescriptionsByLanguages(languageIds, registryMetaModel.getLegalAddress()));
                            model.setRegistrationDate(registryMetaModel.getRegistrationDate());
                            model.setRegionName(registryMetaModel.getLegalAddressCity());
                            model.setLegalActDate(registryMetaModel.getLastLegalActDate());

                            // branches
                            List<FiBranchSyncMetaModel> fiBranchSyncMetaModels = getSyncFiBranches(acceptLanguage, languageIds, registryMetaModel.getId(), null);
                            model.setBranches(fiBranchSyncMetaModels);

                            // management = physical persons from structure/beneficiaries +  authorized persons
                            List<FiManagementSyncMetaModel> managementSyncMetaModels = new ArrayList<>(getSyncAdministrators(acceptLanguage, languageIds, managementBodyId, registryMetaModel.getId(), null));
                            model.setManagement(managementSyncMetaModels);

                            models.add(model);
                        }
                    }
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                throw new FinATypeException(t, FinATypeException.Type.FIS_FIRST_SYNC_FAILED);
            }
        }

        return models;
    }

    private List<FiBranchSyncMetaModel> getSyncFiBranches(String acceptLanguage, List<Long> languageIds, String fiRegistryId, List<String> includeStatuses) {
        List<NodeMetaModel> branchNodes = nodeProxySession.getNodeChildren(acceptLanguage, fiRegistryId, null, null, null, null, null, new IncludeParam(Collections.singletonList("properties")), "Branches", null, null).getList();

        List<FiBranchSyncMetaModel> result = new ArrayList<>();
        if (branchNodes != null && !branchNodes.isEmpty()) {
            for (NodeMetaModel branchNode : branchNodes) {
                Map<String, Object> properties = branchNode.getProperties();
                String branchType = FirstUtil.getValue(properties.get(EcmConstants.BRANCH_PROP_TYPE), String.class);
                if (branchType != null && !branchType.equalsIgnoreCase("HEAD_OFFICE")) {
                    String status = FirstUtil.getValue(properties.get(EcmConstants.COMMON_PROP_STATUS), String.class);
                    if ((includeStatuses != null && !includeStatuses.isEmpty() && includeStatuses.contains(status)) || (includeStatuses == null || includeStatuses.isEmpty())) {
                        result.add(FiBranchSyncModelHelper.getMetaModel(languageIds, properties));
                    }
                }
            }
        }

        return result;
    }

    private List<FiManagementSyncMetaModel> getSyncAdministrators(String acceptLanguage, List<Long> languageIds, long managementBodyId, String fiRegistryId, List<String> includeFinalStatuses) {
        List<NodeMetaModel> administratorNodes = nodeProxySession.getNodeChildren(acceptLanguage, fiRegistryId, null, null, null, null, null, new IncludeParam(Collections.singletonList("properties")), "Authorized Persons", null, null).getList();

        List<FiManagementSyncMetaModel> result = new ArrayList<>();
        if (administratorNodes != null && !administratorNodes.isEmpty()) {
            for (NodeMetaModel administratorNode : administratorNodes) {
                Map<String, Object> properties = administratorNode.getProperties();
                String finalStatus = FirstUtil.getValue(properties.get(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS), String.class);

                if ((includeFinalStatuses != null && !includeFinalStatuses.isEmpty() && includeFinalStatuses.contains(finalStatus)) || (includeFinalStatuses == null || includeFinalStatuses.isEmpty())) {
                    result.add(FiAdministratorSyncModelHelper.getFiManagementSyncMetaModel(languageIds, managementBodyId, administratorNode.getProperties()));
                }
            }
        }

        return result;
    }

    private List<NodeRepresentation> getFiRegistryNodes(String acceptLanguage, String registryRootNodeId) {
        List<NodeRepresentation> result = new ArrayList<>();
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
        ResultPaging<NodeRepresentation> resultSet = client.getNodesAPI().listNodeChildrenCall(registryRootNodeId, 0, Integer.MAX_VALUE, null, "(nodeType='" + EcmConstants.REGISTRY_TYPE_REGISTRY + " INCLUDESUBTYPES')", new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null, null, null);
        if (resultSet != null && resultSet.getObjects() != null) {
            result = new ArrayList<>(resultSet.getObjects());
        }
        return result;
    }

}
