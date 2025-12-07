package net.fina.first.ecm.registry.proxy;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;
import net.fina.ecm.alfresco.api.dictionary.model.ClassPropertyRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.registry.model.FiRegistryActionQuestionnaireStatusMetaModel;
import net.fina.first.ecm.registry.model.FiRegistryActionQuestionnaireStatusMetaModelHelper;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;
import net.fina.messages.MessagesUtil;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Stateless
@Interceptors(FirstRecordingAuditor.class)
public class FiRegistryActionQuestionnaireStatusProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private SearchLocal searchProxySession;

    @Inject
    private NodeLocal nodeProxySession;

    public PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> loadQuestionnaireStatusesForAction(String acceptLanguage, String fiRegistryId, String fiRegistryActionId) {

        List<FiRegistryActionQuestionnaireStatusMetaModel> resultModels = new ArrayList<>();

        String fiRegistryActionQuestionnaireFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ACTIONS_QUESTIONNAIRE_FOLDER_NAME_KEY);

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        if (fiRegistryActionId == null) {
            fiRegistryActionId = getLastActionFolderId(client, fiRegistryId);
        }

        if (fiRegistryActionId != null) {
            ResultPaging<NodeRepresentation> actionQuestionnaires = client.getNodesAPI().listNodeChildrenCall(fiRegistryActionId,
                    null, null, new OrderByParam(Collections.singletonList(EcmConstants.ACTION_QUESTIONNAIRE_SEQUENCE)),
                    "(nodeType=" + EcmConstants.ACTION_QUESTIONNAIRE_TYPE_QUESTIONNAIRE + ")",
                    new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiRegistryActionQuestionnaireFolderName, null, null);

            if (actionQuestionnaires != null) {
                resultModels = FiRegistryActionQuestionnaireStatusMetaModelHelper.getModels(actionQuestionnaires);
            }
        }

        if (fiRegistryId != null && !fiRegistryId.trim().isEmpty()) {
            // sub objects questionnaire
            String[] subObjectsQuestionnaireRelativePaths = {
                    AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_BRANCHES_FOLDER_NAME_KEY),
                    AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_AUTHORIZED_PERSONS_FOLDER_NAME_KEY),
                    AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_COMPLEX_STRUCTURE_FOLDER_NAME_KEY)
            };
            resultModels.addAll(getNodeSubQuestionnairesByAction(client, fiRegistryId, Arrays.asList(subObjectsQuestionnaireRelativePaths), fiRegistryActionId));
        }

        PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(resultModels.size());

        return listWrapper;
    }

    public PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> saveQuestionnaire(String acceptLanguage, String fiRegistryId, FiRegistryActionQuestionnaireStatusMetaModel model) throws NodeException {
        TreeMap<String, Object> properties = new TreeMap<>();

        properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_QUESTION, model.getQuestion());
        properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS, model.getStatus() != null ? model.getStatus() : "NONE");
        properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_NOTE, model.getNote());
        properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_PREDEFINED, false);

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        String fiRegistryActionId = getLastActionFolderId(client, fiRegistryId);
        if (fiRegistryActionId != null) {
            String fiRegistryActionQuestionnaireFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ACTIONS_QUESTIONNAIRE_FOLDER_NAME_KEY);
            String path = client.getNodesAPI().getNodeCall(fiRegistryActionId, null, fiRegistryActionQuestionnaireFolderName, null).getId();

            NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.ACTION_QUESTIONNAIRE_TYPE_QUESTIONNAIRE, properties, null);
            NodeRepresentation newActionQuestionnaire = nodeProxySession.createNode(path, nodeBodyCreate, client);

            PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> listWrapper = new PaginatedListWrapper<>();
            listWrapper.setList(Collections.singletonList(FiRegistryActionQuestionnaireStatusMetaModelHelper.getModel(newActionQuestionnaire.getProperties(), newActionQuestionnaire.getId())));
            listWrapper.setTotalResults(1);

            return listWrapper;
        }

        return null;
    }

    public PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> updateQuestionnaire(String acceptLanguage, String id, FiRegistryActionQuestionnaireStatusMetaModel model, TreeMap<String, Object> extraProperties) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        TreeMap<String, Object> properties = new TreeMap<>();

        if (extraProperties != null && !extraProperties.isEmpty()) {
            properties.putAll(extraProperties);
        }

        properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS, model.getStatus());
        properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_NOTE, model.getNote());

        NodeRepresentation registryNode = client.getNodesAPI().getNodeCall(id);
        if (!FiRegistryActionQuestionnaireStatusMetaModelHelper.getModel(registryNode.getProperties(), registryNode.getId()).isPredefined()) {
            properties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_QUESTION, model.getQuestion());
        }

        registryNode = nodeProxySession.updateNode(id, new NodeBodyUpdate(properties), client);

        PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(Collections.singletonList(FiRegistryActionQuestionnaireStatusMetaModelHelper.getModel(registryNode.getProperties(), registryNode.getId())));
        listWrapper.setTotalResults(1);

        return listWrapper;
    }

    public void deleteQuestionnaire(String acceptLanguage, String id) {
        ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().deleteNodeCall(id);
    }

    public PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> getFiRegistrySubObjectExtraQuestionnaires(String fiRegistrySubObjectId) {
        String extraQuestionnaireFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_SUB_ITEM_EXTRA_QUESTIONNAIRE_FOLDER_NAME_KEY);
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        List<FiRegistryActionQuestionnaireStatusMetaModel> resultModels = new ArrayList<>();

        List<NodeRepresentation> fiRegistrySubObjectChildren = client.getNodesAPI().listNodeChildrenCall(fiRegistrySubObjectId).getObjects();
        if (fiRegistrySubObjectChildren != null && !fiRegistrySubObjectChildren.isEmpty()) {
            for (NodeRepresentation fiRegistrySubObjectChild : fiRegistrySubObjectChildren) {
                if (fiRegistrySubObjectChild.isFolder() && fiRegistrySubObjectChild.getName().equalsIgnoreCase(extraQuestionnaireFolderName)) {
                    ResultPaging<NodeRepresentation> extraQuestionnaires = client.getNodesAPI().listNodeChildrenCall(fiRegistrySubObjectChild.getId(),
                            null, null, null,
                            "(nodeType=" + EcmConstants.ACTION_QUESTIONNAIRE_TYPE_QUESTIONNAIRE + ")",
                            new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null, null, null);

                    resultModels = FiRegistryActionQuestionnaireStatusMetaModelHelper.getModels(extraQuestionnaires);
                    for (FiRegistryActionQuestionnaireStatusMetaModel model : resultModels) {
                        model.setSubTypeQuestionnaire(true);
                    }

                    break;
                }
            }
        }

        PaginatedListWrapper<FiRegistryActionQuestionnaireStatusMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(resultModels.size());

        return listWrapper;
    }

    public List<FiRegistryActionQuestionnaireStatusMetaModel> updateFiRegistrySubObjectExtraQuestionnaire(List<Map<String, Object>> extraQuestionnaires) throws NodeException {
        return createOrUpdateFiRegistrySubObjectExtraQuestionnaire(null, extraQuestionnaires, true);
    }

    public List<FiRegistryActionQuestionnaireStatusMetaModel> createFiRegistrySubObjectExtraQuestionnaire(String extraQuestionnaireFolderId, List<Map<String, Object>> extraQuestionnaires) throws NodeException {
        return createOrUpdateFiRegistrySubObjectExtraQuestionnaire(extraQuestionnaireFolderId, extraQuestionnaires, false);
    }

    private String getLastActionFolderId(AlfrescoClient client, String fiRegistryId) {
        NodeRepresentation registryNode = client.getNodesAPI().getNodeCall(fiRegistryId, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)).toString(), null, null);
        if (registryNode != null && registryNode.getProperties() != null && !registryNode.getProperties().isEmpty()) {
            return FirstUtil.getValue(registryNode.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);
        }
        return null;
    }

    public List<FiRegistryActionQuestionnaireStatusMetaModel> getNodeSubQuestionnairesByAction(AlfrescoClient client, String nodeId, List<String> relativePaths, String fiRegistryActionId) {
        List<FiRegistryActionQuestionnaireStatusMetaModel> result = new ArrayList<>();
        String groupNameSeparator = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.QUESTIONNAIRE_UNIQUE_NAME_PART_SEPARATOR_KEY);
        groupNameSeparator = (groupNameSeparator != null && !groupNameSeparator.trim().isEmpty() ? groupNameSeparator.trim() : " - ");

        String complexStructureFolderName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_COMPLEX_STRUCTURE_FOLDER_NAME_KEY);

        List<NodeRepresentation> childrenNodes = client.getNodesAPI().listNodeChildrenCall(nodeId, null, null, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null, null, null).getObjects();
        List<String> uniqueGroupNames = new ArrayList<>();
        for (NodeRepresentation node : childrenNodes) {
            String nodeName = node.getName();
            if (relativePaths.contains(complexStructureFolderName) && nodeName.equalsIgnoreCase(complexStructureFolderName)) { // tree: complex structure
                result.addAll(getNodeChildrenQuestionnaires(client, node, groupNameSeparator, true, EcmConstants.COMPLEX_STRUCTURE_PROP_TYPE, uniqueGroupNames, fiRegistryActionId));
            } else if (relativePaths.contains(nodeName)) {
                result.addAll(getNodeChildrenQuestionnaires(client, node, groupNameSeparator, uniqueGroupNames, fiRegistryActionId));
            }
        }
        return result;
    }

    public List<FiRegistryActionQuestionnaireStatusMetaModel> getNodeQuestionnaires(AlfrescoClient client, String nodeId, String rootId, String fiRegistryActionId) {

        NodeRepresentation nodeRepresentation = client.getNodesAPI().getNodeCall(nodeId);
        NodeRepresentation rootNode = client.getNodesAPI().getNodeCall(rootId);
        String nodeType = nodeRepresentation.getNodeType();

        String groupNameSeparator = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.QUESTIONNAIRE_UNIQUE_NAME_PART_SEPARATOR_KEY);
        groupNameSeparator = (groupNameSeparator != null && !groupNameSeparator.trim().isEmpty() ? groupNameSeparator.trim() : " - ");

        List<String> uniqueGroupNames = new ArrayList<>();

        List<ClassPropertyRepresentation> questionnaireClassProperties = client.getDictionaryAPI().getClassPropertiesCall(nodeType.replace(":", "_"));
        Map<String, List<String>> questionnairePropertyNamesMap = getConstraintPropertiesNamesMap(client, nodeType, "Questions");
        Map<String, List<String>> questionnaireNameFieldsPropertyNamesMap = getConstraintPropertiesNamesMap(client, nodeType, "UniqueNameCombinationFields");

        Map<String, Map<String, ClassPropertyRepresentation>> questionnairesClassRepresentationsMap = getQuestionnaireClassPropertyRepresentationsMap(questionnaireClassProperties, questionnairePropertyNamesMap);

        return extractQuestion(
                nodeRepresentation,
                nodeType,
                questionnaireClassProperties,
                questionnairePropertyNamesMap,
                questionnaireNameFieldsPropertyNamesMap,
                client,
                questionnairesClassRepresentationsMap,
                fiRegistryActionId,
                groupNameSeparator,
                false,
                null,
                uniqueGroupNames,
                rootNode
        );
    }

    private List<FiRegistryActionQuestionnaireStatusMetaModel> getNodeChildrenQuestionnaires(AlfrescoClient client, NodeRepresentation rootNode, String groupNameSeparator, List<String> uniqueGroupNames, String fiRegistryActionId) {
        return getNodeChildrenQuestionnaires(client, rootNode, groupNameSeparator, false, null, uniqueGroupNames, fiRegistryActionId);
    }

    private List<FiRegistryActionQuestionnaireStatusMetaModel> getNodeChildrenQuestionnaires(AlfrescoClient client, NodeRepresentation rootNode, String groupNameSeparator, boolean isTree, String treeNodeItemTypePropertyName, List<String> uniqueGroupNames, String fiRegistryActionId) {
        List<FiRegistryActionQuestionnaireStatusMetaModel> result = new ArrayList<>();

        List<? extends NodeRepresentation> childrenNodes = !isTree ? client.getNodesAPI().listNodeChildrenCall(rootNode.getId(), null, null, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null, null, null).getObjects()
                : getChildrenRecursive(client, rootNode.getId());

        if (childrenNodes != null && !childrenNodes.isEmpty()) {
            String nodeType = childrenNodes.get(0).getNodeType();

            List<ClassPropertyRepresentation> questionnaireClassProperties = client.getDictionaryAPI().getClassPropertiesCall(nodeType.replace(":", "_"));
            Map<String, List<String>> questionnairePropertyNamesMap = getConstraintPropertiesNamesMap(client, nodeType, "Questions");
            Map<String, List<String>> questionnaireNameFieldsPropertyNamesMap = getConstraintPropertiesNamesMap(client, nodeType, "UniqueNameCombinationFields");

            Map<String, Map<String, ClassPropertyRepresentation>> questionnairesClassRepresentationsMap = getQuestionnaireClassPropertyRepresentationsMap(questionnaireClassProperties, questionnairePropertyNamesMap);

            for (NodeRepresentation childNode : childrenNodes) {

                result.addAll(extractQuestion(
                        childNode,
                        nodeType,
                        questionnaireClassProperties,
                        questionnairePropertyNamesMap,
                        questionnaireNameFieldsPropertyNamesMap,
                        client,
                        questionnairesClassRepresentationsMap,
                        fiRegistryActionId,
                        groupNameSeparator,
                        isTree,
                        treeNodeItemTypePropertyName,
                        uniqueGroupNames,
                        rootNode
                ));
            }
        }

        return result;
    }

    private List<FiRegistryActionQuestionnaireStatusMetaModel> extractQuestion(NodeRepresentation node,
                                                                               String nodeType,
                                                                               List<ClassPropertyRepresentation> questionnaireClassProperties,
                                                                               Map<String, List<String>> questionnairePropertyNamesMap,
                                                                               Map<String, List<String>> questionnaireNameFieldsPropertyNamesMap,
                                                                               AlfrescoClient client,
                                                                               Map<String, Map<String, ClassPropertyRepresentation>> questionnairesClassRepresentationsMap,
                                                                               String fiRegistryActionId,
                                                                               String groupNameSeparator,
                                                                               boolean isTree,
                                                                               String treeNodeItemTypePropertyName,
                                                                               List<String> uniqueGroupNames,
                                                                               NodeRepresentation rootNode
    ) {
        if (!node.getNodeType().equals(nodeType)) { // For cases, when children nodes can have different types (e.g Branches)
            nodeType = node.getNodeType();
            questionnaireClassProperties = client.getDictionaryAPI().getClassPropertiesCall(nodeType.replace(":", "_"));
            questionnairePropertyNamesMap = getConstraintPropertiesNamesMap(client, nodeType, "Questions");
            questionnaireNameFieldsPropertyNamesMap = getConstraintPropertiesNamesMap(client, nodeType, "UniqueNameCombinationFields");

            questionnairesClassRepresentationsMap = getQuestionnaireClassPropertyRepresentationsMap(questionnaireClassProperties, questionnairePropertyNamesMap);

        }

        ResultPaging<VersionRepresentation> nodeVersions = client.getVersionAPI().listVersionHistoryCall(node.getId(), null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null);

        VersionRepresentation nodeVersion = getLastNodeVersionInAction(fiRegistryActionId, nodeVersions.getObjects());

        if (nodeVersion == null) {
            return new ArrayList<>();
        }

        List<FiRegistryActionQuestionnaireStatusMetaModel> midResult = new ArrayList<>();

        Map<String, Object> childNodeProperties = nodeVersion.getProperties();

        String questionnairePropertyNameKey = nodeVersion.getNodeType() + "Questions";
        String questionnaireUniqueNamePropertyKey = nodeVersion.getNodeType() + "UniqueNameCombinationFields";

        if (isTree) {
            String complexStructureType = FirstUtil.getValue(childNodeProperties.get(treeNodeItemTypePropertyName), String.class);
            questionnairePropertyNameKey = nodeVersion.getNodeType() + complexStructureType + "Questions";
            questionnaireUniqueNamePropertyKey = nodeVersion.getNodeType() + complexStructureType + "UniqueNameCombinationFields";
        }

        List<String> questionnairePropertyNames = questionnairePropertyNamesMap.get(questionnairePropertyNameKey);
        List<String> questionnaireUniqueNameCombinationFieldsPropertyNames = questionnaireNameFieldsPropertyNamesMap.get(questionnaireUniqueNamePropertyKey);
        Map<String, ClassPropertyRepresentation> questionnaireClassRepresentationsMap = questionnairesClassRepresentationsMap.get(questionnairePropertyNameKey);

        String questionnaireTypeGroup = null;
        if (questionnairePropertyNames != null && !questionnairePropertyNames.isEmpty() && questionnaireClassRepresentationsMap != null && !questionnaireClassRepresentationsMap.isEmpty()) {
            questionnaireTypeGroup = rootNode.getName() + ": " + node.getId();
            if (questionnaireUniqueNameCombinationFieldsPropertyNames != null && !questionnaireUniqueNameCombinationFieldsPropertyNames.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String questionnaireNameFieldPart : questionnaireUniqueNameCombinationFieldsPropertyNames) {
                    String namePart = FirstUtil.getValue(childNodeProperties.get(questionnaireNameFieldPart), String.class);
                    if (namePart != null) {
                        sb.append(MessagesUtil.getString(namePart.trim())).append(" ").append(groupNameSeparator.trim()).append(" ");
                    }
                }

                String uniqueGroupName = sb.toString();
                if (!uniqueGroupName.isEmpty()) {
                    questionnaireTypeGroup = rootNode.getName() + ": " + uniqueGroupName.substring(0, uniqueGroupName.length() - (2 + groupNameSeparator.trim().length()));
                }
            }


            for (String questionnairePropertyName : questionnairePropertyNames) {
                FiRegistryActionQuestionnaireStatusMetaModel model = new FiRegistryActionQuestionnaireStatusMetaModel();

                if (childNodeProperties.get(questionnairePropertyName) != null) {
                    Boolean status = FirstUtil.getValue(childNodeProperties.get(questionnairePropertyName), Boolean.class);
                    model.setStatus(status != null && status ? "OK" : "NO");
                } else {
                    model.setStatus("NONE");
                }

                ClassPropertyRepresentation questionnairePropertyRepresentation = questionnaireClassRepresentationsMap.get(questionnairePropertyName);
                model.setId(UUID.randomUUID().toString());
                model.setNote(FirstUtil.getValue(childNodeProperties.get(questionnairePropertyName + "Note"), String.class));
                model.setQuestionnaireTypeGroup(questionnaireTypeGroup);
                model.setQuestion(questionnairePropertyRepresentation.getTitle());
                model.setPredefined(true);
                model.setSubTypeQuestionnaire(true);
                model.setQuestionnairePropertyName(questionnairePropertyName);
                midResult.add(model);
                model.setRelativeNode(node);
            }

            // extra questionnaires
            if (nodeVersion.getIsFolder()) {
                List<FiRegistryActionQuestionnaireStatusMetaModel> extraQuestionnaires = getFiRegistrySubObjectExtraQuestionnaires(node.getId()).getList();
                for (FiRegistryActionQuestionnaireStatusMetaModel extraQuestionnaire : extraQuestionnaires) {
                    extraQuestionnaire.setQuestionnaireTypeGroup(questionnaireTypeGroup);
                    midResult.add(extraQuestionnaire);
                }
            }

            // add to result and check if item name combination is duplicated
            if (!midResult.isEmpty()) {
                int groupNameOccurrence = Collections.frequency(uniqueGroupNames, questionnaireTypeGroup);
                if (groupNameOccurrence != 0) {
                    for (FiRegistryActionQuestionnaireStatusMetaModel fiRegistryActionQuestionnaireStatusMetaModel : midResult) {
                        fiRegistryActionQuestionnaireStatusMetaModel.setQuestionnaireTypeGroup(questionnaireTypeGroup + " [" + groupNameOccurrence + "]");
                    }
                }
                uniqueGroupNames.add(questionnaireTypeGroup);
                return midResult;
            }
        }
        return new ArrayList<>();
    }

    private List<NodeRepresentation> getChildrenRecursive(AlfrescoClient client, String rootNodeId) {
        List<NodeRepresentation> result = new ArrayList<>();

        List<NodeRepresentation> children = client.getNodesAPI().listNodeChildrenCall(rootNodeId, null, null,
                null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)),
                null, null, null).getObjects();

        if (children != null && !children.isEmpty()) {
            for (NodeRepresentation child : children) {
                result.add(child);
                if (child.isFolder()) {
                    result.addAll(getChildrenRecursive(client, child.getId()));
                }
            }
        }

        return result;
    }

    private Map<String, List<String>> getConstraintPropertiesNamesMap(AlfrescoClient client, String nodeType, String propertyNameSuffix) {
        List<ClassPropertyRepresentation> classProperties = client.getDictionaryAPI().getClassPropertiesCall(nodeType.replace(":", "_"));
        Map<String, List<String>> propertyNamesMap = new HashMap<>();

        for (ClassPropertyRepresentation classProperty : classProperties) {
            if (classProperty.getName().endsWith(propertyNameSuffix)) {
                propertyNamesMap.put(classProperty.getName(), FirstUtil.getPropertyListValues(classProperty.getConstraints()));
            }
        }

        return propertyNamesMap;
    }

    private VersionRepresentation getLastNodeVersionInAction(String actionId, List<VersionRepresentation> nodeVersions) {
        for (VersionRepresentation v : nodeVersions) {
            if (actionId.equals(v.getProperties().get(EcmConstants.NODE_PROP_FI_REGISTRY_ACTION_ID))) {
                return v;
            }
        }
        return null;
    }

    private TreeMap<String, Object> getExtraQuestionnaireProperties(Map<String, Object> extraQuestionnaire) {
        Boolean extraQuestionnaireStatus = FirstUtil.getValue(extraQuestionnaire.get("status"), Boolean.class);
        String extraQuestionnaireStatusText = (extraQuestionnaireStatus != null ? extraQuestionnaireStatus ? "OK" : "NO" : "NONE");

        TreeMap<String, Object> extraQuestionnaireProperties = new TreeMap<>();
        extraQuestionnaireProperties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_QUESTION, FirstUtil.getValue(extraQuestionnaire.get("question"), String.class));
        extraQuestionnaireProperties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_NOTE, FirstUtil.getValue(extraQuestionnaire.get("note"), String.class));
        extraQuestionnaireProperties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS, extraQuestionnaireStatusText);
        extraQuestionnaireProperties.put(EcmConstants.ACTION_QUESTIONNAIRE_PROP_PREDEFINED, false);

        return extraQuestionnaireProperties;
    }

    private List<FiRegistryActionQuestionnaireStatusMetaModel> createOrUpdateFiRegistrySubObjectExtraQuestionnaire(String extraQuestionnaireFolderId, List<Map<String, Object>> extraQuestionnaires, boolean isUpdate) throws NodeException {
        List<FiRegistryActionQuestionnaireStatusMetaModel> resultModels = new ArrayList<>();
        if (extraQuestionnaires != null && !extraQuestionnaires.isEmpty()) {
            AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();

            List<NodeRepresentation> resultNodeRepresentations = new ArrayList<>();
            for (Map<String, Object> extraQuestionnaire : extraQuestionnaires) {
                TreeMap<String, Object> extraQuestionnaireProperties = getExtraQuestionnaireProperties(extraQuestionnaire);

                if (isUpdate) {
                    String id = FirstUtil.getValue(extraQuestionnaire.get("id"), String.class);

                    NodeBodyUpdate extraQuestionnaireNodeBodyUpdate = new NodeBodyUpdate();
                    extraQuestionnaireNodeBodyUpdate.setProperties(extraQuestionnaireProperties);
                    NodeRepresentation extraQuestionnaireNodeRepresentation = nodeProxySession.updateNode(id, extraQuestionnaireNodeBodyUpdate, client);
                    resultNodeRepresentations.add(extraQuestionnaireNodeRepresentation);
                } else {
                    NodeBodyCreate extraQuestionnaireNodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.ACTION_QUESTIONNAIRE_TYPE_QUESTIONNAIRE);
                    extraQuestionnaireNodeBodyCreate.setProperties(extraQuestionnaireProperties);

                    NodeRepresentation extraQuestionnaireNodeRepresentation = nodeProxySession.createNode(extraQuestionnaireFolderId, extraQuestionnaireNodeBodyCreate, client);
                    resultNodeRepresentations.add(extraQuestionnaireNodeRepresentation);
                }
            }

            ResultPaging<NodeRepresentation> extraQuestionnairesResultPaging = new ResultPaging<>();
            extraQuestionnairesResultPaging.setObjects(resultNodeRepresentations);
            resultModels = FiRegistryActionQuestionnaireStatusMetaModelHelper.getModels(extraQuestionnairesResultPaging);
        }

        return resultModels;
    }

    private Map<String, Map<String, ClassPropertyRepresentation>> getQuestionnaireClassPropertyRepresentationsMap(List<ClassPropertyRepresentation> questionnaireClassPropertyRepresentations, Map<String, List<String>> questionnairePropertyNamesMap) {
        Map<String, Map<String, ClassPropertyRepresentation>> result = new TreeMap<>();
        if (questionnaireClassPropertyRepresentations != null && !questionnaireClassPropertyRepresentations.isEmpty() && questionnairePropertyNamesMap != null && !questionnairePropertyNamesMap.isEmpty()) {

            for (Map.Entry<String, List<String>> entry : questionnairePropertyNamesMap.entrySet()) {
                Map<String, ClassPropertyRepresentation> middleResult = new TreeMap<>();
                for (ClassPropertyRepresentation questionnaireClassPropertyRepresentation : questionnaireClassPropertyRepresentations) {
                    if (entry.getValue().contains(questionnaireClassPropertyRepresentation.getName())) {
                        middleResult.put(questionnaireClassPropertyRepresentation.getName(), questionnaireClassPropertyRepresentation);
                    }
                }
                result.put(entry.getKey(), middleResult);
            }

        }
        return result;
    }
}
