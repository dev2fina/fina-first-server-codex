package net.fina.server.processing.api;

import net.fina.server.i18n.entity.Language;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.JSTreeDateProcessor;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnStatus;
import net.fina.server.returns.entity.Schedule;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * nikoloz on 8/17/15.
 */
public interface ProcessingStoreLocal extends JSTreeDateProcessor {

    Language getLanguage(long langId);

    ProcessReturnModel getReturnModel(long returnId);

    Map<Long, List<MDTNode>> loadMdtNodesByParentId();

    ReturnStatus loadReturnCurrentStatus(long returnId);

    List<Long> getScheduleReturnDefinitionTableNodeIds(long scheduleId);

    List<MDTNode> getDependentReturnsNotExistingNodes(List<Long> nodes, long scheduleId, String versionCode);

    Map<Long, Long> gerReturnReturnDefinitionIds(List<Long> returnIds);

    Map<Long, Collection<ReturnDefinition>> loadReturnDefinitionDependencies();

    List<Long> loadDependentReturns(long fiId, long periodId, Collection<Long> returnDefinitions, long versionId);

    Map<Long, Long> loadDependentReturnIdDefinitionIdMap(long fiId, long periodId, Collection<Long> returnDefinitions, long versionId);

    Collection<Long> getReturnDefinitionDependencies(long returnDefinitionId);

    Map<Long, ProcessItem> loadReturnNodesValue(List<Long> returnIds, long versionId, List<MDTDependentNode> mdtDependentNodes, long langId, Map<Long, List<MDTNode>> allMdtNodesByParentId);

    Map<Long, List<ComparisonItem>> loadComparisons();

    void updateProcessItems(Collection<ProcessItem> values, long returnId, String processId, List<MDTDependentNode> mdtDependentNodes, final Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons);

    void updateProcessItems(Collection<ProcessItem> values, String processId);

    List<MDTDependentNode> loadAllMdtDependentNodes();

    Collection<Long> getUsedReturnDefinitions(long returnDefinitionId);

    long getVersionId(long returnId);

    long getScheduleId(long returnId);

    Schedule getSchedule(long returnId);

    String getVersionCode(long versionId);

    Map<Long, MDTNode> getMdtNodesById();

    Map<Long, Long> findStoredDependentReturns(List<Long> returnIds, long returnDefinitionId);

    void updateReturnDependencyTreeCache(Long returnId, List<Long> dependentReturnIds);

    List<Long> getReturnDependencyTreeByReturnId(long returnId);
}
