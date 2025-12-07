package net.fina.server.mdt.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.MDTComparisonFilter;
import net.fina.common.client.filter.MdtFilter;
import net.fina.common.client.mdt.*;
import net.fina.common.shared.SortField;
import net.fina.server.mdt.entity.*;

import jakarta.ejb.Local;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Local
public interface MDTNodeLocal {
    List<MDTNode> loadNodes(Map<MdtFilter, Object> filter);

    List<MDTNode> loadAllNodes();

    List<MDTDependentNode> loadAllMdtDependentNodes();

    MDTNode save(MDTNode node) throws FinATypeException;

    MDTNode saveImportedNode(MDTNode node) throws FinATypeException;

    List<MDTNode> save(List<MDTNode> nodes) throws FinATypeException;

    MDTNode findByCode(String code);

    MDTNode findParentByCode(String childCode);

    boolean checkCodeUnique(MDTNode node);

    Map<Integer, ArrayList<MDTDependency>> getDependencies(Long nodeId, Long langId);

    boolean enableDisable(long id);

    List<Long> getParentIds(long nodeId);

    List<MDTNode> getMDTNodeByCodes(List<String> codes);

    MDTComparison saveComparison(MDTComparison model) throws FinATypeException;

    void deleteComparison(MDTComparison model) throws FinATypeException;

    List<MDTComparison> loadComparisons(Map<MDTComparisonFilter, Object> filter, SortField sortField);

    boolean isDepend(Long nodeId);

    Map<Long, List<MDTNode>> loadAllNodesByParentId();

    MDTNode loadMdtNodes(long parentId);

    long childrenCount(long mdtNodeId);

    List<String> changeNodeType(List<String> codes, MDTNodeTypes type);

    long getMdtMaxId();

    MDTNode loadSimpleMdtNode(long id);

    List<MDTNode> loadNodesSimple(long parentId);

    MDTNode loadNodeSimple(long nodeId);

    void reorder(MDTNode mdtNode);

    Map<Long, MDTDeleteResult> delete(List<Long> nodeIds) throws FinATypeException;

    List<MDTNode> getNodePath(long nodeId);

    long count(Map<MdtFilter, Object> filter);

    byte[] exportMdtSubtreeXML(long nodeId);

    MDTImportResult importMDTSubtree(long nodeId, byte[] bytes);

    MdtImportTranslationResult importMdtTranslation(byte[] bytes);

    Map<String, MDTNode> getMdtNodesByCode();

    List<MDTNode> loadMdtNodesByParentId(long parentId);

    void loadNodeChildren(List<Long> result, long nodeId);

    String getMdtNodeCodeById(long id);

    boolean isUsed(long nodeId);

    List<String> loadMdtItemCodesByTypes(List<MDTNodeTypes> nodeTypes);

    MDTNode findNodeById(long id);

    Map<Long, MDTNode> getMdtNodesById();

    List<MDTNode> getUserMdtNodesFromCache();

    List<MDTNode> loadNodesFromCache(Map<MdtFilter, Object> filter, long langId);

    int countFromCache(Map<MdtFilter, Object> filter, long langId);

    List<MDTNode> loadDamagedMdtNodes();

    List<MDTNode> loadRootNodesFromCache();

    List<UserMdt> loadUserRootNodes(long userId);

    List<RoleMdt> loadUserRoleRootNodes(long userId);

    Map<Long, List<MDTNode>> getAllNodesByParentIdWithLevel1Id();

    UserMdt getUserMdt(long nodeId, long userId);

    Collection<MDTNode> getMdtNodesFromCache();

    long getNodeChildMaxSequence(long nodeId);

    void checkUniqueEquationInSubTree(MDTNode catalogNode, MDTNode catalogTrashNode, String key) throws FinATypeException;

    MDTNode fixInconsistentDependency(long nodeId) throws FinATypeException;

    void moveNode(long nodeId, long parentNodeID);

    int countComparisons(Map<MDTComparisonFilter, Object> filter);

    long childrenCountWhereCodeLike(long parentId, String codeLike);

    List<MDTNode> loadDescendantNodes(long parentId);
}
