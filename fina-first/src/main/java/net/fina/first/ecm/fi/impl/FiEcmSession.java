package net.fina.first.ecm.fi.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.body.PermissionsBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PathElementRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PermissionElementRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PermissionsInfoRepresentation;
import net.fina.ecm.alfresco.api.search.body.RequestSortDefinition;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.common.exception.WorkflowProcessException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.cache.EcmCacheServiceSingleton;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.complexstructure.ComplexStructureBeneficiaryCalculator;
import net.fina.first.ecm.dictionary.model.ClassPropertyMetaModel;
import net.fina.first.ecm.dictionary.proxy.DictionaryProxySession;
import net.fina.first.ecm.fi.api.FiLocalEcm;
import net.fina.first.ecm.fi.api.FiTypeLocal;
import net.fina.first.ecm.fi.model.FiDocumentParameterModel;
import net.fina.first.ecm.fi.model.FiFinishModel;
import net.fina.first.ecm.fi.model.FiRegistryActionCancelResultMetaModel;
import net.fina.first.ecm.fi.model.FiRegistryFilterModel;
import net.fina.first.ecm.group.proxy.GroupProxySession;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.ExportTemplate;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.ecm.notification.api.NotificationInfoLocal;
import net.fina.first.ecm.people.api.PeopleLocal;
import net.fina.first.ecm.registry.model.*;
import net.fina.first.ecm.registry.proxy.FiRegistryActionQuestionnaireStatusProxySession;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.ecm.tag.model.TagMetaModel;
import net.fina.first.ecm.tag.proxy.TagProxySession;
import net.fina.first.ecm.template.TemplateDataMapGenerator;
import net.fina.first.ecm.template.TemplateKeyExtractor;
import net.fina.first.ecm.template.TemplateProcessor;
import net.fina.first.ecm.version.api.VersionLocal;
import net.fina.first.ecm.version.model.VersionMetaModel;
import net.fina.first.ecm.version.model.VersionRevertBodyMetaModel;
import net.fina.first.ecm.workflow.api.WorkflowLocal;
import net.fina.first.ecm.workflow.model.TaskItemMetaModel;
import net.fina.first.ecm.workflow.model.WorkflowProcessBodyCreateMetaModel;
import net.fina.first.interceptors.FirstRecordingAuditor;
import net.fina.messages.MessagesUtil;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(FiLocalEcm.class)
@Interceptors(FirstRecordingAuditor.class)
public class FiEcmSession implements FiLocalEcm {

    private static final String[] IGNORED_PROPERTIES = {"id", "leaf", "parentId", "checked", EcmConstants.QUESTIONNAIRE_EXTRA_NEW_PROP, EcmConstants.QUESTIONNAIRE_EXTRA_UPDATED_PROP, EcmConstants.QUESTIONNAIRE_EXTRA_REMOVED_PROP};
    private final Logger log = Logger.getLogger(getClass().getName());
    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private NodeLocal nodeProxySession;

    @Inject
    private SearchLocal searchProxySession;

    @Inject
    private PeopleLocal peopleProxySession;

    @Inject
    private FiRegistryActionQuestionnaireStatusProxySession fiRegistryActionQuestionnaireStatusProxySession;

    @Inject
    private WorkflowLocal workflowProxySession;

    @Inject
    private VersionLocal versionProxySession;

    @Inject
    private TagProxySession tagProxySession;

    @Inject
    private GroupProxySession groupProxySession;

    @Inject
    private NotificationInfoLocal notificationInfoLocal;

    @Inject
    private FiTypeLocal fiTypeLocal;

    @Inject
    private DictionaryProxySession dictionaryProxySession;

    @Inject
    private EcmCacheServiceSingleton ecmCacheServiceSingleton;

    @Override
    public PaginatedListWrapper<FiRegistryMetaModel> loadFiRegistry(String acceptLanguage, String query, int page, int start, int limit, FiRegistryFilterModel filter, String sort, String group) {

        List<FiRegistryMetaModel> resultModels = new ArrayList<>();

        String fiRegistryRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ROOT_FOLDER_PATH_KEY);

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        ResultPaging<? extends NodeRepresentation> registryNodes;

        List<SortField> sortFields = FirstUtil.getSortFieldsFromParam(sort);

