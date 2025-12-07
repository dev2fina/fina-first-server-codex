package net.fina.first.ecm.template;

import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.body.RequestFilterQuery;
import net.fina.ecm.alfresco.api.search.body.RequestQuery;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.ecm.util.AlfrescoUtil;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.fi.model.FiDocumentParameterModel;
import net.fina.messages.MessagesUtil;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class TemplateDataMapGenerator {
    private final AlfrescoClient alfrescoClient;
    private final FiDocumentParameterModel parameterModel;
    private Logger log = Logger.getLogger(getClass().getName());
    private final ResourceBundle resourceBundle;

    public TemplateDataMapGenerator(AlfrescoClient alfrescoClient, FiDocumentParameterModel parameterModel) {
        this.alfrescoClient = alfrescoClient;
        this.parameterModel = parameterModel;
        this.resourceBundle = MessagesUtil.loadMessageBundle(parameterModel.getLangCode());
    }

    public Map<String, Object> generateTemplateDataMap(Set<String> templateKeys) {
        return getGeneralDataMap(templateKeys);
    }


    private Map<String, Object> getGeneralDataMap(Set<String> templateKeys) {

        Map<String, Object> result = new HashMap<>();

        if (templateKeys != null && !templateKeys.isEmpty()) {
            NodeRepresentation fiRegistryNode = alfrescoClient.getNodesAPI().getNodeCall(parameterModel.getFiRegistryId());

            // data from web script
            if (FirstUtil.templateWebScriptLocatorEnable()) {
                String webScriptPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.TEMPLATE_WEB_SCRIPT_PATH_KEY);
                result = new HashMap<>(getSafeResponseData(webScriptPath, fiRegistryNode.getId(), String.join(",", templateKeys)));
            }


            // Legacy
            result.put("legalForm", getFiLegalFormDescription(fiRegistryNode));
            result.put("registryName", fiRegistryNode.getProperties().get("fina:fiRegistryName"));
            result.put("identificationNumber", fiRegistryNode.getProperties().get("fina:fiRegistryIdentity"));
            result.put("legalAddress", getFiAddress(fiRegistryNode, Arrays.asList("fina:fiRegistryLegalAddressCity", "fina:fiRegistryLegalAddressAddress")));
            result.put("address", getFiLegalAddress(fiRegistryNode));
        }


        return result;
    }

    private String getFiAddress(NodeRepresentation fiRegistryNode, List<String> addressKeys) {
        StringBuilder address = new StringBuilder();

        addressKeys.forEach(key -> {
            String value = FirstUtil.getValue(fiRegistryNode.getProperties().get(key), String.class);
            address.append(value);
            if (addressKeys.indexOf(key) != addressKeys.size() - 1) {
                address.append(", ");
            }
        });

        return address.toString();

    }

    private NodeRepresentation getFiHeadOfficeBranch(NodeRepresentation registryNode) {
        try {
            String registryCode = FirstUtil.getValue(registryNode.getProperties().get("fina:fiRegistryCode"), String.class);
            String searchStr = "(fina:fiRegistryBranchType:\"HEAD_OFFICE\")";
            RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);
            QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"))
                    .filterQueries(Arrays.asList(
                            new RequestFilterQuery().query("+PATH:'//cm:" + AlfrescoUtil.getISO9075String(registryCode) + "//*'"),
                            new RequestFilterQuery().query("+TYPE:'fina:fiRegistryBranch'")
                    ));


            ResultSetRepresentation<ResultNodeRepresentation> resultSearch = alfrescoClient.getSearchAPI().search(queryBody);

            return resultSearch.getObjects().isEmpty() ? null : resultSearch.getObjects().get(0);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return null;
    }

    private String getFiLegalAddress(NodeRepresentation fiRegistryNode) {
        String address = "";

        NodeRepresentation headOfficeNode = getFiHeadOfficeBranch(fiRegistryNode);

        if (headOfficeNode != null) {
            List<String> addressKeys = Arrays.asList("fina:fiRegistryBranchAddressCity", "fina:fiRegistryBranchAddress");
            List<String> addressList = new ArrayList<>();

            if ("GAP".equals(headOfficeNode.getProperties().get(EcmConstants.BRANCH_PROP_STATUS))
                    || "DECLINED".equals(headOfficeNode.getProperties().get(EcmConstants.BRANCH_PROP_STATUS))) {
                ResultPaging<VersionRepresentation> versionRepresentationResultPaging = alfrescoClient.getVersionAPI().listVersionHistoryCall(headOfficeNode.getId(), null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null);
                String currVersion = FirstUtil.getValue(headOfficeNode.getProperties().get(EcmConstants.BRANCH_PROP_FI_REGISTRY_ACTION_ID), String.class);

                for (VersionRepresentation versionRepresentation : versionRepresentationResultPaging.getObjects()) {
                    if (!currVersion.equals(FirstUtil.getValue(versionRepresentation.getProperties().get(EcmConstants.BRANCH_PROP_FI_REGISTRY_ACTION_ID), String.class))) {
                        addressKeys.forEach(key -> {
                            String value = FirstUtil.getValue(versionRepresentation.getProperties().get(key), String.class);
                            addressList.add(value);
                        });
                        break;
                    }
                }
            } else {
                addressKeys.forEach(key -> {
                    String value = FirstUtil.getValue(headOfficeNode.getProperties().get(key), String.class);
                    addressList.add(value);
                });
            }
            address = String.join(", ", addressList);
        }


        return address;

    }

    private String getFiLegalFormDescription(NodeRepresentation fiRegistryNode) {
        String legalFormCode = (String) fiRegistryNode.getProperties().get("fina:fiRegistryLegalFormType");
        return resourceBundle.getString(legalFormCode);
    }

    private Map<String, Object> getSafeResponseData(String webScriptPath,
                                                    String registryNodeId,
                                                    String templateKeys) {
        try {

            return alfrescoClient.getWebScriptApi().getTemplateData(webScriptPath,
                    registryNodeId,
                    parameterModel.getDocumentType().name(),
                    parameterModel.getBranchId(),
                    String.join(",", templateKeys),
                    Calendar.getInstance().getTimeZone().getID());
        } catch (Throwable t) {
            log.error("Error While Gettinga template data response");
            log.error(t.getMessage(), t);
        }
        return new HashMap<>();
    }

}
