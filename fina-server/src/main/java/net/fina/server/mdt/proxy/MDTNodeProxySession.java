package net.fina.server.mdt.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.MDTComparisonFilter;
import net.fina.common.client.filter.MdtFilter;
import net.fina.common.client.mdt.*;
import net.fina.common.client.tools.mdt.tester.MdtTesterResultMetaModel;
import net.fina.common.shared.AttachmentModel;
import net.fina.common.shared.ContentModel;
import net.fina.common.shared.KeyValuePair;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.mdt.MDTComparisonModel;
import net.fina.common.shared.mdt.MDTNodeModel;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.event.MdtNodeInfoEvent;
import net.fina.server.mdt.model.MDTNodeDeleteResponse;
import net.fina.server.mdt.model.MdtNodeDependenciesModel;
import net.fina.server.mdt.model.helper.MdtNodeModelHelper;
import net.fina.server.mdt.util.MDTComparisonPrintUtil;
import net.fina.server.misc.ProductHelper;
import net.fina.server.security.api.UserLocal;
import net.fina.server.tools.api.ToolsLocal;
import net.fina.server.tools.mdt.v2.StreamedContent;
import net.fina.server.util.SortUtil;
import org.apache.commons.io.IOUtils;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class MDTNodeProxySession {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private UserLocal userLocal;

    @Inject
    private MDTNodeLocal mdtLocal;

    @Inject
    private ToolsLocal toolsLocal;

    @Inject
    private Event<MdtNodeInfoEvent> mdtEvent;

    public List<MDTNodeModel> loadRootNodesWithUserPermissions(Long userId) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        userId = userId == null ? userLocal.getCurrentUserId() : userId;

        List<MDTNodeModel> modelList = new ArrayList<>();
        Map<Long, MDTNode> userMdts = userLocal.getUserMdt(userId);
        Map<Long, MDTNode> userRoleMdts = userLocal.getUserRoleMdt(userId);

        for (MDTNode node : mdtLocal.loadRootNodesFromCache()) {
            MDTNodeModel model = MdtNodeModelHelper.toModel(node, langId);

            if (userMdts.containsKey(node.getId())) {
                model.setPermissionType(userMdts.get(node.getId()).getCanAmend() ? MDTPermissionType.AMEND : MDTPermissionType.REVIEW);
            }
            if (userRoleMdts.containsKey(node.getId())) {
                model.setPermissionType(userRoleMdts.get(node.getId()).getCanAmend() ? MDTPermissionType.AMEND : MDTPermissionType.REVIEW);
                model.setFromRole(true);
            }

            if (modelList.contains(model)) {
                MDTNodeModel existing = modelList.get(modelList.indexOf(model));
                if (!existing.isCanUserAmend()) {
                    existing.setCanUserAmend(model.isCanUserAmend());
                }
            } else {
                modelList.add(model);
            }

        }

        return modelList;
    }

    @RolesAllowed(PermissionIdNames.FINA_METADATA_REVIEW)
    public List<MDTNodeModel> loadCurrentUserRootNodes() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        long userId = userLocal.getCurrentUserId();

        Set<MDTNode> distinctMDTs = new HashSet<>();
        distinctMDTs.addAll(userLocal.getUserMdt(userId).values());
        distinctMDTs.addAll(userLocal.getUserRoleMdt(userId).values());

        List<MDTNode> rootNodes = mdtLocal.loadRootNodesFromCache();

        List<MDTNode> filteredRootNodes = rootNodes.stream().filter(distinctMDTs::contains).toList();

        return MdtNodeModelHelper.toModels(filteredRootNodes, langId);
    }


    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public List<MDTNodeModel> loadRootNodes() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<MDTNode> roots = mdtLocal.loadRootNodesFromCache();
        if (roots != null) {
            return MdtNodeModelHelper.toModels(roots, langId);
        }
        return new ArrayList<>();
    }

    public List<MDTNodeModel> loadGroupMdtRootNodes(long groupId) {
        List<MDTNodeModel> modelList = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();
        Map<Long, MDTNode> roleMdts = userLocal.getRoleMdt(Collections.singletonList(groupId));
        for (MDTNode node : mdtLocal.loadRootNodesFromCache()) {
            MDTNodeModel model = MdtNodeModelHelper.toModel(node, langId);
            if (roleMdts.containsKey(node.getId())) {
                model.setPermissionType(roleMdts.get(node.getId()).getCanAmend() ? MDTPermissionType.AMEND : MDTPermissionType.REVIEW);
            }
            modelList.add(model);
        }

        return modelList;
    }

    public List<String> loadMdtItemCodes(List<String> nodeTypesString, boolean loadAll) {
        List<MDTNodeTypes> nodeTypes = new ArrayList<>();
        if (loadAll) {
            nodeTypes = Arrays.asList(MDTNodeTypes.values());
        } else if (nodeTypesString != null) {
            nodeTypes = nodeTypesString.stream().map(t -> MDTNodeTypes.valueOf(t.toUpperCase().trim())).collect(Collectors.toList());
        }
        return mdtLocal.loadMdtItemCodesByTypes(nodeTypes);
    }

    @RolesAllowed(PermissionIdNames.FINA_METADATA_REVIEW)
    public List<MDTNodeModel> loadChildren(long parentId, Boolean fetchDisabledNodes, boolean foldersOnly) {
        List<MDTNode> damagedNodes = mdtLocal.loadDamagedMdtNodes();

        long langId = ThreadLocalHolder.getLanguage().getId();
        List<MDTNodeModel> rootModels = new ArrayList<>();

        Map<MdtFilter, Object> filterMap = new HashMap<>();

        if (fetchDisabledNodes != null && !fetchDisabledNodes) {
            filterMap.put(MdtFilter.disable, fetchDisabledNodes);
        }

        filterMap.put(MdtFilter.parentId, parentId);
        if (foldersOnly) {
            filterMap.put(MdtFilter.showLeaves, false);
        }

        for (MDTNode entity : mdtLocal.loadNodes(filterMap)) {
            MDTNodeModel model = MdtNodeModelHelper.toModel(entity, langId);
            if (damagedNodes.contains(entity)) {
                model.setDamaged(true);
            }

            rootModels.add(model);
        }

        return rootModels;
    }

    public List<Long> getNodePath(long nodeId) {
        return mdtLocal.getNodePath(nodeId).stream().map(MDTNode::getId).collect(Collectors.toList());
    }

    public List<MDTNodeModel> findItemsInCache(String code, boolean foldersOnly, List<MDTNodeTypes> nodeTypes) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Map<MdtFilter, Object> filterMap = new HashMap<>();

        if (code != null) {
            filterMap.put(MdtFilter.code, code);
        }
        filterMap.put(MdtFilter.showLeaves, !foldersOnly);
        filterMap.put(MdtFilter.TYPES, nodeTypes);
        List<MDTNode> loadedNodes = mdtLocal.loadNodesFromCache(filterMap, langId);

        List<MDTNodeModel> result = new ArrayList<>();

        for (MDTNode node : loadedNodes) {
            result.add(MdtNodeModelHelper.toModel(node, langId));
        }

        return result;
    }

    @RolesAllowed(PermissionIdNames.FINA_METADATA_AMEND)
    public MDTNodeModel save(MDTNodeModel nodeModel) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        MDTNode node = MdtNodeModelHelper.toEntity(nodeModel, langId);
        if (node.getId() <= 0) {
            node.setId(0);
            //generate unique code
            node.setCode(node.getCode().trim());
            String originalCode = node.getCode();
            if (!mdtLocal.checkCodeUnique(node)) {
                int counter = 1;
                StringBuilder newCode = new StringBuilder();
                do {
                    newCode.setLength(0);
                    newCode.trimToSize();
                    newCode.append(originalCode).append(".").append(counter++);
                    node.setCode(newCode.toString());
                } while (!mdtLocal.checkCodeUnique(node));
            }
        }

        boolean addToUserMdt = (node.getId() == 0 && node.getParentId() == 0);
        node = mdtLocal.save(node);

        if (addToUserMdt) {
            userLocal.addMtdToUser(node, userLocal.getCurrentUserId());
        }

        MDTNodeModel model = MdtNodeModelHelper.toModel(node, langId);
        model.setCanUserReview(addToUserMdt || nodeModel.isCanUserReview());
        model.setCanUserAmend(addToUserMdt || nodeModel.isCanUserAmend());

        return model;
    }

    @RolesAllowed(PermissionIdNames.FINA_METADATA_DELETE)
    public List<MDTNodeDeleteResponse> deleteNodes(List<Long> nodeIds) throws FinATypeException {
        return mdtLocal.delete(nodeIds).entrySet().stream().map((k) -> new MDTNodeDeleteResponse(k.getKey(), k.getValue())).collect(Collectors.toList());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public void importMDT(long parentNodeId, List<AttachmentModel> attachments) throws FinATypeException {

        String user = userLocal.getCurrentUserLogin();

        List<MDTImportResult> result = new ArrayList<>();
        for (AttachmentModel attachmentModel : attachments) {
            MDTImportResult importResult = mdtLocal.importMDTSubtree(parentNodeId, attachmentModel.getContent());
            if (importResult.getException() != null) {
                importResult.setMessage(importResult.getException().getMessage());
                importResult.setException(null);
            }
            result.add(importResult);
        }
        mdtEvent.fire(new MdtNodeInfoEvent(user, result));

    }

    public AttachmentModel exportMdt(long nodeId) throws FinATypeException {
        if (!ProductHelper.getInstance().isExportImportEnable()) {
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }
        byte[] file = mdtLocal.exportMdtSubtreeXML(nodeId);
        String nodeCode = mdtLocal.loadNodeSimple(nodeId).getCode().trim();

        return new AttachmentModel(nodeCode + ".mdt.xml", file);
    }

    public List<MdtImportTranslationResult> importMdtTranslation(List<AttachmentModel> attachments) {
        List<MdtImportTranslationResult> importResult = new ArrayList<>();
        for (AttachmentModel a : attachments) {
            MdtImportTranslationResult result = mdtLocal.importMdtTranslation(a.getContent());

            if (result.getException() != null) {
                result.setExceptionMessage(result.getException().getMessage());
                result.setException(null);
            }
            importResult.add(result);
        }
        return importResult;
    }

    public List<MdtTesterResultMetaModel> runMdtTester(long parentNodeId) {
        return toolsLocal.runMdtTester(parentNodeId);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    // TODO: 8/15/2024 rewrite for better performance ?
    public List<KeyValuePair<String, List<String>>> fixInconsistentMDtDependencies(List<Long> correctDependencies) {
        List<String> fixedNodeCodes = new ArrayList<>();
        List<String> failedToFixNodeCodes = new ArrayList<>();

        if (correctDependencies != null) {
            for (Long nodeId : correctDependencies) {
                fixInconsistentDependency(nodeId, fixedNodeCodes, failedToFixNodeCodes);
            }
        }

        return Arrays.asList(new KeyValuePair<>("FAILED_TO_FIX", fixedNodeCodes), new KeyValuePair<>("FIXED", failedToFixNodeCodes));

    }

    private void fixInconsistentDependency(long nodeId, List<String> updatedNodeCodes, List<String> failedToFixNodeCodes) {
        try {
            String fixedNodeCode = mdtLocal.fixInconsistentDependency(nodeId).getCode();
            updatedNodeCodes.add(fixedNodeCode);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            failedToFixNodeCodes.add(mdtLocal.getMdtNodeCodeById(nodeId));
        }
    }

    public byte[] generateXmls(List<AttachmentModel> attachments) throws Exception {
        if (attachments.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }

        StreamedContent streamedContent = toolsLocal.convert(attachments.get(0).getContent(), null, "Mdt_generator", true);

        return IOUtils.toByteArray(streamedContent.getStream());
    }

    public MdtNodeDependenciesModel loadDependencies(long nodeId) {
        Map<Integer, ArrayList<MDTDependency>> dependencies = mdtLocal.getDependencies(nodeId, ThreadLocalHolder.getLanguage().getId());
        return new MdtNodeDependenciesModel(dependencies.get(1), dependencies.get(2));
    }

    public MDTComparisonModel saveComparisonModel(MDTComparisonModel mdtComparisonModel) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        MDTComparison mdtComparison = MDTComparisonModelHelper.toEntity(mdtComparisonModel, langId);

        mdtComparison = mdtLocal.saveComparison(mdtComparison);
        return MDTComparisonModelHelper.toModel(mdtComparison, langId);
    }

    public PaginatedListWrapper<MDTComparisonModel> loadComparisons(Map<MDTComparisonFilter, Object> filter, String sortField, String sortDir) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<MDTComparisonModel> comparisonModelList = MDTComparisonModelHelper.toModels(mdtLocal.loadComparisons(filter, SortUtil.constructSortField(sortField, sortDir)), langId);

        int totalResults = comparisonModelList.size();
        if (filter.get(MDTComparisonFilter.PAGE_OFFSET) != null && filter.get(MDTComparisonFilter.PAGE_LIMIT) != null) {
            totalResults = mdtLocal.countComparisons(filter);
        }

        return new PaginatedListWrapper<>(comparisonModelList, (int) filter.get(MDTComparisonFilter.PAGE_LIMIT), totalResults);
    }


    public void deleteComparison(long comparisonId, long nodeID) throws FinATypeException {
        MDTComparison mdtComparison = new MDTComparison();

        MDTNode mdtNode = new MDTNode();
        mdtNode.setId(nodeID);

        mdtComparison.setId(comparisonId);
        mdtComparison.setNode(mdtNode);

        mdtLocal.deleteComparison(mdtComparison);
    }

    public ContentModel printMDTComparisons(Map<MDTComparisonFilter, Object> filter, String contextPath) throws FinATypeException {
        List<MDTComparison> comparisons = mdtLocal.loadComparisons(filter, null);
        return MDTComparisonPrintUtil.generateFile(comparisons, contextPath, ThreadLocalHolder.getLanguage());
    }

    public List<MDTNodeModel> pasteNodes(List<MDTNodeModel> models) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        mdtLocal.save(MdtNodeModelHelper.toEntities(models, langId));
        return models;
    }

}