        if ((query != null && !query.trim().isEmpty()) || filter != null) {

            query = query != null ? query.replaceAll("\"", "") : null;

            boolean isFirstLastNameSearch = false;
            String searchQuery;
            if (filter != null) {
                searchQuery = filterToQuery(filter);
            } else {
                String[] queries = query.split("\\s+");
                if (queries.length == 2) {
                    isFirstLastNameSearch = true;
                    searchQuery = MessageFormat.format(getFiRegistrySearchTemplate(AlfrescoPropConstants.REGISTRY_SEARCH_FIRST_LAST_NAME_TEMPLATE), queries[0], queries[1]);
                } else {
                    searchQuery = MessageFormat.format(getFiRegistrySearchTemplate(AlfrescoPropConstants.REGISTRY_SEARCH_TEMPLATE), query);
                }
            }

            List<RequestSortDefinition> sortDefinitions = getRequestSortDefinitions(sortFields);

            ResultSetRepresentation<ResultNodeRepresentation> resultSearch = searchProxySession.searchAFTS(acceptLanguage, searchQuery, start, limit, sortDefinitions);

            if (isFirstLastNameSearch && resultSearch.getCount() == 0) {
                searchQuery = MessageFormat.format(getFiRegistrySearchTemplate(AlfrescoPropConstants.REGISTRY_SEARCH_TEMPLATE), query);
                resultSearch = searchProxySession.searchAFTS(acceptLanguage, searchQuery, start, limit, sortDefinitions);
            }

            List<NodeRepresentation> searchResult = new ArrayList<>();

            for (ResultNodeRepresentation node : resultSearch.getObjects()) {

                if (node.getNodeType() != null && node.getPath().getName() != null && node.getPath().getName().contains(fiRegistryRootFolderPath)) {
                    //get registry fi node id from path element
                    int pathSplitSize = fiRegistryRootFolderPath.split("/").length + 2;
                    List<PathElementRepresentation> refPathList = node.getPath().getElement().size() > pathSplitSize ? node.getPath().getElement().subList(pathSplitSize - 1, pathSplitSize) : new ArrayList<>();
                    if (!refPathList.isEmpty() && !searchResult.contains(node)) {
                        searchResult.add(client.getNodesAPI().getNodeCall(refPathList.get(0).getId()));
                    } else {
                        searchResult.add(node);
                    }
                }

            }

            registryNodes = new ResultPaging<>(searchResult, resultSearch.getPagination());

        } else {
            List<String> sortParameters = getSortParameters(sortFields);
            registryNodes = client.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT, start, limit, new OrderByParam(sortParameters), null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiRegistryRootFolderPath, null, null);
        }

        SortField groupField = FirstUtil.getSortFieldFromParam(group);
        if (groupField != null) {
            registryNodes = groupResultsBy(acceptLanguage, registryNodes, groupField);
        }

        if (registryNodes != null && registryNodes.getObjects() != null) {

            Set<String> fiRegistryTypes = new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.DATA_TYPE_FI_REGISTRIES_KEY)));

            for (NodeRepresentation registryNode : registryNodes.getObjects()) {
                if (fiRegistryTypes.contains(registryNode.getNodeType())) {
                    FiRegistryMetaModel registryMetaModel = FiRegistryMetaModelHelper.getModel(registryNode);

                    // Set last inspector
                    String lastInspectorId = FirstUtil.getValue(registryNode.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_INSPECTOR_ID), String.class);
                    registryMetaModel.setLastInspector(ecmCacheServiceSingleton.getUser(lastInspectorId));

                    // Set last editor
                    String lastEditorId = FirstUtil.getValue(registryNode.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_EDITOR_ID), String.class);
                    registryMetaModel.setLastEditor(ecmCacheServiceSingleton.getUser(lastEditorId));

                    NodeRepresentation representation = client.getNodesAPI().getNodeCall(registryMetaModel.getLastActionId());
                    registryMetaModel.setControlStatus(getControlStatus(representation));
                    registryMetaModel.setRedactingStatus(getPropertyFromAction(representation, EcmConstants.ACTION_PROP_REDACTING_STATUS));
                    registryMetaModel.setCancellationReason(getPropertyFromAction(representation, EcmConstants.ACTION_PROP_CANCELLATION_REASON));

                    //TODO load with filter when necessary
                   /* NodeMetaModel directorNode = getFiDirector(acceptLanguage, registryNode.getId());
                    if (directorNode != null) {
                        registryMetaModel.setDirectorFullName(getPersonFullName(directorNode));
                    }*/

                    resultModels.add(registryMetaModel);
                }
            }
        }

        PaginatedListWrapper<FiRegistryMetaModel> result = new PaginatedListWrapper<>();
        result.setList(resultModels);
        result.setCurrentPage(page);
        result.setTotalResults(Objects.requireNonNull(registryNodes).getPagination().getTotalItems());

        return result;
    }

    private ResultPaging<? extends NodeRepresentation> groupResultsBy(String acceptLanguage, ResultPaging<? extends NodeRepresentation> paginatedNodes, SortField groupByField) {
        if (paginatedNodes == null) {
            return null;
        }

        ResultPaging<NodeRepresentation> result = new ResultPaging<>();
        List<? extends NodeRepresentation> nodes = paginatedNodes.getObjects();
        List<NodeRepresentation> groupedNodes;

        if (nodes == null || nodes.isEmpty()) {
            groupedNodes = new ArrayList<>();
        } else {
            String groupByProperty = getNodePropertyFromModelField(groupByField.getProperty());
            if (groupByProperty != null) {
                List<ClassPropertyMetaModel> propertyDefinitions = dictionaryProxySession.getClassProperties(acceptLanguage, nodes.get(0).getNodeType().replace(":", "_"));
                boolean isAscending = groupByField.getDirection() == null || groupByField.getDirection().toUpperCase().equals("ASC");

                groupedNodes = nodes.stream().sorted((n1, n2) -> compareByProperties(n1, n2, propertyDefinitions, groupByProperty, isAscending)).collect(Collectors.toList());

                result.setObjects(groupedNodes);
            } else {
                groupedNodes = new ArrayList<>(nodes);
            }
        }

        result.setObjects(groupedNodes);
        result.setPagination(paginatedNodes.getPagination());

        return result;
    }

    private List<RequestSortDefinition> getRequestSortDefinitions(List<SortField> sortFields) {
        List<RequestSortDefinition> res = new ArrayList<>();
        if (sortFields != null && !sortFields.isEmpty()) {
            for (SortField sf : sortFields) {
                String propertyName = getNodePropertyFromModelField(sf.getProperty());
                String direction = sf.getDirection() != null ? sf.getDirection().toUpperCase() : "ASC";
                RequestSortDefinition rsd = new RequestSortDefinition().field(propertyName).ascending(direction.equals("ASC"));
                res.add(rsd);
            }
        }

        return res;
    }

    private int compareByProperties(NodeRepresentation nr1, NodeRepresentation nr2, List<ClassPropertyMetaModel> propertyDefinitions, String property, boolean ascending) {
        Object value1 = nr1.getProperties() != null ? nr1.getProperties().get(property) : null;
        Object value2 = nr2.getProperties() != null ? nr2.getProperties().get(property) : null;

        if (value1 == null && value2 == null) {
            return 0;
        } else if (value1 == null) {
            return ascending ? -1 : 1;
        } else if (value2 == null) {
            return ascending ? 1 : -1;
        }

        String dataType = getDataType(propertyDefinitions, property);

        int comparisonResult;

        switch (dataType) {
            case "d:date":
                String dateStr1 = FirstUtil.getValue(value1, String.class);
                Date d1 = FirstUtil.getDateValue(dateStr1, FirstUtil.DATE_FORMAT_LONG_STRING);

                String dateStr2 = FirstUtil.getValue(value2, String.class);
                Date d2 = FirstUtil.getDateValue(dateStr2, FirstUtil.DATE_FORMAT_LONG_STRING);

                // If either of the dates is null, consider it "less" than any valid date
                if (d1 == null && d2 == null) {
                    comparisonResult = 0;
                } else if (d1 == null) {
                    comparisonResult = ascending ? -1 : 1;
                } else if (d2 == null) {
                    comparisonResult = ascending ? 1 : -1;
                } else {
                    comparisonResult = ascending ? d1.compareTo(d2) : d2.compareTo(d1);
                }
                break;
            case "d:int":
                int intVal1 = Integer.parseInt(value1.toString());
                int intVal2 = Integer.parseInt(value2.toString());
                comparisonResult = ascending ? intVal1 - intVal2 : intVal2 - intVal1;
                break;
            default:
                int stringCmp = value1.toString().compareTo(value2.toString());
                comparisonResult = ascending ? stringCmp : -stringCmp;
                break;
        }

        return comparisonResult;
    }

    private String getDataType(List<ClassPropertyMetaModel> propertyDefinitions, String property) {
        String res = "d:text";
        for (ClassPropertyMetaModel definition : propertyDefinitions) {
            if (definition.getName().equals(property)) {
                res = definition.getDataType();
                break;
            }
        }

        return res;
    }

    private String getNodePropertyFromModelField(String fieldName) {
        String propertyName = null;
        switch (fieldName) {
            case "code":
                propertyName = EcmConstants.REGISTRY_PROP_CODE;
                break;
            case "identity":
                propertyName = EcmConstants.REGISTRY_PROP_IDENTITY;
                break;
            case "name":
                propertyName = EcmConstants.REGISTRY_PROP_NAME;
                break;
            case "lastActionDate":
                propertyName = EcmConstants.REGISTRY_PROP_LAST_ACTION_DATE;
                break;
            case "archivedGapTaskCount":
                propertyName = EcmConstants.REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT;
                break;
            case "fiTypeCode":
                propertyName = EcmConstants.REGISTRY_PROP_TYPE_CODE;
                break;
            case "actionType":
                propertyName = EcmConstants.REGISTRY_PROP_ACTION_TYPE;
                break;
            case "status":
                propertyName = EcmConstants.REGISTRY_PROP_STATUS;
                break;
            case "author":
                propertyName = EcmConstants.REGISTRY_PROP_LAST_EDITOR_FULL_NAME;
                break;
            case "lastInspector":
                propertyName = EcmConstants.REGISTRY_PROP_LAST_INSPECTOR_ID;
                break;
            case "legalFormType":
                propertyName = EcmConstants.REGISTRY_PROP_LEGAL_FORM_TYPE;
                break;
            case "lastLegalActNumber":
                propertyName = EcmConstants.REGISTRY_PROP_ACT_NUMBER;
                break;
            case "lastLegalActDate":
                propertyName = EcmConstants.REGISTRY_PROP_ACT_DATE;
                break;
            case "legalAddressRegion":
                propertyName = EcmConstants.REGISTRY_PROP_LEGAL_ADDRESS_REGION;
                break;
            case "legalAddressCity":
                propertyName = EcmConstants.REGISTRY_PROP_LEGAL_ADDRESS_CITY;
                break;
            case "legalAddress":
                propertyName = EcmConstants.REGISTRY_PROP_LEGAL_ADDRESS;
                break;
            default:
                break;

        }

        return propertyName;
    }

    private String getSortStringFromModel(SortField model) {
        if (model == null) {
            return null;
        }

        String propertyField = model.getProperty();
        if (propertyField == null) {
            return null;
        }
        String property = getNodePropertyFromModelField(propertyField);

        String direction = model.getDirection();
        if (direction == null || direction.trim().isEmpty()) {
            direction = "desc";
        }

        return property + " " + direction;
    }

    private List<String> getSortParameters(List<SortField> sort) {
        List<String> result = new ArrayList<>();

        for (SortField sortModel : sort) {
            String sortString = getSortStringFromModel(sortModel);
            if (sortString != null) {
                result.add(sortString);
            }
        }

        return result;
    }

    private String getPersonFullName(NodeMetaModel personNode) {
        if (personNode == null || personNode.getProperties() == null) {
            return "";
        }

        String firstName = FirstUtil.getValue(personNode.getProperties().get(EcmConstants.FI_PERSON_PROP_FIRSTNAME), String.class);
        String lastName = FirstUtil.getValue(personNode.getProperties().get(EcmConstants.FI_PERSON_PROP_LASTNAME), String.class);

        return firstName != null ? firstName + (lastName != null ? " " + lastName : "") : "";
    }

    private NodeMetaModel getFiDirector(String acceptLanguage, String fiNodeId) {
        try {
            PaginatedListWrapper<NodeMetaModel> nodeModels = nodeProxySession.getNodeChildren(acceptLanguage, fiNodeId, null, Integer.MAX_VALUE, null, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), "Authorized Persons", null, null);
            if (nodeModels != null && nodeModels.getList() != null && nodeModels.getList().size() > 0) {
                Map<String, Object> properties = nodeModels.getList().get(0).getProperties();
                String positionPropName = properties.keySet().stream().filter(item -> item.endsWith("fiAuthorizedPersonPosition")).findAny().orElse(null);
                List<NodeMetaModel> directors = nodeModels.getList().stream().filter(node -> "director".equals(node.getProperties().get(positionPropName))).collect(Collectors.toList());
                if (directors.size() > 0) {
                    return directors.get(0);
                }
            }
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> loadFiRegistryAssociations(String acceptLanguage, String fiRegistryId) {
        ResultPaging<NodeRepresentation> resultNodes = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().listTargetAssociationsCall(fiRegistryId, null, null, new FieldsParam(Arrays.asList("id", "nodeType")));
        List<NodeMetaModel> nodeMetaModels = NodeModelHelper.getMetaModels(resultNodes.getObjects());

        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(nodeMetaModels);
        listWrapper.setTotalResults(resultNodes.getCount());
        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> loadFiDetails(String acceptLanguage, String folderId, String parentId, String query, String filter, int start, int pageSize) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
        String nodeId = parentId != null && !parentId.equals("root") ? parentId : folderId;
        NodeRepresentation folderNode = client.getNodesAPI().getNodeCall(nodeId);

        if (query != null && !query.isEmpty()) {
            return searchFiDetails(nodeId, folderNode, query, filter, start, pageSize);
        }

        if (filter != null && !filter.trim().isEmpty()) {
            return filterFiDetails(client, nodeId, folderNode, filter, start, pageSize);
        }

        IncludeParam includeParam = new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE));
        Integer maxItems = pageSize > 0 ? pageSize : null;
        ResultPaging<NodeRepresentation> nodes = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().listNodeChildrenCall(nodeId, start, maxItems, null, null, includeParam, null, null, null);

        List<NodeMetaModel> filteredNodeMetaModels = new ArrayList<>();

        List<NodeMetaModel> nodeMetaModels = NodeModelHelper.getMetaModels(nodes.getObjects());
        for (NodeMetaModel model : nodeMetaModels) {
            Object visible = model.getProperties() == null ? false : model.getProperties().get("fina:folderConfigVisible");
            if (visible == null || (boolean) visible) {
                filteredNodeMetaModels.add(model);
            }
        }

        sortItemsBySequence(filteredNodeMetaModels);

        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(filteredNodeMetaModels);
        listWrapper.setTotalResults(nodes.getPagination().getTotalItems());

        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> loadFiActions(String acceptLanguage, Integer start, Integer pageSize, String fiRegistryId) {

        String fiRegistryActionFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ACTIONS_FOLDER_NAME_KEY);

        ResultPaging<NodeRepresentation> actions = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().listNodeChildrenCall(fiRegistryId, start, pageSize, new OrderByParam(Collections.singletonList("createdAt desc")), "(nodeType=" + EcmConstants.ACTION_TYPE_ACTION + ")", new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiRegistryActionFolderName, null, null);

        List<NodeMetaModel> nodeMetaModels = NodeModelHelper.getMetaModels(actions.getObjects());

        for (NodeMetaModel nodeMetaModel : nodeMetaModels) {
            if (nodeMetaModel.getProperties().containsKey(EcmConstants.ACTION_PROP_AUTHOR)) {
                String authorId = (String) nodeMetaModel.getProperties().get(EcmConstants.ACTION_PROP_AUTHOR);
                String authorDisplayName = FirstUtil.getPersonDisplayName(ecmClientProxySession.getAlfrescoClient().getPeopleAPI().getPersonById(authorId));
                nodeMetaModel.getProperties().put(EcmConstants.ACTION_PROP_AUTHOR, authorDisplayName);
            }
        }

        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(nodeMetaModels);
        listWrapper.setTotalResults(nodeMetaModels.size());

        return listWrapper;
    }

    @Override
    public NodeMetaModel getFi(String acceptLanguage, String id) {
        NodeRepresentation registryNode = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().getNodeCall(id);
        return NodeModelHelper.getMetaModel(registryNode);
    }

    @Override
    public NodeMetaModel saveProperties(String acceptLanguage, String id, TreeMap<String, Object> properties) throws NodeException {
        for (String prop : IGNORED_PROPERTIES) {
            properties.remove(prop);
        }

        Map<String, Object> dateProperties = new HashMap<>();

        for (Map.Entry<String, Object> e : properties.entrySet()) {
            if (e.getKey().endsWith("Date")) {
                if (e.getValue() != null && !e.getValue().toString().trim().isEmpty()) {
                    Date value = FirstUtil.getDateValue(e.getValue().toString().trim());
                    dateProperties.put(e.getKey(), value != null ? value : e.getValue());
                }
            }
        }

        properties.putAll(dateProperties);

        NodeRepresentation nodeRepresentation = nodeProxySession.updateNode(id, new NodeBodyUpdate(properties), ecmClientProxySession.getAlfrescoClient(acceptLanguage));
        if (nodeRepresentation.getNodeType().equals(EcmConstants.FI_REGISTRY_SMS_TYPE)) {
            sendMessage(nodeRepresentation);
        }

        return NodeModelHelper.getMetaModel(nodeRepresentation);
    }

    private void sendMessage(NodeRepresentation nodeRepresentation) {
        ecmClientProxySession.getAlfrescoClient().getWebScriptApi().sendMessage(nodeRepresentation.getId());
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> saveFiDetail(String acceptLanguage, String id, TreeMap<String, Object> properties) throws NodeException {
        detectAndModifyExtraQuestionnaires(acceptLanguage, id, properties);
        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(Collections.singletonList(saveProperties(acceptLanguage, id, properties)));
        listWrapper.setTotalResults(1);

        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> createFiDetail(String acceptLanguage, String fiRegistryId, String parentId, String nodeType, TreeMap<String, Object> properties) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
        parentId = parentId != null && !parentId.equals("root") ? parentId : fiRegistryId;
        NodeRepresentation parent = client.getNodesAPI().getNodeCall(parentId);

        if (nodeType == null || nodeType.isEmpty()) {
            Object nodeTypeObject = parent.getProperties().get("fina:folderConfigChildType");

            if (nodeTypeObject == null) {
                nodeType = parent.getNodeType();
            } else {
                nodeType = nodeTypeObject.toString();
            }
        }

        List<Map<String, Object>> extraQuestionnaires = new ArrayList<>();

        TreeMap<String, Object> cleanProperties = new TreeMap<>();
        cleanProperties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);

        for (Map.Entry<String, Object> e : properties.entrySet()) {
            if (e.getKey().equalsIgnoreCase(EcmConstants.QUESTIONNAIRE_EXTRA_NEW_PROP)) {
                extraQuestionnaires = (List<Map<String, Object>>) e.getValue();
            } else if (!Arrays.asList(IGNORED_PROPERTIES).contains(e.getKey())) {
                cleanProperties.put(e.getKey().replace("_", ":"), e.getValue());
            }
        }

        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(nodeType.replace(":", "_") + "_" + UUID.randomUUID().toString(), nodeType);
        nodeBodyCreate.setProperties(cleanProperties);

        NodeRepresentation registryNode = nodeProxySession.createNode(parentId, nodeBodyCreate, client);

        if (!extraQuestionnaires.isEmpty()) {
            // extra questionnaire folder
            String extraQuestionnaireFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_SUB_ITEM_EXTRA_QUESTIONNAIRE_FOLDER_NAME_KEY);

            NodeBodyCreate extraQuestionnaireFolderNodeBodyCreate = new NodeBodyCreate(extraQuestionnaireFolderName, "cm:folder");
            NodeRepresentation extraQuestionnaireFolderNode = nodeProxySession.createNode(registryNode.getId(), extraQuestionnaireFolderNodeBodyCreate, client);

            // extra questionnaire items
            fiRegistryActionQuestionnaireStatusProxySession.createFiRegistrySubObjectExtraQuestionnaire(extraQuestionnaireFolderNode.getId(), extraQuestionnaires);
        }

        if ((EcmConstants.FI_REGISTRY_SMS_TYPE).equals(registryNode.getNodeType())) {
            sendMessage(registryNode);
        }

        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(Collections.singletonList(NodeModelHelper.getMetaModel(registryNode)));
        listWrapper.setTotalResults(1);

        return listWrapper;
    }

    @Override
    public void removeFiDetail(String acceptLanguage, String itemId) {
        ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().deleteNodeCall(itemId);
    }

    @Override
    public String getFiRegistryStatus(String acceptLanguage, String fiRegistryId) {
        NodeRepresentation registryNode = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().getNodeCall(fiRegistryId);
        return (String) registryNode.getProperties().get("fina:fiRegistryStatus");
    }

    @Override
    public byte[] getFiExportContent(String acceptLanguage, FiRegistryFilterModel filter) throws Throwable {
        String fiRegistryRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ROOT_FOLDER_PATH_KEY);
        NodeRepresentation rootNode = ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, fiRegistryRootFolderPath, null);

        return nodeProxySession.getExportNodeHierarchyContent(acceptLanguage, ExportTemplate.FI_REGISTRY, rootNode.getId(), null, filter);
    }

    @Override
    public PaginatedListWrapper<ResultNodeRepresentation> getFiRegistryDocument(String processId, String documentType, String actionId) {
        String searchQuery = "select * from " + EcmConstants.DOCUMENT_TYPE_DOCUMENT + " where " + EcmConstants.DOCUMENT_PROP_DOCUMENT_TYPE + "='" + documentType + "' and " + EcmConstants.DOCUMENT_PROP_DOCUMENT_PROCESS_ID + "='" + processId + "' and " + EcmConstants.DOCUMENT_PROP_DOCUMENT_ACTION_ID + "='" + actionId + "'";
        return searchProxySession.searchByCMIS(searchQuery);
    }

    @Override
    public String filterToQuery(FiRegistryFilterModel filter) {
        String currDate = formatDate(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("((");

        if (filter.getIdentity() != null && (!filter.getIdentity().isEmpty())) {
            sb.append(EcmConstants.REGISTRY_PROP_IDENTITY).append(":\"*").append(filter.getIdentity()).append("*\" AND ");
        } else {
            if (filter.getFiRegistryName() != null) {
                sb.append(EcmConstants.REGISTRY_PROP_NAME).append(":\"*").append(filter.getFiRegistryName()).append("*\" AND ");
            }
            if (filter.getFiRegistryCode() != null) {
                sb.append(EcmConstants.REGISTRY_PROP_CODE).append(":\"*").append(filter.getFiRegistryCode()).append("*\" AND ");
            }
            if (filter.getCityName() != null) {
                sb.append(EcmConstants.REGISTRY_PROP_LEGAL_ADDRESS_CITY).append(":\"*").append(filter.getCityName()).append("*\" AND ");
            }
            if (filter.getRegionName() != null) {
                sb.append(EcmConstants.REGISTRY_PROP_LEGAL_ADDRESS_REGION).append(":\"*").append(filter.getRegionName()).append("*\" AND ");
            }
            if (filter.getTaskReceiptDateFrom() != null || filter.getTaskReceiptDateTo() != null) {
                String from = filter.getTaskReceiptDateFrom() != null ? formatDate(filter.getTaskReceiptDateFrom()) : currDate;
                String to = filter.getTaskReceiptDateTo() != null ? formatDate(filter.getTaskReceiptDateTo()) : currDate;
                sb.append(EcmConstants.REGISTRY_PROP_TASK_DATE).append(":[\"").append(from).append("\" TO \"").append(to).append("\"] AND ");
            }
            if (filter.getFiRegistryLegalActDateFrom() != null || filter.getFiRegistryLegalActDateTo() != null) {
                String from = filter.getFiRegistryLegalActDateFrom() != null ? formatDate(filter.getFiRegistryLegalActDateFrom()) : currDate;
                String to = filter.getFiRegistryLegalActDateTo() != null ? formatDate(filter.getFiRegistryLegalActDateTo()) : currDate;
                sb.append(EcmConstants.REGISTRY_PROP_ACT_DATE).append(":[\"").append(from).append("\" TO \"").append(to).append("\"] AND ");
            }
            if (filter.getTypes() != null && (!filter.getTypes().isEmpty())) {

                if (filter.getTypes().size() > 1) {
                    sb.append("(");
                }

                int index = 0;
                for (String fiTypeCode : filter.getTypes()) {
                    if ((index > 0)) {
                        sb.append(" OR ");
                    }
                    sb.append(EcmConstants.REGISTRY_PROP_TYPE_CODE).append(":\"").append(fiTypeCode).append("\"");
                    index++;
                }

                if (filter.getTypes().size() > 1) {
                    sb.append(")");
                }

                sb.append(" AND ");
            }
            if (filter.getAuthor() != null) {
                sb.append(EcmConstants.REGISTRY_PROP_LAST_EDITOR_FULL_NAME).append(":\"*").append(filter.getAuthor()).append("*\" OR  ");
            }
            if (filter.getStatus() != null) {
                String actionType = filter.getStatus().stream().map(el -> EcmConstants.REGISTRY_PROP_STATUS + ":\"" + el + "\"").collect(Collectors.joining(" OR "));

                sb.append(" (").append(actionType).append(") AND ");
            }
            if (filter.getFiRegistryLicenseStatus() != null) {
                sb.append(EcmConstants.REGISTRY_PROP_LICENSE_STATUS).append(":\"").append(filter.getFiRegistryLicenseStatus()).append("\" AND ");
            }
            if (filter.getFiRegistryLastEditorId() != null) {
                sb.append(EcmConstants.REGISTRY_PROP_LAST_EDITOR_ID).append(":\"*").append(filter.getFiRegistryLastEditorId()).append("*\" AND ");
            }
            if (filter.getFiRegistryActionType() != null) {
                String actionType = filter.getFiRegistryActionType().stream().map(el -> EcmConstants.REGISTRY_PROP_ACTION_TYPE + ":\"" + el + "\"").collect(Collectors.joining(" OR "));

                sb.append(" (").append(actionType).append(") AND ");
            }
            if (filter.getFiRegistryLastInspectorId() != null) {
                sb.append(EcmConstants.REGISTRY_PROP_LAST_INSPECTOR_ID).append(":\"*").append(filter.getFiRegistryLastInspectorId()).append("*\" AND ");
            }
            if (filter.getYears() != null && !filter.getYears().isEmpty()) {
                String fiLicenseStatus = filter.getFiRegistryLicenseStatus();
                String datePropertyName = fiLicenseStatus == null || fiLicenseStatus.equalsIgnoreCase("ACTIVE") ? EcmConstants.REGISTRY_PROP_REGISTRATION_DATE : EcmConstants.REGISTRY_PROP_TASK_DATE;
                fiLicenseStatus = fiLicenseStatus != null ? fiLicenseStatus : "";

                sb.append("(");
                for (int i = 0; i < filter.getYears().size(); i++) {
                    String year = filter.getYears().get(i);
                    if (i > 0) {
                        sb.append(" OR ");
                    }

                    //Get first day of the year
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, Integer.parseInt(year));
                    cal.set(Calendar.DAY_OF_YEAR, 1);
                    String from = formatDate(cal.getTime());

                    //Get last day of the year
                    cal.set(Calendar.MONTH, 11); //December
                    cal.set(Calendar.DAY_OF_MONTH, 31);
                    String to = formatDate(cal.getTime());

                    String actionType = null;
                    if (filter.getFiRegistryActionType() != null && filter.getFiRegistryActionType().size() == 1) {
                        actionType = filter.getFiRegistryActionType().get(0);
                    }
                    datePropertyName = actionType != null && actionType.equalsIgnoreCase("CANCELLATION") ? EcmConstants.REGISTRY_PROP_ACT_DATE : datePropertyName;
                    sb.append("(").append(datePropertyName).append(":[\"").append(from).append("\" TO \"").append(to).append("\"]  )");
                    if (fiLicenseStatus.equalsIgnoreCase("INACTIVE")) {
                        if (!(actionType != null && !actionType.equalsIgnoreCase("CANCELLATION"))) {

                            sb.append("OR (").append(EcmConstants.REGISTRY_PROP_ACT_DATE).append(":[\"").append(from).append("\" TO \"").append(to).append("\"] ) ");
                        }
                    }
                }
                sb.append(") AND ");
            }
            if (filter.getRegistrationDateFrom() != null || filter.getRegistrationDateTo() != null) {
                String from = filter.getRegistrationDateFrom() != null ? formatDate(filter.getRegistrationDateFrom()) : currDate;
                String to = filter.getRegistrationDateTo() != null ? formatDate(filter.getRegistrationDateTo()) : currDate;
                sb.append(EcmConstants.REGISTRY_PROP_REGISTRATION_DATE).append(":[\"").append(from).append("\" TO \"").append(to).append("\"] AND ");
            }
        }

        if (sb.length() > 5) {
            sb.delete(sb.length() - 5, sb.length());
        }

        sb.append("))");

        return sb.toString();
    }

    private String formatDate(Date date) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("y-M-d");
        return simpleDateformat.format(date);
    }

    private void detectAndModifyExtraQuestionnaires(String acceptLanguage, String id, TreeMap<String, Object> properties) throws NodeException {
        Object extraQuestionnairesNewObject = properties.get(EcmConstants.QUESTIONNAIRE_EXTRA_NEW_PROP);
        Object extraQuestionnairesUpdatedObject = properties.get(EcmConstants.QUESTIONNAIRE_EXTRA_UPDATED_PROP);
        Object extraQuestionnairesRemovedObject = properties.get(EcmConstants.QUESTIONNAIRE_EXTRA_REMOVED_PROP);

        if (extraQuestionnairesNewObject != null || extraQuestionnairesUpdatedObject != null || extraQuestionnairesRemovedObject != null) {
            AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
            String extraQuestionnaireFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_SUB_ITEM_EXTRA_QUESTIONNAIRE_FOLDER_NAME_KEY);
            List<NodeRepresentation> childrenNodeRepresentations = client.getNodesAPI().listNodeChildrenCall(id).getObjects();
            String extraQuestionnaireFolderId = null;
            if (childrenNodeRepresentations != null && !childrenNodeRepresentations.isEmpty()) {
                for (NodeRepresentation childNodeRepresentation : childrenNodeRepresentations) {
                    if (childNodeRepresentation.getName().equalsIgnoreCase(extraQuestionnaireFolderName)) {
                        extraQuestionnaireFolderId = childNodeRepresentation.getId();
                        break;
                    }
                }
            }

            // create extra questionnaire folder if not exists
            if (extraQuestionnaireFolderId == null) {
                NodeBodyCreate extraQuestionnaireFolderNodeBodyCreate = new NodeBodyCreate(extraQuestionnaireFolderName, "cm:folder");
                extraQuestionnaireFolderId = nodeProxySession.createNode(id, extraQuestionnaireFolderNodeBodyCreate, client).getId();
            }

            // remove extra questionnaire items by id
            if (extraQuestionnairesRemovedObject != null) {
                List<String> extraQuestionnaireIds = (List<String>) extraQuestionnairesRemovedObject;
                for (String extraQuestionnaireId : extraQuestionnaireIds) {
                    client.getNodesAPI().deleteNodeCall(extraQuestionnaireId);
                }
            }

            // create new questionnaire items
            if (extraQuestionnairesNewObject != null) {
                List<Map<String, Object>> newExtraQuestionnaires = (List<Map<String, Object>>) extraQuestionnairesNewObject;
                cleanDuplicatedExtraQuestionnaires(acceptLanguage, newExtraQuestionnaires, extraQuestionnaireFolderId);
                if (!newExtraQuestionnaires.isEmpty()) {
                    fiRegistryActionQuestionnaireStatusProxySession.createFiRegistrySubObjectExtraQuestionnaire(extraQuestionnaireFolderId, newExtraQuestionnaires);
                }
            }

            // update questionnaire items
            if (extraQuestionnairesUpdatedObject != null) {
                fiRegistryActionQuestionnaireStatusProxySession.updateFiRegistrySubObjectExtraQuestionnaire((List<Map<String, Object>>) extraQuestionnairesUpdatedObject);
            }
        }
    }

    @Override
    public void syncGapsFromQuestionnaires(String acceptLanguage, String fiRegistryId, String registryActionId) throws NodeException {
        PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> questionnaires = fiRegistryActionQuestionnaireStatusProxySession.loadQuestionnaireStatusesForAction(acceptLanguage, fiRegistryId, null);
        List<FiRegistryActionQuestionnaireStatusMetaModel> gappedQuestionnaires = questionnaires.getList().stream().filter(q -> "NO".equals(q.getStatus())).collect(Collectors.toList());
        PaginatedListWrapper<NodeMetaModel> gappedNodes = nodeProxySession.getNodeChildren(acceptLanguage, registryActionId, null, null, null, null, null, new IncludeParam(Collections.singletonList("properties")), AlfrescoPropConstants.FI_REGISTRY_GAP_FOLDER_NAME_KEY, null, null);

        String gapObject = "Questionnaire";

        // delete questionnaire based gap nodes
        for (NodeMetaModel gapNode : gappedNodes.getList()) {
            if (gapNode.getProperties().get(EcmConstants.FI_GAP_PROP_OBJECT).equals(gapObject)) {
                nodeProxySession.deleteNodeById(gapNode.getId());
            }
        }

        // create questionnaire based gap nodes
        for (FiRegistryActionQuestionnaireStatusMetaModel q : gappedQuestionnaires) {
            String gapReason = q.getQuestion();
            String gapComment = q.getNote();
            String gapDescription = constructGapDescription(q, fiRegistryId, acceptLanguage);
            if (q.isSubTypeQuestionnaire()) {
                if (q.isPredefined()) { // child object: questionnaire
                    NodeRepresentation questionNode = q.getRelativeNode();
                    String gapQuestionnairePropertyName = q.getQuestionnairePropertyName();
                    createGapObject(gapObject, gapReason, questionNode.getId(), registryActionId, gapQuestionnairePropertyName, null, gapComment, gapDescription);
                } else { // child object: extra questionnaire
                    createGapObject(gapObject, gapReason, q.getId(), registryActionId, EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS, q.getId(), gapComment, gapDescription);
                }
            } else { // predefined questionnaire
                createGapObject(gapObject, gapReason, q.getId(), registryActionId, null, null, gapComment, gapDescription);
            }
        }

    }

    private String getTemplateNameFromScript(AlfrescoClient client, FiDocumentParameterModel parameterModel, String branchId) {
        String templateWebScriptPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.TEMPLATE_WEB_SCRIPT_PATH_KEY);
        return client.getWebScriptApi().getTemplateName(templateWebScriptPath, parameterModel.getFiRegistryId(), parameterModel.getDocumentType().name(), branchId);
    }

    @Override
    public NodeMetaModel generateDocument(FiDocumentParameterModel parameterModel) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        String templateNameConstant = "";

        boolean templateWebScriptEnable = FirstUtil.templateWebScriptLocatorEnable();

        switch (parameterModel.getDocumentType()) {
            case CONFIRMATION_LETTER:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.CONFIRMATION_LETTER_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case DECREE:
            case DECREE_CARD_REFUSAL:
                return generateDecreeDocumentsForBranch(parameterModel);
            case GAP_LETTER:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.GAP_LETTER_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case REFUSAL_LETTER:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.REFUSAL_LETTER_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case REPORT_CARD_REFUSAL:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.REPORT_CARD_REFUSAL_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case REPORT_CARD:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.REPORT_CARD_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case REPORT_CARD_LIQUIDATOR:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.REPORT_CARD_LIQUIDATOR_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case LETTER_LIQUIDATOR:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.LETTER_LIQUIDATOR_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case LETTER_HERALD:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.LETTER_HERALD_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case LETTER_REGISTRY:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.LETTER_REGISTRY_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case REPORT_CARD_WEBSITE_PUBLISH:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.REPORT_CARD_PUBLISH_WEBSITE_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case REPORT_CARD_DOCUMENT_WITHDRAWAL:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.REPORT_CARD_DOCUMENT_WITHDRAWAL_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case DECREE_CARD_DOCUMENT_WITHDRAWAL:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.DECREE_CARD_DOCUMENT_WITHDRAWAL_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case LETTER_TO_THE_REPRESENTATIVE:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.LETTER_TO_THE_REPRESENTATIVE_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            case CANCELED_BRANCH_REPORT_CARD:
                templateNameConstant = !templateWebScriptEnable ? AlfrescoPropConstants.CANCELED_BRANCH_REPORT_CARD_TEMPLATE_PROP_NAME : getTemplateNameFromScript(client, parameterModel, parameterModel.getBranchId());
                break;
            default:
                log.error("Template Type not found : [" + parameterModel.getDocumentType().name() + "]");
                throw new NodeException(new UnexpectedErrorRepresentation(500, "TemplateDocument Type not found"));
        }

        log.info("Template Key : [ " + templateNameConstant + "] & " + "Template Name :[" + AlfrescoConfiguration.get().getAlfrescoProperty(templateNameConstant) + ']');

        NodeRepresentation templateNode = getTemplateByName(client, AlfrescoConfiguration.get().getAlfrescoProperty(templateNameConstant), parameterModel.getActionType().name(), parameterModel.getFiTypeCode());

        byte[] fileContent = getGeneratedDocumentContent(client, templateNode, parameterModel);

        String fileName = templateNode.getName();
        int extensionIndex = templateNode.getName().indexOf(".doc");
        String templateName = templateNode.getName().substring(0, extensionIndex), displayName = null;
        if (parameterModel.getBranchId() != null && !parameterModel.getBranchId().isEmpty()) {
            fileName = templateName + "_" + parameterModel.getBranchId() + ".docx";
            displayName = getDisplayName(templateName, parameterModel.getBranchId(), parameterModel.getLangCode()) + ".docx";
        }

        NodeRepresentation nodeRepresentation = generateCustomDocument(client, parameterModel, fileName, displayName, fileContent);

        return NodeModelHelper.getMetaModel(nodeRepresentation);
    }

    private NodeRepresentation generateCustomDocument(AlfrescoClient client, FiDocumentParameterModel parameterModel, String fileName, String displayName, byte[] content) throws NodeException {

        ResultNodeRepresentation existingNodeRepresentation = checkExistingCustomDocument(parameterModel, fileName, parameterModel.getKeepExistingDocumentType());

        if (existingNodeRepresentation != null) { // regenerate
            InputStream targetStream = new ByteArrayInputStream(content);
            StreamingOutput out = outputStream -> outputStream.write(IOUtils.toByteArray(targetStream));

            Map<String, Object> properties = client.getNodesAPI().updateUploadNodeCall(existingNodeRepresentation.getId(), out, false, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null).getProperties();
            properties.put(EcmConstants.DOCUMENT_PROP_DOCUMENT_IS_LAST_VERSION, true);
            properties.put(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER, parameterModel.getDocumentNumber());
            properties.put(EcmConstants.DOCUMENT_PROP_DOCUMENT_DATE, parameterModel.getDocumentDate());
            properties.put("cm:name", fileName);
            NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(new TreeMap<>(properties));

            return nodeProxySession.updateNode(existingNodeRepresentation.getId(), nodeBodyUpdate, client);
        } else { // create new
            NodeRepresentation registry = client.getNodesAPI().getNodeCall(parameterModel.getFiRegistryId());
            String fiRegistryActionId = FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);
            NodeRepresentation registryAction = client.getNodesAPI().getNodeCall(fiRegistryActionId);
            String actionDocumentsFolderId = FirstUtil.getValue(registryAction.getProperties().get(EcmConstants.ACTION_PROP_DOCUMENTS_FOLDER_ID), String.class);
            MultipartFormDataOutput mdo = getMultipartFormDataForCustomDocument(fileName, displayName, content, null, parameterModel);

            return nodeProxySession.createUploadNode(actionDocumentsFolderId, mdo, true, null, null, client);
        }

    }


    private MultipartFormDataOutput getMultipartFormDataForCustomDocument(String fileName, String displayName, byte[] content, String nodeType, FiDocumentParameterModel parameterModel) {
        MultipartFormDataOutput mdo = new MultipartFormDataOutput();

        InputStream targetStream = new ByteArrayInputStream(content);
        OutputPart objPart = mdo.addFormData("filedata", targetStream, MediaType.TEXT_PLAIN_TYPE);
        objPart.getHeaders().putSingle("Content-Disposition", "form-data; name=" + "filedata" + "; filename=" + fileName);
        mdo.addFormData("name", fileName, MediaType.TEXT_PLAIN_TYPE);

        mdo.addFormData("majorVersion", true, MediaType.TEXT_PLAIN_TYPE);
        mdo.addFormData("nodeType", nodeType == null ? EcmConstants.DOCUMENT_TYPE_DOCUMENT : nodeType, MediaType.TEXT_PLAIN_TYPE);
        mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_PROCESS_ID, parameterModel.getProcessId(), MediaType.TEXT_PLAIN_TYPE);
        mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_ACTION_ID, parameterModel.getFiActionId(), MediaType.TEXT_PLAIN_TYPE);
        mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_TYPE, parameterModel.getDocumentType().name(), MediaType.TEXT_PLAIN_TYPE);
        mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_IS_LAST_VERSION, true, MediaType.TEXT_PLAIN_TYPE);
        if (displayName != null) {
            mdo.addFormData(EcmConstants.DOCUMENT_PROP_DISPLAY_NAME, displayName, MediaType.TEXT_PLAIN_TYPE);
        }
        if (parameterModel.getDocumentDate() != null) {
            mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_DATE, parameterModel.getDocumentDate(), MediaType.TEXT_PLAIN_TYPE);
        }
        if (parameterModel.getDocumentNumber() != null) {
            mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER, parameterModel.getDocumentNumber(), MediaType.TEXT_PLAIN_TYPE);
        }
        if (parameterModel.getBranchId() != null && !parameterModel.getBranchId().trim().isEmpty()) {
            mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_BRANCH_ID, parameterModel.getBranchId(), MediaType.TEXT_PLAIN_TYPE);
        }


        return mdo;
    }

    private ResultNodeRepresentation checkExistingCustomDocument(FiDocumentParameterModel parameterModel, String fileName, boolean checkFileNameOnly) {
        PaginatedListWrapper<ResultNodeRepresentation> existingGapCards = getFiRegistryDocument(parameterModel.getProcessId(), parameterModel.getDocumentType().name(), parameterModel.getFiActionId());

        if (!checkFileNameOnly) {
            switch (parameterModel.getDocumentType()) {
                case CONFIRMATION_LETTER:
                case GAP_LETTER:
                case REPORT_CARD: {
                    return !existingGapCards.getList().isEmpty() ? existingGapCards.getList().get(0) : null;
                }
            }
        }

        if (existingGapCards.getList() != null && !existingGapCards.getList().isEmpty()) {
            for (ResultNodeRepresentation erc : existingGapCards.getList()) {
                if (erc.getName().equalsIgnoreCase(fileName)) {
                    return erc;
                }
            }
        }

        return null;
    }

    private String getControlStatus(NodeRepresentation action) {
        NodeMetaModel actionModel = NodeModelHelper.getMetaModel(action);
        return (String) actionModel.getProperties().get(EcmConstants.ACTION_PROP_CONTROL_STATUS);
    }

    private String getPropertyFromAction(NodeRepresentation action, String property) {
        NodeMetaModel actionModel = NodeModelHelper.getMetaModel(action);
        return (String) actionModel.getProperties().get(property);
    }

    @Override
    public PaginatedListWrapper<NodeRepresentation> loadBranchDecreeDocuments(String fiRegistryId, boolean headOfficeOnly, boolean showActiveBranchesOnly) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        String branchesFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_BRANCHES_FOLDER_NAME_KEY);

        NodeRepresentation registry = client.getNodesAPI().getNodeCall(fiRegistryId);
        String actionId = FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);
        NodeRepresentation registryAction = client.getNodesAPI().getNodeCall(actionId);
        String actionDocumentsFolderId = FirstUtil.getValue(registryAction.getProperties().get(EcmConstants.ACTION_PROP_DOCUMENTS_FOLDER_ID), String.class);

        ResultPaging<NodeRepresentation> branches = client.getNodesAPI().listNodeChildrenCall(fiRegistryId, null, 1000, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), branchesFolderName, null, null);
        //show only active status branches
        if (showActiveBranchesOnly) {
            List<NodeRepresentation> activeBranches = branches.getObjects().stream().filter(item -> "ACTIVE".equals(item.getProperties().get(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS))).collect(Collectors.toList());
            branches.setObjects(activeBranches);
        }

        if (headOfficeOnly) {
            branches.setObjects(branches.getObjects().stream().filter(item -> "HEAD_OFFICE".equals(item.getProperties().get(EcmConstants.BRANCH_PROP_TYPE))).collect(Collectors.toList()));
            branches.getPagination().setTotalItems(1);
        }

        ResultPaging<NodeRepresentation> documents = client.getNodesAPI().listNodeChildrenCall(actionDocumentsFolderId, 0, 1000, null, "(nodeType='" + EcmConstants.DOCUMENT_TYPE_DOCUMENT + "')", new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null, null, null);

        branches.getObjects().forEach(branch -> {
            NodeRepresentation branchDocument = getBranchDocument(branch, documents.getObjects());
            if (branchDocument != null) {
                branch.getProperties().put(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER, branchDocument.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER));
                branch.getProperties().put(EcmConstants.DOCUMENT_PROP_DOCUMENT_DATE, branchDocument.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_DATE));
                branch.getProperties().put("document", branchDocument);

            }
        });
        PaginatedListWrapper<NodeRepresentation> resultPaging = new PaginatedListWrapper<>();
        resultPaging.setList(branches.getObjects());
        resultPaging.setTotalResults(branches.getPagination().getTotalItems());

        return resultPaging;
    }

    private NodeRepresentation getBranchDocument(NodeRepresentation document, List<NodeRepresentation> documents) {

        for (NodeRepresentation n : documents) {
            if (document.getId().equals(n.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_BRANCH_ID))) {
                return n;
            }
        }

        return null;
    }

    private NodeRepresentation getBranchDocumentByBranchIdAndDocumentType(String branchId, FiRegistryDocumentType documentType, List<NodeRepresentation> documents) {
        if (branchId != null && !branchId.trim().isEmpty() && documentType != null) {
            for (NodeRepresentation document : documents) {
                String documentNodeBranchId = FirstUtil.getValue(document.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_BRANCH_ID), String.class);
                String documentNodeType = FirstUtil.getValue(document.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_TYPE), String.class);
                if (branchId.equals(documentNodeBranchId) && documentType.name().equalsIgnoreCase(documentNodeType)) {
                    return document;
                }
            }
        }
        return null;
    }

    @Override
    public NodeMetaModel generateDecreeDocumentsForBranch(FiDocumentParameterModel parameterModel) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();

        NodeRepresentation registry = client.getNodesAPI().getNodeCall(parameterModel.getFiRegistryId());
        String actionId = FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);
        NodeRepresentation registryAction = client.getNodesAPI().getNodeCall(actionId);
        String actionDocumentsFolderId = FirstUtil.getValue(registryAction.getProperties().get(EcmConstants.ACTION_PROP_DOCUMENTS_FOLDER_ID), String.class);
        ResultPaging<NodeRepresentation> documents = client.getNodesAPI().listNodeChildrenCall(actionDocumentsFolderId, 0, 1000, null, "(nodeType='" + EcmConstants.DOCUMENT_TYPE_DOCUMENT + "')", new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null, null, null);

        if (parameterModel.getBranchId() != null) {
            NodeRepresentation branchDocument = getBranchDocumentByBranchIdAndDocumentType(parameterModel.getBranchId(), parameterModel.getDocumentType(), documents.getObjects());
            String docNumber = branchDocument != null ? (String) branchDocument.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER) : null;
            parameterModel.setDocumentNumber(docNumber);
            return NodeModelHelper.getMetaModel(generateBranchDocument(parameterModel));
        }

        String branchesFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_BRANCHES_FOLDER_NAME_KEY);


        ResultPaging<NodeRepresentation> branches = client.getNodesAPI().listNodeChildrenCall(parameterModel.getFiRegistryId(), null, 1000, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), branchesFolderName, null, null);
        if (parameterModel.isFilterActiveBranches()) {
            branches.setObjects(branches.getObjects().stream().filter(item -> "ACTIVE".equals(item.getProperties().get(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS))).collect(Collectors.toList()));
        }
        FiRegistryActionType actionType = FiRegistryActionType.valueOf(registryAction.getProperties().get(EcmConstants.ACTION_PROP_TYPE).toString());
        for (NodeRepresentation branch : branches.getObjects()) {
            NodeRepresentation branchDocument = getBranchDocument(branch, documents.getObjects());
            String docNumber = branchDocument != null ? (String) branchDocument.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER) : null;
            parameterModel.setDocumentNumber(docNumber);
            parameterModel.setBranchId(branch.getId());

            FiRegistryDocumentType documentType;
            if (actionType.equals(FiRegistryActionType.CANCELLATION)) {
                if (branch.getProperties().get(EcmConstants.COMMON_PROP_STATUS).equals("ACTIVE")) {
                    documentType = FiRegistryDocumentType.DECREE;
                    parameterModel.setDocumentType(documentType);
                    generateBranchDocument(parameterModel);
                }
            } else {
                String branchStatus = branch.getProperties().get(EcmConstants.BRANCH_PROP_STATUS) != null ? branch.getProperties().get(EcmConstants.BRANCH_PROP_STATUS).toString() : "";
                if ("GAP".equals(branchStatus)) {
                    if (actionType.equals(FiRegistryActionType.REGISTRATION)) {
                        continue;
                    }
                    documentType = FiRegistryDocumentType.GAP_LETTER;
                } else if ("DECLINED".equals(branchStatus)) {
                    documentType = FiRegistryDocumentType.DECREE_CARD_REFUSAL;
                } else {
                    documentType = FiRegistryDocumentType.DECREE;
                }

                parameterModel.setDocumentType(documentType);
                generateBranchDocument(parameterModel);
            }
        }

        return null;
    }

    private NodeRepresentation generateBranchDocument(FiDocumentParameterModel parameterModel) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();


        String templateNameConstant = "";
        if (FirstUtil.templateWebScriptLocatorEnable()) {
            String templateWebScriptPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.TEMPLATE_WEB_SCRIPT_PATH_KEY);
            templateNameConstant = client.getWebScriptApi().getTemplateName(templateWebScriptPath, parameterModel.getFiRegistryId(), parameterModel.getDocumentType().name(), parameterModel.getBranchId());
        } else {
            switch (parameterModel.getDocumentType()) {
                case DECREE:
                    templateNameConstant = AlfrescoPropConstants.DECREE_TEMPLATE_PROP_NAME;
                    break;
                case GAP_LETTER:
                    templateNameConstant = AlfrescoPropConstants.GAP_LETTER_TEMPLATE_PROP_NAME;
                    break;
                case DECREE_CARD_REFUSAL:
                    templateNameConstant = AlfrescoPropConstants.DECREE_CARD_REFUSAL_TEMPLATE_PROP_NAME;
                    break;
                default:
                    log.error("Template Type not found : [" + parameterModel.getDocumentType().name() + "]");
                    throw new NodeException(new UnexpectedErrorRepresentation(500, "Branch Template Document Type not found"));
            }
        }

        log.info("Template Key : [ " + templateNameConstant + "] & " + "Template Name :[" + AlfrescoConfiguration.get().getAlfrescoProperty(templateNameConstant) + ']');

        NodeRepresentation templateNode = getTemplateByName(client, AlfrescoConfiguration.get().getAlfrescoProperty(templateNameConstant), parameterModel.getActionType().name(), parameterModel.getFiTypeCode());

        byte[] fileContent = getGeneratedDocumentContent(client, templateNode, parameterModel);

        int extensionIndex = templateNode.getName().indexOf(".doc");
        String templateName = templateNode.getName().substring(0, extensionIndex), fileName = templateName + "_" + UUID.randomUUID().toString() + ".docx", displayName = getDisplayName(templateName, parameterModel.getBranchId(), parameterModel.getLangCode()) + ".docx";

        String searchQuery = "select * from " + EcmConstants.DOCUMENT_TYPE_DOCUMENT + " where " + EcmConstants.DOCUMENT_PROP_DOCUMENT_TYPE + "='" + parameterModel.getDocumentType().name() + "' and " + EcmConstants.DOCUMENT_PROP_DOCUMENT_PROCESS_ID + "='" + parameterModel.getProcessId() + "' and " + EcmConstants.DOCUMENT_PROP_DOCUMENT_BRANCH_ID + "='" + parameterModel.getBranchId() + "' and " + EcmConstants.DOCUMENT_PROP_DOCUMENT_ACTION_ID + "='" + parameterModel.getFiActionId() + "'";

        PaginatedListWrapper<ResultNodeRepresentation> result = searchProxySession.searchByCMIS(searchQuery);

        ResultNodeRepresentation existingNodeRepresentation = result.getList().isEmpty() ? null : result.getList().get(0);

        if (existingNodeRepresentation != null) { // regenerate
            InputStream targetStream = new ByteArrayInputStream(fileContent);
            StreamingOutput out = outputStream -> outputStream.write(IOUtils.toByteArray(targetStream));

            Map<String, Object> properties = client.getNodesAPI().updateUploadNodeCall(existingNodeRepresentation.getId(), out, false, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null).getProperties();
            properties.put(EcmConstants.DOCUMENT_PROP_DOCUMENT_IS_LAST_VERSION, true);
            properties.put(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER, parameterModel.getDocumentNumber());
            properties.put("cm:name", fileName);
            if (parameterModel.getDocumentDate() != null && !parameterModel.getDocumentDate().isEmpty()) {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(parameterModel.getDocumentDate());
                    properties.put(EcmConstants.DOCUMENT_PROP_DOCUMENT_DATE, date);
                } catch (ParseException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (parameterModel.getFiActionId() != null) {
                properties.put(EcmConstants.DOCUMENT_PROP_DOCUMENT_ACTION_ID, parameterModel.getFiActionId());
            }
            NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(new TreeMap<>(properties));

            return nodeProxySession.updateNode(existingNodeRepresentation.getId(), nodeBodyUpdate, client);
        } else { // create new
            if (parameterModel.getFiActionId() == null) {
                NodeRepresentation registry = client.getNodesAPI().getNodeCall(parameterModel.getFiRegistryId());
                parameterModel.setFiActionId(FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class));
            }
            NodeRepresentation registryAction = client.getNodesAPI().getNodeCall(parameterModel.getFiActionId());
            String actionDocumentsFolderId = FirstUtil.getValue(registryAction.getProperties().get(EcmConstants.ACTION_PROP_DOCUMENTS_FOLDER_ID), String.class);

            MultipartFormDataOutput mdo = getMultipartFormDataForCustomDocument(fileName, displayName, fileContent, null, parameterModel);
            mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_BRANCH_ID, parameterModel.getBranchId(), MediaType.TEXT_PLAIN_TYPE);
            return nodeProxySession.createUploadNode(actionDocumentsFolderId, mdo, true, null, null, client);
        }

    }

    private String getDisplayName(String templateName, String relatedNodeId, String langCode) {
        if (relatedNodeId != null && !relatedNodeId.isEmpty()) {
            NodeRepresentation nodeRepresentation = nodeProxySession.getNodeById(relatedNodeId, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)).toString(), null, null);

            String nodeDescription = "";
            if (nodeRepresentation != null) {
                String nodeType = nodeRepresentation.getNodeType();
                if (nodeType != null && nodeType.contains("fiRegistryBranch")) {
                    Object branchType = nodeRepresentation.getProperties().get(EcmConstants.BRANCH_PROP_TYPE);
                    if (branchType != null) {
                        Object branchAddressRegion = nodeRepresentation.getProperties().get(EcmConstants.BRANCH_PROP_ADDRESS_REGION);
                        Object branchAddressCity = nodeRepresentation.getProperties().get(EcmConstants.BRANCH_PROP_ADDRESS_CITY);
                        Object branchAddress = nodeRepresentation.getProperties().get(EcmConstants.BRANCH_PROP_ADDRESS_ADDRESS);
                        String branchTypeI18n = MessagesUtil.getString(FirstUtil.getValue(branchType, String.class), langCode);
                        nodeDescription = " (" + branchTypeI18n + " - " + FirstUtil.getValue(branchAddressRegion, String.class) + " " + FirstUtil.getValue(branchAddressCity, String.class) + " " + FirstUtil.getValue(branchAddress, String.class) + ")";
                    }
                } else {
                    Object firstNameObj = nodeRepresentation.getProperties().get(EcmConstants.FI_PERSON_PROP_FIRSTNAME);
                    Object lastNameObj = nodeRepresentation.getProperties().get(EcmConstants.FI_PERSON_PROP_LASTNAME);
                    if (firstNameObj != null && lastNameObj != null) {
                        String firstName = FirstUtil.getValue(firstNameObj, String.class);
                        String lastName = FirstUtil.getValue(lastNameObj, String.class);
                        nodeDescription = " (" + firstName + " " + lastName + ")";
                    }
                }
            }

            templateName = templateName.concat(nodeDescription);
        }

        return templateName;
    }

    private boolean isNodeListContainsNodeId(String nodeId, List<NodeMetaModel> nodeMetaModels) {
        if (nodeId != null && nodeMetaModels != null) {
            for (NodeMetaModel nodeMetaModel : nodeMetaModels) {
                if (nodeMetaModel.getId().equalsIgnoreCase(nodeId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> getFiRegistryDocumentChildrenWithClassProperties(String acceptLanguage, String nodeId, String fiRegistryId, int page, Integer start, Integer limit, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
        List<NodeMetaModel> resultModels = new ArrayList<>(nodeProxySession.getNodeChildrenWithClassProperties(acceptLanguage, nodeId, start, limit, orderBy, where, include, relativePath, includeSource, fields).getList());

        // load workflow documents on a first page of the grid
        if (page == 1 && fiRegistryId != null && !fiRegistryId.trim().isEmpty()) {
            NodeRepresentation nodeRepresentation = client.getNodesAPI().getNodeCall(nodeId);
            if (nodeRepresentation.getParentId().equalsIgnoreCase(fiRegistryId)) {
                List<NodeRepresentation> actionNodes = client.getNodesAPI().listNodeChildrenCall(fiRegistryId, null, null, new OrderByParam(Collections.singletonList("createdAt desc")), null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), "Actions", null, null).getObjects();
                for (NodeRepresentation actionNode : actionNodes) {
                    String processId = FirstUtil.getValue(actionNode.getProperties().get("fina:fiRegistryActionProcessId"), String.class);
                    List<TaskItemMetaModel> taskItemMetaModels = workflowProxySession.getProcessItems(processId, null, null).getList();
                    if (taskItemMetaModels != null && !taskItemMetaModels.isEmpty()) {
                        for (TaskItemMetaModel taskItemMetaModel : taskItemMetaModels) {
                            String taskItemId = taskItemMetaModel.getId();
                            if (!isNodeListContainsNodeId(taskItemId, resultModels)) {
                                resultModels.add(nodeProxySession.getFullNodeById(acceptLanguage, taskItemMetaModel.getId(), null));
                            }
                        }
                    }
                }
            }
        }

        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(resultModels.size());
        return listWrapper;
    }


    @Override
    public PaginatedListWrapper<NodeMetaModel> filterFiDetails(AlfrescoClient client, String folderId, NodeRepresentation folderNode, String filterJson, int start, int pageSize) {
        String nodeType = folderNode.getProperties().get("fina:folderConfigChildType") != null ? (String) folderNode.getProperties().get("fina:folderConfigChildType") : EcmConstants.REGISTRY_PERSON_TYPE;

        PaginatedListWrapper<ResultNodeRepresentation> nodes = searchProxySession.getFilteredNodes(folderId, nodeType, filterJson);
        List<NodeMetaModel> filteredNodeMetaModels = new ArrayList<>();

        List<NodeMetaModel> nodeMetaModels = NodeModelHelper.getMetaModels(nodes.getList());
        for (NodeMetaModel model : nodeMetaModels) {
            Object visible = model.getProperties() == null ? false : model.getProperties().get("fina:folderConfigVisible");
            if (visible == null || (boolean) visible) {
                filteredNodeMetaModels.add(model);
            }
        }

        sortItemsBySequence(filteredNodeMetaModels);

        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setTotalResults(filteredNodeMetaModels.size());
        if (pageSize > 0) {
            listWrapper.setPageSize(pageSize);
            listWrapper.setCurrentPage(start / pageSize + 1);
            filteredNodeMetaModels = filteredNodeMetaModels.subList(start, Math.min(start + pageSize, filteredNodeMetaModels.size()));
        }
        listWrapper.setList(filteredNodeMetaModels);

        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> loadComplexStructureBeneficiaries(String fiRegistryId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();

        List<NodeMetaModel> beneficiaries = NodeModelHelper.getMetaModels(ComplexStructureBeneficiaryCalculator.getInstance().getMainBeneficiaries(fiRegistryId, client));
        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(beneficiaries);
        listWrapper.setTotalResults(beneficiaries.size());

        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<NodeRepresentation> loadActionLiquidatorObjects(String registryActionId, int start, int limit) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        ResultPaging<NodeRepresentation> liquidators = client.getNodesAPI().listNodeChildrenCall(registryActionId, start, limit, new OrderByParam(Collections.singletonList("modifiedAt")), null, new IncludeParam(Collections.singletonList("properties")), "Liquidators", null, null);

        liquidators.getObjects().forEach(n -> {
            String liquidatorId = FirstUtil.getValue(n.getProperties().get(EcmConstants.FI_LIQUIDATOR_PROP_REFERENCE_ID), String.class);
            String reportCardDocumentId = FirstUtil.getValue(n.getProperties().get(EcmConstants.FI_LIQUIDATOR_PROP_REPORT_CARD_REFERENCE_ID), String.class);
            String letterDocumentId = FirstUtil.getValue(n.getProperties().get("fina:fiActionLiquidatorLetterReferenceId"), String.class);

            if (liquidatorId != null && !liquidatorId.trim().isEmpty()) {
                n.getProperties().put(EcmConstants.REGISTRY_PERSON_TYPE, client.getNodesAPI().getNodeCall(liquidatorId));
            }

            if (reportCardDocumentId != null && !reportCardDocumentId.trim().isEmpty()) {
                NodeRepresentation document = findDocumentById(reportCardDocumentId);
                if (document != null) {
                    n.getProperties().put(EcmConstants.FI_LIQUIDATOR_PROP_REPORT_CARD_DOCUMENT, document);
                }
            }

            if (letterDocumentId != null && !letterDocumentId.trim().isEmpty()) {
                NodeRepresentation document = findDocumentById(letterDocumentId);
                if (document != null) {
                    n.getProperties().put("fina:fiLiquidatorLetterDocument", document);
                }
            }
        });

        PaginatedListWrapper<NodeRepresentation> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(liquidators.getObjects());
        listWrapper.setTotalResults(liquidators.getPagination().getTotalItems());

        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<NodeRepresentation> loadBranchChangedObjects(String registryActionId, String relativePath, int start, int limit) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        ResultPaging<NodeRepresentation> changes = client.getNodesAPI().listNodeChildrenCall(registryActionId, start, limit, new OrderByParam(Collections.singletonList("createdAt")), null, new IncludeParam(Collections.singletonList("properties")), relativePath, null, null);

        changes.getObjects().forEach(n -> {
            Object branchId = n.getProperties().get(EcmConstants.FI_BRANCH_REFERENCE_ID);
            Object documentId = n.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_DOCUMENT_REFERENCE_ID);
            Object decreeDocumentId = n.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_DECREE_DOCUMENT_REFERENCE_ID);
            Object refusalLetterDocumentId = n.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_REFUSAL_LETTER_DOCUMENT_REFERENCE_ID);
            if (branchId != null) {
                n.getProperties().put(EcmConstants.BRANCH_TYPE_BRANCH, client.getNodesAPI().getNodeCall((String) branchId));
            }
            if (documentId != null) {
                NodeRepresentation document = findDocumentById((String) documentId);
                if (document != null) {
                    n.getProperties().put(EcmConstants.DOCUMENT_TYPE_DOCUMENT, document);
                }
            }
            if (decreeDocumentId != null) {
                NodeRepresentation document = findDocumentById((String) decreeDocumentId);
                if (document != null) {
                    n.getProperties().put(EcmConstants.DECREE_DOCUMENT_TYPE, document);
                }
            }
            if (refusalLetterDocumentId != null) {
                NodeRepresentation document = findDocumentById((String) refusalLetterDocumentId);
                if (document != null) {
                    n.getProperties().put(EcmConstants.REFUSAL_LETTER_DOCUMENT_TYPE, document);
                }
            }
        });

        PaginatedListWrapper<NodeRepresentation> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(changes.getObjects());
        listWrapper.setTotalResults(changes.getPagination().getTotalItems());

        return listWrapper;
    }

    private NodeRepresentation findDocumentById(String id) {
        String searchQuery = "select * from " + EcmConstants.DOCUMENT_TYPE_DOCUMENT + " where  cmis:objectId='" + id + "'";
        PaginatedListWrapper<ResultNodeRepresentation> result = searchProxySession.searchByCMIS(searchQuery);

        return result.getList().isEmpty() ? null : result.getList().get(0);
    }


    private byte[] getGeneratedDocumentContent(AlfrescoClient client, NodeRepresentation templateNode, FiDocumentParameterModel parameterModel) {
        if (templateNode != null) {
            Response contentResponse = client.getNodesAPI().getNodeContent(templateNode.getId(), true);

            try {
                InputStream inputStream = contentResponse.readEntity(InputStream.class);
                byte[] templateContent = IOUtils.toByteArray(inputStream);

                TemplateProcessor processor = new TemplateProcessor();
                Set<String> templateKeysParsed = new TemplateKeyExtractor().getKeysFromDocument(templateContent);

                Map<String, Object> data = new TemplateDataMapGenerator(client, parameterModel).generateTemplateDataMap(templateKeysParsed);

                return processor.process(templateContent, data, null);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return null;
    }


    private NodeRepresentation getTemplateByName(AlfrescoClient client, String templateName, String actionName, String fiTypeCode) {
        String relativePath = "fina2first/Templates/" + fiTypeCode + "/" + actionName;
        ResultPaging<NodeRepresentation> templatesResultPaging = client.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT, null, null, null, null, null, relativePath, null, null);

        for (NodeRepresentation n : templatesResultPaging.getObjects()) {
            if (n.getName().equals(templateName)) {
                return n;
            }
        }

        return null;
    }

    @Override
    public FiRegistryActionCancelResultMetaModel cancelOrRevertCurrentFiRegistryAction(String acceptLanguage, String fiRegistryId, boolean isCancellation) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        // fi registry node versions
        List<VersionMetaModel> fiRegistryVersionModels = versionProxySession.getNodeVersions(acceptLanguage, fiRegistryId).getList();
        VersionMetaModel fiRegistryCurrentVersion = fiRegistryVersionModels.get(0);

        String currentActionId = (String) fiRegistryCurrentVersion.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID);
        FiRegistryActionType currentActionType = FiRegistryActionType.valueOf((String) fiRegistryCurrentVersion.getProperties().get(EcmConstants.REGISTRY_PROP_ACTION_TYPE));

        // first previous version before action change
        VersionMetaModel fiRegistryRevertToVersion = versionProxySession.getVersionBeforePropertyValueChange(fiRegistryVersionModels, EcmConstants.REGISTRY_PROP_LAST_ACTION_ID, currentActionId);

        FiRegistryActionCancelResultMetaModel result = new FiRegistryActionCancelResultMetaModel(fiRegistryId);
        result.setFiRegistryId(fiRegistryId);
        result.setActionType(currentActionType);
        notificationInfoLocal.deleteNotificationsForAction(currentActionId, null);

        switch (currentActionType) {
            case REGISTRATION: // delete FI registry node
                fiRegistryRevertToVersion = null;
                nodeProxySession.deleteNodeHierarchy(acceptLanguage, fiRegistryId, true);
                break;
            case CHANGE: // change type 1: complex structure (disable, add); administrators (disable, add); change type 2: fi registry node;
                NodeMetaModel actionNodeMetaModel = nodeProxySession.getFullNodeById(acceptLanguage, currentActionId, null);
                String changeType = FirstUtil.getValue(actionNodeMetaModel.getProperties().get(EcmConstants.FI_MANAGEMENT_CHANGE_PROP_CHANGE_FORM_TYPE), String.class);
                if (changeType.equalsIgnoreCase("managementPersonal")) {
                    List<NodeMetaModel> performedChanges = nodeProxySession.getNodeChildren(acceptLanguage, currentActionId, null, null, null, null, "(nodeType=" + EcmConstants.FI_MANAGEMENT_CHANGE_TYPE + ")", new IncludeParam(Collections.singletonList("properties")), "Performed Changes", null, null).getList();
                    if (performedChanges != null && !performedChanges.isEmpty()) {
                        for (NodeMetaModel performedChange : performedChanges) {
                            String performedChangeNodeId = FirstUtil.getValue(performedChange.getProperties().get(EcmConstants.FI_MANAGEMENT_CHANGE_PROP_CHANGE_REF_ID), String.class);
                            String performedChangeNodeStatus = FirstUtil.getValue(performedChange.getProperties().get(EcmConstants.FI_MANAGEMENT_CHANGE_PROP_CHANGE_STATUS), String.class);

                            if (!isCancellation && performedChangeNodeStatus.equalsIgnoreCase("ADDED")) {
                                TreeMap<String, Object> properties = new TreeMap<>();
                                properties.put(EcmConstants.COMMON_PROP_STATUS, "CANCELED");
                                properties.put(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS, "DECLINED");

                                nodeProxySession.updateNode(performedChangeNodeId, new NodeBodyUpdate(properties), null);
                            } else {
                                cancelChangeNode(acceptLanguage, performedChangeNodeStatus, performedChangeNodeId, currentActionType, currentActionId);
                            }

                        }
                    }
                } else if (changeType.equalsIgnoreCase("organizationalForm") && !isCancellation) {
                    TreeMap<String, Object> properties = new TreeMap<>();
                    properties.put(EcmConstants.REGISTRY_PROP_NAME, FirstUtil.getValue(fiRegistryRevertToVersion.getProperties().get(EcmConstants.REGISTRY_PROP_NAME), String.class));
                    properties.put(EcmConstants.REGISTRY_PROP_LEGAL_FORM_TYPE, FirstUtil.getValue(fiRegistryRevertToVersion.getProperties().get(EcmConstants.REGISTRY_PROP_LEGAL_FORM_TYPE), String.class));

                    nodeProxySession.updateNode(fiRegistryId, new NodeBodyUpdate(properties), null);
                }
                break;
            case BRANCHES_CHANGE:
                List<NodeMetaModel> performedChanges = nodeProxySession.getNodeChildren(acceptLanguage, currentActionId, null, null, null, null, "(nodeType=" + EcmConstants.FI_BRANCH_CHANGE_TYPE + ")", new IncludeParam(Collections.singletonList("properties")), "Performed Changes", null, null).getList();
                if (performedChanges != null && !performedChanges.isEmpty()) {
                    for (NodeMetaModel performedChange : performedChanges) {
                        String branchNodeId = FirstUtil.getValue(performedChange.getProperties().get(EcmConstants.FI_BRANCH_REFERENCE_ID), String.class);
                        String branchNodeStatus = FirstUtil.getValue(performedChange.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_STATUS), String.class);

                        cancelChangeNode(acceptLanguage, branchNodeStatus, branchNodeId, currentActionType, currentActionId);
                    }
                }
                break;
            case BRANCHES_EDIT:
                List<NodeMetaModel> performedEdits = nodeProxySession.getNodeChildren(acceptLanguage, currentActionId, null, null, null, null, "(nodeType=" + EcmConstants.FI_BRANCH_CHANGE_TYPE + ")", new IncludeParam(Collections.singletonList("properties")), "Performed Changes", null, null).getList();
                if (performedEdits != null && !performedEdits.isEmpty()) {
                    for (NodeMetaModel performedEdit : performedEdits) {
                        String branchNodeId = FirstUtil.getValue(performedEdit.getProperties().get(EcmConstants.FI_BRANCH_REFERENCE_ID), String.class);

                        List<VersionMetaModel> branchNodeVersionModels = versionProxySession.getNodeVersions(acceptLanguage, branchNodeId).getList();
                        VersionMetaModel branchNodeRevertToVersion = versionProxySession.getVersionBeforePropertyValueChange(branchNodeVersionModels, EcmConstants.BRANCH_PROP_FI_REGISTRY_ACTION_ID, currentActionId);
                        if (branchNodeRevertToVersion == null) {
                            branchNodeRevertToVersion = branchNodeVersionModels.get(branchNodeVersionModels.size() - 1);
                        }
                        VersionRevertBodyMetaModel revertBodyMetaModel = new VersionRevertBodyMetaModel("Revert to version: " + branchNodeRevertToVersion.getId() + ", from version: " + branchNodeVersionModels.get(0).getId() + ", because of " + currentActionType.name() + " action cancellation.", true);
                        versionProxySession.revertToVersion(acceptLanguage, branchNodeId, branchNodeRevertToVersion.getId(), revertBodyMetaModel);
                    }
                }
                break;
            case CANCELLATION:
                NodeMetaModel currentActionNode = nodeProxySession.getFullNodeById(acceptLanguage, currentActionId, null);
                boolean isLiquidatorRequired = FirstUtil.getValue(currentActionNode.getProperties().get(EcmConstants.ACTION_PROP_CANCELLATION_IS_LIQUIDATOR_REQUIRED), Boolean.class);
                if (isLiquidatorRequired) { // check liquidators
                    List<NodeMetaModel> cancellationLiquidators = nodeProxySession.getNodeChildren(acceptLanguage, currentActionId, null, null, null, null, "(nodeType=" + EcmConstants.FI_ACTION_LIQUIDATOR_TYPE + ")", new IncludeParam(Collections.singletonList("properties")), "Liquidators", null, null).getList();
                    if (cancellationLiquidators != null && !cancellationLiquidators.isEmpty()) {
                        for (NodeMetaModel cancellationLiquidator : cancellationLiquidators) {
                            String cancellationLiquidatorType = FirstUtil.getValue(cancellationLiquidator.getProperties().get(EcmConstants.FI_ACTION_LIQUIDATOR_PROP_LIQUIDATOR_TYPE), String.class);
                            if (cancellationLiquidatorType.equalsIgnoreCase("ADDED")) {
                                String authorizedPersonId = FirstUtil.getValue(cancellationLiquidator.getProperties().get(EcmConstants.FI_ACTION_LIQUIDATOR_PROP_LIQUIDATOR_ID), String.class);
                                cancelChangeNode(acceptLanguage, cancellationLiquidatorType, authorizedPersonId, currentActionType, currentActionId);
                            }
                        }
                    }
                }
                break;
            case DOCUMENT_WITHDRAWAL:
            default:
                break;
        }

        // revert fi registry node to specified version
        if (isCancellation && fiRegistryRevertToVersion != null) {
            VersionRevertBodyMetaModel revertBodyMetaModel = new VersionRevertBodyMetaModel("Revert to version: " + fiRegistryRevertToVersion.getId() + ", from version: " + fiRegistryCurrentVersion.getId() + ", because of " + currentActionType.name() + " action cancellation.", true);
            result.setFiRegistryRevertToVersion(versionProxySession.revertToVersion(acceptLanguage, fiRegistryId, fiRegistryRevertToVersion.getId(), revertBodyMetaModel));
        }

        // delete current process
        if (isCancellation) {
            try {
                String currentProcessId = FirstUtil.getValue(fiRegistryCurrentVersion.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_PROCESS_ID), String.class);
                if (currentProcessId != null && !currentProcessId.trim().isEmpty()) {
                    workflowProxySession.deleteWorkflowProcess(currentProcessId);
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }

        // delete gap detail object if exists
        if (fiRegistryRevertToVersion != null) {
            int fiRegistryArchivedGapTaskCount = FirstUtil.getValue(fiRegistryCurrentVersion.getProperties().get(EcmConstants.REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT), Integer.class);

            String fiRegistryRevertToVersionStatus = FirstUtil.getValue(fiRegistryRevertToVersion.getProperties().get(EcmConstants.REGISTRY_PROP_STATUS), String.class);
            String fiRegistryRevertToVersionActionType = FirstUtil.getValue(fiRegistryRevertToVersion.getProperties().get(EcmConstants.REGISTRY_PROP_ACTION_TYPE), String.class);
            String fiRegistryRevertToActionId = FirstUtil.getValue(fiRegistryRevertToVersion.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);
            NodeMetaModel fiRegistryRevertToVersionAction = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryRevertToActionId, null);
            int fiRegistryRevertToVersionActionStep = FirstUtil.getValue(fiRegistryRevertToVersionAction.getProperties().get(EcmConstants.ACTION_PROP_STEP), Integer.class);

            if (fiRegistryRevertToVersionStatus.equalsIgnoreCase("GAP") || (fiRegistryRevertToVersionActionStep == 9 && (fiRegistryRevertToVersionActionType.equalsIgnoreCase(FiRegistryActionType.BRANCHES_CHANGE.name()) || fiRegistryRevertToVersionActionType.equalsIgnoreCase(FiRegistryActionType.BRANCHES_EDIT.name())))) {
                String searchQuery = "select * from " + EcmConstants.FI_REGISTRY_GAP_DETAIL_TYPE + " where " + EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_ACTION_ID + "='" + fiRegistryRevertToActionId + "' and " + EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_ACTIVE + "=true";
                List<ResultNodeRepresentation> gapDetailObjects = searchProxySession.searchByCMIS(searchQuery).getList();
                if (gapDetailObjects != null && !gapDetailObjects.isEmpty()) {
                    for (ResultNodeRepresentation gapDetailObject : gapDetailObjects) {
                        TreeMap<String, Object> fiRegistryGapDetailUpdateProperties = new TreeMap<>();
                        fiRegistryGapDetailUpdateProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_ACTIVE, false);

                        NodeBodyUpdate fiRegistryGapDetailNodeBodyUpdate = new NodeBodyUpdate(fiRegistryGapDetailUpdateProperties);
                        nodeProxySession.updateNode(gapDetailObject.getId(), fiRegistryGapDetailNodeBodyUpdate, null);

                        fiRegistryArchivedGapTaskCount--;
                    }
                }
            }

            TreeMap<String, Object> fiRegistryUpdateProperties = new TreeMap<>();
            fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT, Math.max(fiRegistryArchivedGapTaskCount, 0));

            NodeBodyUpdate fiRegistryNodeBodyUpdate = new NodeBodyUpdate(fiRegistryUpdateProperties);
            nodeProxySession.updateNode(fiRegistryId, fiRegistryNodeBodyUpdate, null);
        }

        return result;
    }

    @Override
    public void finishBranchChangeAction(String acceptLanguage, String registryActionId, String relativePath, String gapCorrectionDeadline, String gapCorrectionDeadlineDays) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
        List<NodeRepresentation> changes = client.getNodesAPI().listNodeChildrenCall(registryActionId, 0, null, new OrderByParam(Collections.singletonList("modifiedAt")), null, new IncludeParam(Collections.singletonList("properties")), relativePath, null, null).getObjects();
        NodeRepresentation action = client.getNodesAPI().getNodeCall(registryActionId);

        for (NodeRepresentation change : changes) {
            String branchId = FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_REFERENCE_ID), String.class);
            String changeStatus = FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_FINAL_STATUS), String.class);

            if (changeStatus != null) {
                switch (changeStatus) {
                    case "ACCEPTED": {
                        // change branch status and delete branchChange object
                        NodeRepresentation branch = client.getNodesAPI().getNodeCall(branchId);
                        String currentBranchStatus = FirstUtil.getValue(branch.getProperties().get(EcmConstants.COMMON_PROP_STATUS), String.class);

                        TreeMap<String, Object> properties = new TreeMap<>();
                        properties.put(EcmConstants.BRANCH_PROP_STATUS, "");
                        properties.put(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS, "CANCELED".equals(currentBranchStatus) ? "CANCELED" : "ACTIVE");
                        properties.put(EcmConstants.COMMON_PROP_STATUS, "CANCELED".equals(currentBranchStatus) ? "CANCELED" : "ACTIVE");

                        if ("ADDED".equals(FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_STATUS), String.class)) || "CANCELED".equals(currentBranchStatus)) {
                            String processId = FirstUtil.getValue(action.getProperties().get(EcmConstants.ACTION_PROP_PROCESS_ID), String.class);
                            PaginatedListWrapper<ResultNodeRepresentation> decreeCards = getFiRegistryDocument(processId, FiRegistryDocumentType.DECREE.name(), registryActionId);

                            for (ResultNodeRepresentation decree : decreeCards.getList()) {
                                if (branch.getId().equals(FirstUtil.getValue(decree.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_BRANCH_ID), String.class))) {
                                    properties.put(EcmConstants.BRANCH_PROP_LEGAL_ACT_NUMBER, FirstUtil.getValue(decree.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER), String.class));
                                    properties.put(EcmConstants.BRANCH_PROP_LEGAL_ACT_DATE, FirstUtil.getValue(decree.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_DATE), String.class));
                                    break;
                                }
                            }
                        }
                        client.getNodesAPI().updateNodeCall(branchId, new NodeBodyUpdate(properties));

                        client.getNodesAPI().deleteNodeCall(change.getId());
                        break;
                    }
                    case "DECLINED": {
                        // change branch status and delete branchChange object
                        String currentBranchStatus = FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_STATUS), String.class);

                        if (currentBranchStatus != null) {
                            switch (currentBranchStatus) {
                                case "ADDED": {
                                    TreeMap<String, Object> properties = new TreeMap<>();
                                    properties.put(EcmConstants.BRANCH_PROP_STATUS, "DECLINED");
                                    properties.put(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS, "CANCELED");
                                    properties.put(EcmConstants.COMMON_PROP_STATUS, "CANCELED");
                                    client.getNodesAPI().updateNodeCall(branchId, new NodeBodyUpdate(properties));
                                    break;
                                }
                                case "CANCELED": {
                                    TreeMap<String, Object> properties = new TreeMap<>();
                                    properties.put(EcmConstants.BRANCH_PROP_STATUS, "DECLINED");
                                    properties.put(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS, "ACTIVE");
                                    properties.put(EcmConstants.COMMON_PROP_STATUS, "ACTIVE");
                                    client.getNodesAPI().updateNodeCall(branchId, new NodeBodyUpdate(properties));
                                    break;
                                }
                                case "CHANGED": {
                                    // restore pre-change version
                                    String preChangeBranchVersion = FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_PRE_CHANGE_VERSION), String.class);
                                    if (preChangeBranchVersion != null) {
                                        VersionRevertBodyMetaModel revertBodyMetaModel = new VersionRevertBodyMetaModel("Revert to version: " + preChangeBranchVersion + " because of branch edit refusal", false);
                                        versionProxySession.revertToVersion("*", branchId, preChangeBranchVersion, revertBodyMetaModel);
                                    }
                                    break;
                                }
                                default:
                                    break;
                            }
                        }

                        client.getNodesAPI().deleteNodeCall(change.getId());
                        break;
                    }
                    case "GAP": {
                        String fiRegistryId = nodeProxySession.getNodeById(action.getParentId(), null, null).getParentId();
                        List<FiRegistryActionQuestionnaireStatusMetaModel> branchQuestionnaires = fiRegistryActionQuestionnaireStatusProxySession.getNodeQuestionnaires(client, branchId, fiRegistryId, registryActionId);
                        String nonQuestionnaireBasedGapReason = FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_FINAL_STATUS_NOTE), String.class);

                        // create gap  objects
                        createFiRegistryBranchGapObjectsBasedOnQuestionnaire(branchQuestionnaires, branchId, registryActionId, nonQuestionnaireBasedGapReason);

                        // set gap correction dates
                        String docId = FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_DOCUMENT_REFERENCE_ID), String.class);
                        TreeMap<String, Object> props = new TreeMap<>();
                        props.put(EcmConstants.DOCUMENT_PROP_CORRECTION_DEADLINE, gapCorrectionDeadline);
                        props.put(EcmConstants.DOCUMENT_PROP_CORRECTION_DEADLINE_DAYS, gapCorrectionDeadlineDays);
                        client.getNodesAPI().updateNodeCall(docId, new NodeBodyUpdate(props));

                        break;
                    }
                    default:
                }
            }
        }
    }

    @Override
    public PaginatedListWrapper<NodeRepresentation> loadBranchChangeGaps(String registryActionId, String changesRelativePath, int start, int limit) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        ResultPaging<NodeRepresentation> gaps = client.getNodesAPI().listNodeChildrenCall(registryActionId, start, limit, new OrderByParam(Collections.singletonList("createdAt")), null, new IncludeParam(Collections.singletonList("properties")), AlfrescoPropConstants.FI_REGISTRY_GAP_FOLDER_NAME_KEY, null, null);
        List<NodeRepresentation> changes = client.getNodesAPI().listNodeChildrenCall(registryActionId, 0, null, new OrderByParam(Collections.singletonList("createdAt")), null, new IncludeParam(Collections.singletonList("properties")), changesRelativePath, null, null).getObjects();

        gaps.getObjects().forEach(gap -> {
            String branchId = FirstUtil.getValue(gap.getProperties().get(EcmConstants.FI_GAP_PROP_OBJECT_ID), String.class);

            if (branchId != null) {
                for (NodeRepresentation change : changes) {
                    if (branchId.equals(FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_REFERENCE_ID), String.class))) {
                        gap.getProperties().put("branch", client.getNodesAPI().getNodeCall(branchId));

                        Object documentId = change.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_DOCUMENT_REFERENCE_ID);
                        if (documentId != null) {
                            NodeRepresentation document = findDocumentById((String) documentId);
                            if (document != null) {
                                gap.getProperties().put("document", document);
                            }
                        }

                        break;
                    }
                }
            }
        });

        PaginatedListWrapper<NodeRepresentation> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(gaps.getObjects());
        listWrapper.setTotalResults(gaps.getPagination().getTotalItems());

        return listWrapper;
    }

    @Override
    public void restartBranchChangeAction(String registryActionId, String relativePath) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        List<NodeRepresentation> gaps = client.getNodesAPI().listNodeChildrenCall(registryActionId, 0, null, new OrderByParam(Collections.singletonList("modifiedAt")), null, new IncludeParam(Collections.singletonList("properties")), AlfrescoPropConstants.FI_REGISTRY_GAP_FOLDER_NAME_KEY, null, null).getObjects();
        List<NodeRepresentation> changes = client.getNodesAPI().listNodeChildrenCall(registryActionId, 0, null, new OrderByParam(Collections.singletonList("modifiedAt")), null, new IncludeParam(Collections.singletonList("properties")), relativePath, null, null).getObjects();

        Map<String, TreeMap<String, Object>> predefinedQuestionnaireBasedGaps = new HashMap<>();
        Map<String, TreeMap<String, Object>> extraQuestionnaireBasedGaps = new HashMap<>();

        changes.forEach(change -> {
            String branchId = FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_REFERENCE_ID), String.class);

            if (branchId != null) {
                String preChangeVersion = FirstUtil.getValue(change.getProperties().get(EcmConstants.FI_BRANCH_CHANGE_PRE_CHANGE_VERSION), String.class);

                for (NodeRepresentation gap : gaps) {

                    Map<String, Object> gapProperties = gap.getProperties();

                    if (branchId.equals(FirstUtil.getValue(gapProperties.get(EcmConstants.FI_GAP_PROP_OBJECT_ID), String.class))) {

                        boolean isGapBasedOnQuestionnaire = FirstUtil.getValue(gapProperties.get(EcmConstants.FI_FAP_PROP_IS_BASED_ON_QUESTIONNAIRE), Boolean.class);
                        if (isGapBasedOnQuestionnaire) { // is based on questionnaire

                            String questionnairePropertyName = FirstUtil.getValue(gapProperties.get(EcmConstants.FI_GAP_PROP_QUESTIONNAIRE_PROP_NAME), String.class);
                            String extraQuestionnaireId = FirstUtil.getValue(gapProperties.get(EcmConstants.FI_GAP_PROP_EXTRA_QUESTIONNAIRE_ID), String.class);
                            String gapOriginalStatus = FirstUtil.getValue(gapProperties.get(EcmConstants.FI_GAP_PROP_CORRECTION_STATUS), String.class);
                            Boolean gapBooleanStatus = (gapOriginalStatus != null ? gapOriginalStatus.trim().equalsIgnoreCase("CORRECTED") : null);

                            if (extraQuestionnaireId != null && !extraQuestionnaireId.trim().isEmpty()) { // extra questionnaire
                                extraQuestionnaireBasedGaps.computeIfAbsent(extraQuestionnaireId, k -> new TreeMap<>());

                                String extraQuestionnaireStatusText = (gapBooleanStatus != null ? gapBooleanStatus ? "OK" : "NO" : "NONE");
                                extraQuestionnaireBasedGaps.get(extraQuestionnaireId).put(questionnairePropertyName, extraQuestionnaireStatusText);
                            } else { // predefined questionnaire
                                predefinedQuestionnaireBasedGaps.computeIfAbsent(branchId, k -> new TreeMap<>());
                                predefinedQuestionnaireBasedGaps.get(branchId).put(questionnairePropertyName, gapBooleanStatus);
                            }

                        } else { // is not based on questionnaire
                            String correctionStatus = FirstUtil.getValue(gap.getProperties().get(EcmConstants.FI_GAP_PROP_CORRECTION_STATUS), String.class);
                            String branchChangeStatus = convertCorrectionStatusToChangeStatus(correctionStatus);
                            updateBranchChange(client, change.getId(), branchChangeStatus, preChangeVersion);
                        }

                        // delete gap
                        client.getNodesAPI().deleteNodeCall(gap.getId());
                    }
                }

                // detect and update questionnaire based branch change object
                detectAndUpdateQuestionnaireBasedBranchChanges(client, change.getId(), predefinedQuestionnaireBasedGaps.get(branchId), extraQuestionnaireBasedGaps.get(branchId), preChangeVersion);
            }
        });


        // update branches questionnaires
        if (!predefinedQuestionnaireBasedGaps.isEmpty()) {
            for (Map.Entry<String, TreeMap<String, Object>> entry : predefinedQuestionnaireBasedGaps.entrySet()) {
                client.getNodesAPI().updateNodeCall(entry.getKey(), new NodeBodyUpdate(entry.getValue()));
            }
        }

        // update branches extra questionnaires
        if (!extraQuestionnaireBasedGaps.isEmpty()) {
            for (Map.Entry<String, TreeMap<String, Object>> entry : extraQuestionnaireBasedGaps.entrySet()) {
                client.getNodesAPI().updateNodeCall(entry.getKey(), new NodeBodyUpdate(entry.getValue()));
            }
        }

    }

    private String convertCorrectionStatusToChangeStatus(String correctionStatus) {
        String res = null;

        if (correctionStatus == null || correctionStatus.isEmpty() || correctionStatus.equals("NOT_CORRECTED")) {
            res = "DECLINED";
        }

        return res;
    }

    @Override
    public List<NodeRepresentation> loadReportTemplates(List<String> tags) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        ResultPaging<NodeRepresentation> reportTemplateNodes = client.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT, null, null, new OrderByParam(Collections.singletonList("name asc")), "(isFile=true)", new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.REPORT_TEMPLATES_FOLDER_PATH), null, null);
        List<NodeRepresentation> reportTemplates = reportTemplateNodes.getObjects();

        if (tags != null && !tags.isEmpty() && !reportTemplates.isEmpty()) {
            List<NodeRepresentation> resultNodes = new ArrayList<>();
            for (NodeRepresentation nodeRepresentation : reportTemplates) {
                List<TagMetaModel> reportTemplateTags = tagProxySession.listTagsForNode(nodeRepresentation.getId()).getList();
                if (containsTag(reportTemplateTags, tags)) {
                    resultNodes.add(nodeRepresentation);
                }
            }
            return resultNodes;
        }

        return reportTemplates;
    }

    @Override
    public List<FiRegistryMetaModel> loadFiRegistryByType(String fiTypeNodeId) {
        NodeRepresentation fiTypeNode = nodeProxySession.getNodeById(fiTypeNodeId, new IncludeParam(Collections.singletonList("properties")).toString(), null, null);
        String typeCode = FirstUtil.getValue(fiTypeNode.getProperties().get(EcmConstants.PROP_CODE), String.class);
        String searchQuery = "select * from fina:fiRegistry where " + EcmConstants.REGISTRY_PROP_TYPE_CODE + "='" + typeCode + "'";
        ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchProxySession.searchByCMIS(searchQuery, 1, 0, Integer.MAX_VALUE);

        return FiRegistryMetaModelHelper.getAll(searchResult.getObjects());
    }

    @Override
    public PaginatedListWrapper<FiRegistryMetaModel> searchFi(String queryText, int page, int start, int limit) {
        String query = EcmConstants.REGISTRY_PROP_NAME + ":\"*" + queryText + "*\" OR " + EcmConstants.REGISTRY_PROP_CODE + ":\"*" + queryText + "*\"";
        ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchProxySession.searchAFTS(null, query, start, limit);

        PaginatedListWrapper<FiRegistryMetaModel> resultPaging = new PaginatedListWrapper<>();
        resultPaging.setList(FiRegistryMetaModelHelper.getAll(searchResult.getObjects()));
        resultPaging.setTotalResults(searchResult.getPagination().getTotalItems());
        resultPaging.setCurrentPage(page);
        resultPaging.setPageSize(limit);

        return resultPaging;
    }

    @Override
    public String restoreFiRegistryGapActionTask(String acceptLanguage, String fiRegistryId, String fiRegistryGapDetailNodeId) throws NodeException {
        String resultFiRegistryVersion = null;

        NodeMetaModel fiRegistryNode = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryId, null);
        String fiRegistryCurrentActionId = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);

        NodeMetaModel fiRegistryGapDetailNode = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryGapDetailNodeId, null);
        NodeMetaModel fiRegistryCurrentActionNode = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryCurrentActionId, null);

        if (fiRegistryGapDetailNode.getNodeType().equalsIgnoreCase(EcmConstants.FI_REGISTRY_GAP_DETAIL_TYPE)) {

            String fiRegistryCurrentStatus = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_STATUS), String.class);
            String fiRegistryCurrentActionType = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_ACTION_TYPE), String.class);
            int fiRegistryCurrentActionStep = FirstUtil.getValue(fiRegistryCurrentActionNode.getProperties().get(EcmConstants.ACTION_PROP_STEP), Integer.class);
            String fiRegistryCurrentProcessId = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_PROCESS_ID), String.class);
            boolean isCurrentTaskGap = fiRegistryCurrentStatus.equalsIgnoreCase("GAP") || (fiRegistryCurrentActionStep == 9 && (fiRegistryCurrentActionType.equalsIgnoreCase(FiRegistryActionType.BRANCHES_CHANGE.name()) || fiRegistryCurrentActionType.equalsIgnoreCase(FiRegistryActionType.BRANCHES_EDIT.name())));

            if (isCurrentTaskGap) {
                TreeMap<String, Object> createProperties = new TreeMap<>();
                createProperties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);
                createProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_OBJECT_TYPE, fiRegistryCurrentActionType);
                createProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_FI_REGISTRY_VERSION, fiRegistryNode.getProperties().get("cm:versionLabel"));
                createProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_REGISTRY_ID, fiRegistryId);
                createProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_ACTION_ID, fiRegistryCurrentActionId);

                List<ResultNodeRepresentation> gapLetterNodes = getFiRegistryDocument(fiRegistryCurrentProcessId, FiRegistryDocumentType.GAP_LETTER.name(), fiRegistryCurrentActionId).getList();
                if (gapLetterNodes != null && !gapLetterNodes.isEmpty()) {
                    Map<String, Object> gapLetterProperties = gapLetterNodes.get(0).getProperties();
                    createProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_CORRECTION_DATE, gapLetterProperties.get(EcmConstants.DOCUMENT_PROP_CORRECTION_DEADLINE));
                    createProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_CORRECTION_DAYS, gapLetterProperties.get(EcmConstants.DOCUMENT_PROP_CORRECTION_DEADLINE_DAYS));
                    createProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_CORRECTION_LETTER_NUMBER, gapLetterProperties.get(EcmConstants.DOCUMENT_PROP_DOCUMENT_NUMBER));
                }

                NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.FI_REGISTRY_GAP_DETAIL_TYPE, createProperties, null);
                nodeProxySession.createChildNode(fiRegistryGapDetailNode.getParentId(), nodeBodyCreate);
            }

            // fi registry version to get data from
            resultFiRegistryVersion = FirstUtil.getValue(fiRegistryGapDetailNode.getProperties().get(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_FI_REGISTRY_VERSION), String.class);
            Map<String, Object> restoreToRegistryNodeVersionProperties = versionProxySession.getNodeVersion(acceptLanguage, fiRegistryId, resultFiRegistryVersion).getProperties();

            // update fi registry based on the task
            TreeMap<String, Object> fiRegistryUpdateProperties = new TreeMap<>();
            fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_STATUS, restoreToRegistryNodeVersionProperties.get(EcmConstants.REGISTRY_PROP_STATUS));
            fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_HAS_REFUSAL_DOCUMENT, restoreToRegistryNodeVersionProperties.get(EcmConstants.REGISTRY_PROP_HAS_REFUSAL_DOCUMENT));
            fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_LAST_EDITOR_ID, restoreToRegistryNodeVersionProperties.get(EcmConstants.REGISTRY_PROP_LAST_EDITOR_ID));
            fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID, restoreToRegistryNodeVersionProperties.get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID));
            fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_LAST_INSPECTOR_ID, restoreToRegistryNodeVersionProperties.get(EcmConstants.REGISTRY_PROP_LAST_INSPECTOR_ID));
            fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_LAST_PROCESS_ID, restoreToRegistryNodeVersionProperties.get(EcmConstants.REGISTRY_PROP_LAST_PROCESS_ID));
            fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_ACTION_TYPE, restoreToRegistryNodeVersionProperties.get(EcmConstants.REGISTRY_PROP_ACTION_TYPE));

            if (!isCurrentTaskGap) {
                int fiRegistryArchivedGapTaskCount = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT), Integer.class);
                if (fiRegistryArchivedGapTaskCount != 0) {
                    fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT, fiRegistryArchivedGapTaskCount - 1);
                }
            }

            NodeBodyUpdate fiRegistryNodeBodyUpdate = new NodeBodyUpdate(fiRegistryUpdateProperties);
            nodeProxySession.updateNode(fiRegistryNode.getId(), fiRegistryNodeBodyUpdate, null);

            // make restored gap detail object active status to false
            TreeMap<String, Object> fiRegistryGapDetailUpdateProperties = new TreeMap<>();
            fiRegistryGapDetailUpdateProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_ACTIVE, false);

            NodeBodyUpdate fiRegistryGapDetailNodeBodyUpdate = new NodeBodyUpdate(fiRegistryGapDetailUpdateProperties);
            nodeProxySession.updateNode(fiRegistryGapDetailNode.getId(), fiRegistryGapDetailNodeBodyUpdate, null);
        }

        return resultFiRegistryVersion;

    }

    @Override
    public NodeMetaModel deleteFiRegistryGapActionDetail(String acceptLanguage, String fiRegistryGapDetailId, String comment) throws NodeException {

        NodeMetaModel result = null;

        NodeMetaModel fiRegistryGapDetailNode = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryGapDetailId, null);
        if (fiRegistryGapDetailNode != null && fiRegistryGapDetailNode.getNodeType().equalsIgnoreCase(EcmConstants.FI_REGISTRY_GAP_DETAIL_TYPE)) {

            String fiRegistryActionId = FirstUtil.getValue(fiRegistryGapDetailNode.getProperties().get(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_ACTION_ID), String.class);

            // create gap for action
            if (comment != null && !comment.trim().isEmpty()) {
                NodeBodyCreate nbc = new NodeBodyCreate();
                nbc.setNodeType(EcmConstants.FI_GAP_TYPE);
                Map<String, Object> properties = new TreeMap<>();
                properties.put(EcmConstants.FI_GAP_PROP_OBJECT, "other");
                properties.put(EcmConstants.FI_GAP_PROP_REASON, comment);
                nbc.setProperties(properties);

                nbc.setRelativePath(AlfrescoPropConstants.FI_REGISTRY_GAP_FOLDER_NAME_KEY);
                nodeProxySession.createChildNode(fiRegistryActionId, nbc);
            }

            // update current fi registry gap detail object
            TreeMap<String, Object> fiRegistryGapDetailUpdateProperties = new TreeMap<>();
            fiRegistryGapDetailUpdateProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_ACTIVE, false);
            fiRegistryGapDetailUpdateProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_IS_DELETED, true);
            fiRegistryGapDetailUpdateProperties.put(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_DELETE_COMMENT, comment);

            NodeBodyUpdate fiRegistryGapDetailNodeBodyUpdate = new NodeBodyUpdate(fiRegistryGapDetailUpdateProperties);
            result = nodeProxySession.updateNode(fiRegistryGapDetailNode.getId(), fiRegistryGapDetailNodeBodyUpdate);

            // update fi registry object
            String fiRegistryId = FirstUtil.getValue(fiRegistryGapDetailNode.getProperties().get(EcmConstants.FI_REGISTRY_GAP_DETAIL_PROP_REGISTRY_ID), String.class);
            NodeMetaModel fiRegistryNode = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryId, null);
            int fiRegistryArchivedGapTaskCount = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT), Integer.class);

            if (fiRegistryArchivedGapTaskCount != 0) {
                TreeMap<String, Object> fiRegistryUpdateProperties = new TreeMap<>();
                fiRegistryUpdateProperties.put(EcmConstants.REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT, fiRegistryArchivedGapTaskCount - 1);

                NodeBodyUpdate fiRegistryNodeBodyUpdate = new NodeBodyUpdate(fiRegistryUpdateProperties);
                nodeProxySession.updateNode(fiRegistryId, fiRegistryNodeBodyUpdate);
            }

        }

        return result;
    }

    @Override
    public ECMPersonMetaModel sendToController(String acceptLanguage, String fiRegistryId, String groupId) throws Exception {
        ECMPersonMetaModel controller;
        NodeRepresentation registry = nodeProxySession.getNodeById(fiRegistryId, new IncludeParam(Arrays.asList(APIConstants.PROPERTIES_VALUE, APIConstants.PERMISSIONS_VALUE)).toString(), null, null);

        List<ECMPersonMetaModel> availableControllers = groupProxySession.loadGroupById(groupId);
        availableControllers = availableControllers.stream().filter(item -> !item.getId().equals(FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_EDITOR_ID), String.class))).collect(Collectors.toList());

        if (!availableControllers.isEmpty()) {
            SecureRandom random = new SecureRandom();
            int randomInt = random.nextInt(availableControllers.size());
            controller = availableControllers.get(randomInt);

            // Update last inspector prop
            TreeMap<String, Object> props = new TreeMap<>();
            props.put(EcmConstants.REGISTRY_PROP_LAST_INSPECTOR_ID, controller.getId());

            PermissionsInfoRepresentation pi = registry.getPermissions();
            List<PermissionElementRepresentation> locallySetPermissions = pi.getLocallySet();

            // Add Collaborator permission on FiRegistry folder for inspector
            addPermission(locallySetPermissions, controller.getId(), "Collaborator");

            PermissionsBodyUpdate pbu = new PermissionsBodyUpdate();
            pbu.setLocallySet(locallySetPermissions);
            pbu.setInheritanceEnabled(pi.getIsInheritanceEnabled());

            NodeBodyUpdate nbu = new NodeBodyUpdate();
            nbu.setProperties(props);
            nbu.setPermissions(pbu);

            nodeProxySession.updateNode(fiRegistryId, nbu);

            return controller;
        } else {
            throw new Exception("No Available Controllers In Group [" + groupId + "]!");
        }
    }

    private void cancelChangeNode(String acceptLanguage, String performedChangeNodeStatus, String performedChangeNodeId, FiRegistryActionType actionType, String actionId) throws NodeException {
        List<VersionMetaModel> performedChangeNodeVersionModels = versionProxySession.getNodeVersions(acceptLanguage, performedChangeNodeId).getList();

        if (performedChangeNodeStatus.equalsIgnoreCase("CHANGED")) {
            VersionMetaModel performedChangeNodeRevertToVersion = versionProxySession.getVersionBeforePropertyValueChange(performedChangeNodeVersionModels, EcmConstants.BRANCH_PROP_FI_REGISTRY_ACTION_ID, actionId);
            if (performedChangeNodeRevertToVersion == null) {
                performedChangeNodeRevertToVersion = performedChangeNodeVersionModels.get(performedChangeNodeVersionModels.size() - 1);
            }
            VersionRevertBodyMetaModel revertBodyMetaModel = new VersionRevertBodyMetaModel("Revert to version: " + performedChangeNodeRevertToVersion.getId() + ", from version: " + performedChangeNodeVersionModels.get(0).getId() + ", because of " + actionType.name() + " action cancellation.", true);
            versionProxySession.revertToVersion(acceptLanguage, performedChangeNodeId, performedChangeNodeRevertToVersion.getId(), revertBodyMetaModel);
        } else if (!performedChangeNodeStatus.equalsIgnoreCase("ADDED")) {
            VersionMetaModel branchNodeRevertToVersion = versionProxySession.getVersionBeforePropertyValueChange(performedChangeNodeVersionModels, EcmConstants.COMMON_PROP_STATUS, performedChangeNodeStatus);
            VersionRevertBodyMetaModel revertBodyMetaModel = new VersionRevertBodyMetaModel("Revert to version: " + branchNodeRevertToVersion.getId() + ", from version: " + performedChangeNodeVersionModels.get(0).getId() + ", because of " + actionType.name() + " action cancellation.", true);
            versionProxySession.revertToVersion(acceptLanguage, performedChangeNodeId, branchNodeRevertToVersion.getId(), revertBodyMetaModel);
        } else {
            nodeProxySession.deleteNodeById(performedChangeNodeId);
        }
    }

    private boolean containsTag(List<TagMetaModel> existingTags, List<String> checkTags) {
        if (existingTags != null && !existingTags.isEmpty() && checkTags != null && !checkTags.isEmpty()) {
            for (TagMetaModel existingTag : existingTags) {
                for (String checkTag : checkTags) {
                    if (existingTag.getTag().equalsIgnoreCase(checkTag)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public NodeMetaModel getRegionByName(String name) {
        ResultNodeRepresentation result = getRegionalStructureData(EcmConstants.REGIONAL_STRUCTURE_REGION_NAME + ":" + name);
        return result == null ? null : NodeModelHelper.getMetaModel(result);
    }

    @Override
    public NodeMetaModel getCityByName(String name) {
        ResultNodeRepresentation result = getRegionalStructureData(EcmConstants.REGIONAL_STRUCTURE_CITY_NAME + ":" + name);
        return result == null ? null : NodeModelHelper.getMetaModel(result);
    }

    private ResultNodeRepresentation getRegionalStructureData(String query) {
        ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchProxySession.searchAFTS("*", query, 0, Integer.MAX_VALUE);
        if (searchResult != null && !searchResult.getObjects().isEmpty()) {
            return searchResult.getObjects().get(0);
        }
        return null;
    }

    private String constructGapDescription(FiRegistryActionQuestionnaireStatusMetaModel q, String fiRegistryId, String acceptLanguage) {
        StringBuilder sb = new StringBuilder();
        NodeRepresentation relativeNode = q.getRelativeNode();
        if (relativeNode != null) {
            if (relativeNode.getNodeType().contains("fiRegistryBranch")) {
                sb.append(getBranchDescription(relativeNode));
            } else {
                Object complexStructureTypeObj = relativeNode.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_TYPE);
                if (complexStructureTypeObj != null && complexStructureTypeObj.equals(EcmConstants.COMPLEX_STRUCTURE_STATUS_LEGAL)) {
                    sb.append(getLegalShareHolderDescription(relativeNode));
                } else {
                    sb.append(getPersonDescription(relativeNode));
                }

                // Append person position information (Authorized person position or "Beneficiary"
                List<String> fiComplexStructureTypes = Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.DATA_TYPE_COMPLEX_STRUCTURE_KEY));
                String personPosition = "";
                if (fiComplexStructureTypes.contains(relativeNode.getNodeType())) {
                    personPosition = MessagesUtil.getString("beneficiary", acceptLanguage);
                } else {
                    for (String propertyName : relativeNode.getProperties().keySet()) {
                        if (propertyName.contains("fiAuthorizedPersonPosition")) {
                            personPosition = MessagesUtil.getString((String) relativeNode.getProperties().get(propertyName), acceptLanguage);
                            break;
                        }
                    }
                }

                if (personPosition.length() > 0) {
                    sb.append(" - ");
                    sb.append(personPosition);
                }
            }
        } else {
            sb.append(getFiDescription(acceptLanguage, fiRegistryId));
        }
        return sb.toString();
    }

    private String getPersonDescription(NodeRepresentation node) {
        StringBuilder sb = new StringBuilder();

        String firstName = FirstUtil.getValue(node.getProperties().get(EcmConstants.FI_PERSON_PROP_FIRSTNAME), String.class);
        String lastName = FirstUtil.getValue(node.getProperties().get(EcmConstants.FI_PERSON_PROP_LASTNAME), String.class);
        String personalNumber = FirstUtil.getValue(node.getProperties().get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER), String.class);

        sb.append(firstName != null ? firstName : "").append(" ").append(lastName != null ? lastName : "").append(personalNumber != null ? (" (" + personalNumber + ")") : "");

        return sb.toString();
    }

    private String getLegalShareHolderDescription(NodeRepresentation node) {
        StringBuilder sb = new StringBuilder();

        String legalFormType = MessagesUtil.getString(FirstUtil.getValue(node.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_LEGAL_TYPE), String.class));
        String legalName = FirstUtil.getValue(node.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_LEGAL_NAME), String.class);
        String id = FirstUtil.getValue(node.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_IDENTIFICATION_NUMBER), String.class);

        sb.append(legalFormType != null ? legalFormType : "");
        sb.append(" ");
        if (legalName != null) {
            sb.append("\"").append(legalName).append("\"");
        }
        sb.append(" ");
        if (id != null) {
            sb.append("(").append(id).append(")");
        }

        return sb.toString();
    }

    private String getBranchDescription(NodeRepresentation node) {
        StringBuilder sb = new StringBuilder();
        String branchType = MessagesUtil.getString(FirstUtil.getValue(node.getProperties().get(EcmConstants.BRANCH_PROP_TYPE), String.class)), address = FirstUtil.getValue(node.getProperties().get(EcmConstants.BRANCH_PROP_ADDRESS_ADDRESS), String.class);
        sb.append(MessagesUtil.getString(branchType));
        if (address != null && !address.trim().isEmpty()) {
            sb.append(" ").append("(").append(address).append(")");
        }
        return sb.toString();
    }

    private String getFiDescription(String acceptLanguage, String fiRegistryId) {
        StringBuilder sb = new StringBuilder();

        NodeMetaModel fiRegistryNode = getFi(acceptLanguage, fiRegistryId);
        String legalFormType = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_LEGAL_FORM_TYPE), String.class);
        String name = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_NAME), String.class);
        String id = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_IDENTITY), String.class);
        sb.append(MessagesUtil.getString(legalFormType)).append(" ").append("\"").append(name).append("\"").append(" ").append("(").append(id).append(")");

        return sb.toString();
    }


    @Override
    public void finishFiProcess(String acceptLanguage, String fiRegistryId, FiFinishModel finishModel) throws NodeException {

        NodeMetaModel fiRegistryNode = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryId, null);
        Map<String, Object> fiRegistryNodeProperties = fiRegistryNode.getProperties();
        String processId = FirstUtil.getValue(fiRegistryNodeProperties.get(EcmConstants.REGISTRY_PROP_LAST_PROCESS_ID), String.class);

        try {
            //approve/reject task
            workflowProxySession.finishWorkflow(acceptLanguage, processId, finishModel.getPreFinishVariables());
            //finish task
            workflowProxySession.finishWorkflow(acceptLanguage, processId, finishModel.getFinishVariables());

            //delete all notifications for finished process
            notificationInfoLocal.deleteNotificationsForAction((String) fiRegistryNodeProperties.get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), null);

            //start new process
            if (finishModel.getNewProcessName() != null) {
                switch (finishModel.getNewProcessName()) {
                    case AlfrescoPropConstants.BRANCH_CHANGE_WORKFLOW_KEY:

                        //create branch change workflow
                        WorkflowProcessBodyCreateMetaModel workflowBody = new WorkflowProcessBodyCreateMetaModel();

                        Map<String, Object> workflowVariables = new HashMap<>();
                        workflowVariables.put("fwf_fiStartTaskBaseFiCode", FirstUtil.getValue(fiRegistryNodeProperties.get(EcmConstants.REGISTRY_PROP_CODE), String.class));
                        workflowVariables.put("fwf_fiStartTaskBaseFiIdentity", FirstUtil.getValue(fiRegistryNodeProperties.get(EcmConstants.REGISTRY_PROP_IDENTITY), String.class));
                        workflowVariables.put("fwf_fiStartTaskBaseTaskNumber", FirstUtil.getValue(fiRegistryNodeProperties.get(EcmConstants.REGISTRY_TASK_NUMBER), String.class));
                        workflowVariables.put("fwf_fiStartTaskBaseTaskReceiptDate", FirstUtil.getValue(fiRegistryNodeProperties.get(EcmConstants.REGISTRY_TASK_RECIPE_DATE), String.class));

                        workflowBody.setVariables(workflowVariables);
                        workflowProxySession.createWorkflow(acceptLanguage, fiTypeLocal.getFiTypeById(fiRegistryId).getBranchChangeWorkflowKey(), workflowBody);

                        String branchesFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_BRANCHES_FOLDER_NAME_KEY);
                        List<NodeMetaModel> branches = nodeProxySession.getNodeChildrenWithClassProperties(acceptLanguage, fiRegistryId, null, null, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), branchesFolderName, null, null).getList();
                        if (branches != null && !branches.isEmpty()) {

                            NodeMetaModel updatedFiRegistryNode = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryId, null);
                            String newProcessActionId = FirstUtil.getValue(updatedFiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);

                            for (NodeMetaModel branch : branches) {
                                Map<String, Object> branchProperties = branch.getProperties();
                                if ("GAP".equalsIgnoreCase(FirstUtil.getValue(branchProperties.get(EcmConstants.BRANCH_PROP_STATUS), String.class))) {
                                    // update branch
                                    TreeMap<String, Object> properties = new TreeMap<>();
                                    properties.put(EcmConstants.COMMON_PROP_STATUS, "ADDED");
                                    properties.put(EcmConstants.NODE_PROP_FI_REGISTRY_ACTION_ID, newProcessActionId);
                                    properties.put(EcmConstants.IGNORE_WARNINGS, true);
                                    nodeProxySession.updateNode(branch.getId(), new NodeBodyUpdate(properties));
                                }
                            }
                        }
                        break;
                }
            }
        } catch (WorkflowProcessException e) {
            throw new NodeException(e.getMessage());
        }
    }

    @Override
    public NodeMetaModel getFiHeadOffice(String fiRegistryId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        String branchesFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_BRANCHES_FOLDER_NAME_KEY);

        ResultPaging<NodeRepresentation> branches = client.getNodesAPI().listNodeChildrenCall(fiRegistryId, null, 1000, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), branchesFolderName, null, null);

        branches.setObjects(branches.getObjects().stream().filter(item -> "HEAD_OFFICE".equals(item.getProperties().get(EcmConstants.BRANCH_PROP_TYPE))).collect(Collectors.toList()));

        NodeMetaModel headOffice = null;
        if (branches.getCount() >= 1) {
            headOffice = NodeModelHelper.getMetaModel(branches.getObjects().get(0));
        }

        return headOffice;
    }

    @Override
    public void syncQuestionnaireBasedOnGaps(String acceptLanguage, String fiRegistryId) throws NodeException {
        NodeMetaModel registry = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryId, null);
        String fiRegistryActionId = FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);
        PaginatedListWrapper<NodeMetaModel> gapNodes = nodeProxySession.getNodeChildren(acceptLanguage, fiRegistryActionId, null, null, null, null, null, new IncludeParam(Collections.singletonList("properties")), AlfrescoPropConstants.FI_REGISTRY_GAP_FOLDER_NAME_KEY, null, null);

        String gapObject = "Questionnaire";

        // update only questionnaire based questions
        for (NodeMetaModel gapNode : gapNodes.getList()) {

            Map<String, Object> gapNodeProperties = gapNode.getProperties();
            String fiGapObject = FirstUtil.getValue(gapNode.getProperties().get(EcmConstants.FI_GAP_PROP_OBJECT), String.class);

            if (fiGapObject.equals(gapObject) || fiGapObject.equals("branch")) {
                String questionnairePropertyName = FirstUtil.getValue(gapNodeProperties.get(EcmConstants.FI_GAP_PROP_QUESTIONNAIRE_PROP_NAME), String.class);
                String gapObjectId = FirstUtil.getValue(gapNodeProperties.get(EcmConstants.FI_GAP_PROP_OBJECT_ID), String.class);
                String questionnaireComment = FirstUtil.getValue(gapNodeProperties.get(EcmConstants.FI_GAP_PROP_CORRECTION_COMMENT), String.class);
                String questionnaireStatus = FirstUtil.getValue(gapNodeProperties.get(EcmConstants.FI_GAP_PROP_CORRECTION_STATUS), String.class);
                String extraQuestionnaireId = FirstUtil.getValue(gapNodeProperties.get(EcmConstants.FI_GAP_PROP_EXTRA_QUESTIONNAIRE_ID), String.class);

                if (questionnairePropertyName == null || questionnairePropertyName.trim().isEmpty()) { // main questionnaire
                    questionnaireStatus = (questionnaireStatus == null || questionnaireStatus.trim().isEmpty() ? "NONE" : questionnaireStatus.equals("NOT_CORRECTED") ? "NO" : "OK");

                    FiRegistryActionQuestionnaireStatusMetaModel actionQuestionnaireStatusMetaModel = new FiRegistryActionQuestionnaireStatusMetaModel();
                    actionQuestionnaireStatusMetaModel.setStatus(questionnaireStatus);
                    actionQuestionnaireStatusMetaModel.setNote(questionnaireComment);

                    TreeMap<String, Object> properties = new TreeMap<>();
                    properties.put(EcmConstants.IGNORE_WARNINGS, true);

                    fiRegistryActionQuestionnaireStatusProxySession.updateQuestionnaire(acceptLanguage, gapObjectId, actionQuestionnaireStatusMetaModel, properties);
                } else if (extraQuestionnaireId != null && !extraQuestionnaireId.trim().isEmpty()) { // child node extra questionnaire
                    questionnaireStatus = (questionnaireStatus == null || questionnaireStatus.trim().isEmpty() ? "NONE" : questionnaireStatus.equals("NOT_CORRECTED") ? "NO" : "OK");

                    TreeMap<String, Object> properties = new TreeMap<>();
                    properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS, questionnaireStatus);
                    properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_NOTE, questionnaireComment);

                    nodeProxySession.updateNode(gapObjectId, new NodeBodyUpdate(properties));
                } else { // child node questionnaire
                    Boolean status = (questionnaireStatus != null && !questionnaireStatus.trim().isEmpty() ? questionnaireStatus.equalsIgnoreCase("CORRECTED") : null);

                    TreeMap<String, Object> properties = new TreeMap<>();
                    properties.put(questionnairePropertyName, status);
                    properties.put(questionnairePropertyName + "Note", questionnaireComment);
                    properties.put(EcmConstants.IGNORE_WARNINGS, true);

                    nodeProxySession.updateNode(gapObjectId, new NodeBodyUpdate(properties));
                }
            }
        }

    }

    @Override
    public List<NodeMetaModel> loadFiBranchesSortedByTypeInOneYear(String fiType) {
        String branchFiType = "";
        LocalDate now = LocalDate.now();
        LocalDate prev = getPeriodStartDate(LocalDate.now().minusYears(1));
        String branchFiTypesString = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.EXISTING_BRANCH_SUBDIVISION_FI_TYPES);

        if (branchFiTypesString == null) {
            return new ArrayList<>();
        }

        for (String bt : branchFiTypesString.split(",")) {
            if (bt.contains(fiType)) {
                branchFiType = bt;
                break;
            }
        }

        String query = String.format("TYPE:'%s' and %s:['%s' TO '%s']", branchFiType, EcmConstants.BRANCH_PROP_REGISTRATION_DATE, prev, now);

        RequestSortDefinition sort = new RequestSortDefinition();
        sort.setType(RequestSortDefinition.TypeEnum.FIELD);
        sort.setField(EcmConstants.BRANCH_PROP_REGISTRATION_DATE);
        sort.ascending(true);

        ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchProxySession.searchAFTS("*", query, 0, Integer.MAX_VALUE, Collections.singletonList(sort), null);

        return NodeModelHelper.getMetaModels(searchResult.getObjects());
    }

    private NodeMetaModel createGapObject(String gapObject, String gapReason, String gapObjectId, String registryActionId) throws NodeException {
        return createGapObject(gapObject, gapReason, gapObjectId, registryActionId, null, null, null, null);
    }

    private NodeMetaModel createGapObject(String gapObject, String gapReason, String gapObjectId, String registryActionId, String gapQuestionnairePropertyName, String gapExtraQuestionnaireId, String gapCorrectionComment, String gapDescription) throws NodeException {

        // create gap
        NodeBodyCreate nbc = new NodeBodyCreate();
        nbc.setNodeType(EcmConstants.FI_GAP_TYPE);
        Map<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.FI_GAP_PROP_OBJECT, gapObject);
        properties.put(EcmConstants.FI_GAP_PROP_REASON, gapReason);
        properties.put(EcmConstants.FI_GAP_PROP_OBJECT_ID, gapObjectId);
        properties.put(EcmConstants.FI_GAP_PROP_QUESTIONNAIRE_PROP_NAME, gapQuestionnairePropertyName);
        properties.put(EcmConstants.FI_GAP_PROP_EXTRA_QUESTIONNAIRE_ID, gapExtraQuestionnaireId);
        properties.put(EcmConstants.FI_FAP_PROP_IS_BASED_ON_QUESTIONNAIRE, gapQuestionnairePropertyName != null && !gapQuestionnairePropertyName.trim().isEmpty());
        properties.put(EcmConstants.FI_GAP_PROP_CORRECTION_COMMENT, gapCorrectionComment);
        properties.put(EcmConstants.FI_GAP_PROP_DESCRIPTION, gapDescription);

        nbc.setProperties(properties);

        nbc.setRelativePath(AlfrescoPropConstants.FI_REGISTRY_GAP_FOLDER_NAME_KEY);
        return nodeProxySession.createChildNode(registryActionId, nbc);
    }

    private void detectAndUpdateQuestionnaireBasedBranchChanges(AlfrescoClient client, String branchChangeId, TreeMap<String, Object> predefinedQuestionnaireBasedGapProperties, TreeMap<String, Object> extraQuestionnaireBasedGapProperties, String preChangeVersion) {
        String changeStatus = null;
        if (predefinedQuestionnaireBasedGapProperties != null && !predefinedQuestionnaireBasedGapProperties.isEmpty()) {
            changeStatus = "ACCEPTED";
            for (Map.Entry<String, Object> entry : predefinedQuestionnaireBasedGapProperties.entrySet()) {
                Boolean predefinedQuestionnaireStatus = FirstUtil.getValue(entry.getValue(), Boolean.class);
                if (predefinedQuestionnaireStatus == null || !predefinedQuestionnaireStatus) {
                    changeStatus = "DECLINED";
                    break;
                }
            }
        }

        if (extraQuestionnaireBasedGapProperties != null && !extraQuestionnaireBasedGapProperties.isEmpty()) {
            changeStatus = "ACCEPTED";
            for (Map.Entry<String, Object> entry : extraQuestionnaireBasedGapProperties.entrySet()) {
                String extraQuestionnaireStatus = FirstUtil.getValue(entry.getValue(), String.class);
                if (extraQuestionnaireStatus == null || !extraQuestionnaireStatus.trim().equalsIgnoreCase("OK")) {
                    changeStatus = "DECLINED";
                    break;
                }
            }
        }

        if (changeStatus != null) {
            updateBranchChange(client, branchChangeId, changeStatus.equals("ACCEPTED") ? null : changeStatus, preChangeVersion);
        }
    }

    private void updateBranchChange(AlfrescoClient client, String branchChangeId, String branchChangeStatus, String preChangeVersion) {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.FI_BRANCH_CHANGE_FINAL_STATUS, branchChangeStatus);
        properties.put(EcmConstants.FI_BRANCH_CHANGE_PRE_CHANGE_VERSION, preChangeVersion);
        properties.put(EcmConstants.FI_BRANCH_CHANGE_REFUSAL_LETTER_DOCUMENT_REFERENCE_ID, null);
        properties.put(EcmConstants.FI_BRANCH_CHANGE_FINAL_STATUS_NOTE, null);
        properties.put(EcmConstants.FI_BRANCH_CHANGE_DECREE_DOCUMENT_REFERENCE_ID, null);
        properties.put(EcmConstants.FI_BRANCH_CHANGE_DOCUMENT_REFERENCE_ID, null);

        // update branches change
        client.getNodesAPI().updateNodeCall(branchChangeId, new NodeBodyUpdate(properties));
    }

    private void createFiRegistryBranchGapObjectsBasedOnQuestionnaire(List<FiRegistryActionQuestionnaireStatusMetaModel> branchQuestionnaires, String branchId, String registryActionId, String nonQuestionaireBasedGapReason) throws NodeException {
        boolean isGapBasedOnQuestionnaire = false;
        if (branchQuestionnaires != null && !branchQuestionnaires.isEmpty()) {
            for (FiRegistryActionQuestionnaireStatusMetaModel branchQuestionnaire : branchQuestionnaires) {
                String branchQuestionnaireStatus = branchQuestionnaire.getStatus();
                if (branchQuestionnaireStatus != null && branchQuestionnaireStatus.equalsIgnoreCase("NO")) {
                    isGapBasedOnQuestionnaire = true;

                    // create gap
                    String gapReason = branchQuestionnaire.getQuestion();
                    if (branchQuestionnaire.isPredefined()) {
                        String gapQuestionnairePropertyName = branchQuestionnaire.getQuestionnairePropertyName();
                        createGapObject("branch", gapReason, branchId, registryActionId, gapQuestionnairePropertyName, null, branchQuestionnaire.getNote(), null);
                    } else {
                        createGapObject("branch", gapReason, branchId, registryActionId, EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS, branchQuestionnaire.getId(), branchQuestionnaire.getNote(), null);
                    }
                }
            }
        }

        if (!isGapBasedOnQuestionnaire) {
            createGapObject("branch", nonQuestionaireBasedGapReason, branchId, registryActionId);
        }
    }

    private PaginatedListWrapper<NodeMetaModel> searchFiDetails(String nodeId, NodeRepresentation folderNode, String query, String filter, int start, int pageSize) {
        String searchType = FirstUtil.getValue(folderNode.getProperties().get("fina:folderConfigChildType"), String.class);
        String searchTemplate = FirstUtil.getValue(folderNode.getProperties().get("fina:folderConfigSearchTemplate"), String.class);
        String searchQuery = "select * from " + (searchType != null ? searchType : "cm:content") + " where ";
        if (searchTemplate != null && !searchTemplate.isEmpty()) {
            searchQuery += AlfrescoConfiguration.get().getAlfrescoProperty(searchTemplate).replace("{0}", query) + " and ";
        }
        searchQuery += " IN_FOLDER('" + nodeId + "')";

        String filterString = searchProxySession.getCMISFilterString(filter);
        searchQuery += !filterString.isEmpty() ? " and " + filterString : "";

        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();
        if (pageSize > 0) {
            ResultSetRepresentation<ResultNodeRepresentation> result = searchProxySession.searchByCMIS(searchQuery, start / pageSize + 1, start, pageSize);
            listWrapper.setTotalResults(result.getPagination().getTotalItems());
            listWrapper.setList(sortItemsBySequence(NodeModelHelper.getMetaModels(result.getObjects())));
        } else {
            PaginatedListWrapper<ResultNodeRepresentation> result = searchProxySession.searchByCMIS(searchQuery);
            listWrapper.setTotalResults(result.getTotalResults());
            listWrapper.setList(sortItemsBySequence(NodeModelHelper.getMetaModels(result.getList())));
        }

        return listWrapper;
    }

    private List<NodeMetaModel> sortItemsBySequence(List<NodeMetaModel> filteredNodeMetaModels) {
        filteredNodeMetaModels.sort((o1, o2) -> {
            String o1s = FirstUtil.getValue(o1.getProperties().get("fina:folderConfigSequence"), String.class);
            String o2s = FirstUtil.getValue(o2.getProperties().get("fina:folderConfigSequence"), String.class);
            if (o1s != null && o2s != null) {
                return o1s.compareTo(o2s);
            }
            return 0;
        });

        return filteredNodeMetaModels;
    }

    private String getFiRegistrySearchTemplate(String templateRefKey) {
        String searchTemplate = AlfrescoConfiguration.get().getAlfrescoProperty(templateRefKey);
        searchTemplate = searchTemplate != null && !searchTemplate.trim().isEmpty() ? searchTemplate : "(cm:name:\"*{0}*\")";

        return searchTemplate;
    }

    private LocalDate getPeriodStartDate(LocalDate date) {
        return date.with(date.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
    }

    @Override
    public List<NodeMetaModel> loadFiBranchesByType(String type, String branchNodeTypeAlfrescoProperty) {
        List<NodeMetaModel> result = new ArrayList<>();
        String branchFiType = "";

        String branchFiTypesString = AlfrescoConfiguration.get().getAlfrescoProperty(branchNodeTypeAlfrescoProperty);

        if (branchFiTypesString == null) {
            return result;
        }

        for (String bt : branchFiTypesString.split(",")) {
            if (bt.contains(type)) {
                branchFiType = bt;
                break;
            }
        }

        String query = String.format("TYPE:'%s' and %s:'%s'", branchFiType, EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS, EcmConstants.COMMON_PROP_STATUS_ACTIVE);
        ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchProxySession.searchAFTS("*", query, 0, Integer.MAX_VALUE);

        for (ResultNodeRepresentation branch : searchResult.getObjects()) {
            result.add(NodeModelHelper.getMetaModel(branch));
        }

        return result;
    }

    @Override
    public ECMPersonMetaModel changeController(String acceptLanguage, String fiRegistryId, String groupId, String newControllerId) throws Exception {
        NodeRepresentation registry = nodeProxySession.getNodeById(fiRegistryId, new IncludeParam(Arrays.asList(APIConstants.PROPERTIES_VALUE, APIConstants.PERMISSIONS_VALUE)).toString(), null, null);
        String lastControllerId = FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_INSPECTOR_ID), String.class);
        ECMPersonMetaModel newController = peopleProxySession.getPersonById(newControllerId, null);

        // Update last inspector prop
        TreeMap<String, Object> props = new TreeMap<>();
        props.put(EcmConstants.REGISTRY_PROP_LAST_INSPECTOR_ID, newController.getId());

        PermissionsInfoRepresentation pi = registry.getPermissions();
        List<PermissionElementRepresentation> locallySetPermissions = pi.getLocallySet();

        // Add Collaborator permission on FiRegistry folder for inspector
        addPermission(locallySetPermissions, newController.getId(), "Collaborator");

        // Disable Collaborator permission on FiRegistry folder for previous inspector
        removePermission(locallySetPermissions, lastControllerId, "Collaborator");

        PermissionsBodyUpdate pbu = new PermissionsBodyUpdate();
        pbu.setLocallySet(locallySetPermissions);
        pbu.setInheritanceEnabled(pi.getIsInheritanceEnabled());

        NodeBodyUpdate nbu = new NodeBodyUpdate();
        nbu.setProperties(props);
        nbu.setPermissions(pbu);

        nodeProxySession.updateNode(fiRegistryId, nbu);

        return newController;
    }

    @Override
    public NodeRepresentation getFiRepresentation(String fiRegistryId) {
        return ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(fiRegistryId);
    }

    private void addPermission(List<PermissionElementRepresentation> locallySetPermissions, String userId, String permissionName) {
        boolean permissionExists = false;
        for (PermissionElementRepresentation p : locallySetPermissions) {
            if (p.getAuthorityId().equals(userId) && p.getName().equals(permissionName)) {
                permissionExists = true;
                p.setAccessStatus(PermissionElementRepresentation.AccessStatusEnum.ALLOWED);
                break;
            }
        }

        if (!permissionExists) {
            PermissionElementRepresentation per = new PermissionElementRepresentation();
            per.setAccessStatus(PermissionElementRepresentation.AccessStatusEnum.ALLOWED);
            per.setAuthorityId(userId);
            per.setName(permissionName);
            locallySetPermissions.add(per);
        }
    }

    private void removePermission(List<PermissionElementRepresentation> locallySetPermissions, String userId, String permissionName) {
        PermissionElementRepresentation permission = null;
        for (PermissionElementRepresentation p : locallySetPermissions) {
            if (p.getAuthorityId().equals(userId) && p.getName().equals(permissionName)) {
                permission = p;
//                p.setAccessStatus(PermissionElementRepresentation.AccessStatusEnum.DENIED);
                break;
            }
        }
        if (permission != null) {
            locallySetPermissions.remove(permission);
        }
    }

    private void cleanDuplicatedExtraQuestionnaires(String acceptLanguage, List<Map<String, Object>> sourceQuestionnaires, String extraQuestionnaireFolderId) {
        PaginatedListWrapper<NodeMetaModel> existingExtraQuestionnaires = nodeProxySession.getNodeChildren(acceptLanguage, extraQuestionnaireFolderId, null, null, null, null, null, new IncludeParam(Collections.singletonList("properties")), null, null, null);
        if (existingExtraQuestionnaires != null && existingExtraQuestionnaires.getTotalResults() > 0) {
            for (NodeMetaModel existingExtraQuestionnaireNode : existingExtraQuestionnaires.getList()) {
                sourceQuestionnaires.removeIf(newExtraQuestionnaire -> Objects.equals(FirstUtil.getValue(newExtraQuestionnaire.get("question"), String.class), FirstUtil.getValue(existingExtraQuestionnaireNode.getProperties().get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_QUESTION), String.class)));
            }
        }
    }

}
