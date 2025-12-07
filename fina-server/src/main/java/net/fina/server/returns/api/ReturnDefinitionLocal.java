package net.fina.server.returns.api;

import jakarta.ejb.Local;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReturnDefinitionFilter;
import net.fina.common.client.returns.DependenceModel;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.xml.v1.Mdt;
import net.fina.server.processing.dependency.DependencyItem;
import net.fina.server.returns.entity.*;

import java.util.*;

@Local
public interface ReturnDefinitionLocal {
    ReturnDefinition getDefinitionInfo(long returnId);

    HashSet<ReturnDefinition> load(Map<Integer, String> map, boolean userDef);

    void delete(List<ReturnDefinition> definitions) throws FinATypeException;

    List<DefinitionTable> loadDefinitontables(Long defId);

    List<DefinitionTable> loadAllDefinitionTables();

    ReturnDefinition save(ReturnDefinition definition) throws FinATypeException;

    DefinitionTable saveDefinitionTable(DefinitionTable definitionTable) throws FinATypeException;

    void deleteDefinitionTable(DefinitionTable definitionTable) throws FinATypeException;

    String getJson(HashMap<Integer, String> hashMap);

    List<UserReturnDefinition> userReturnDefinitions(Long userId);

    Mdt loadReturnDefinitionMdtV1(ReturnDefinition returnDefinition, Map<String, Boolean> activate, Map<Long, List<MDTComparison>> comparisons, Map<Long, String> languagesMap, Map<Long, Set<Long>> dependencies, Map<Long, List<MDTNode>> allMdtNodesByParentId);

    DefinitionTable loadReturnDefinitionTableById(long returnDefinitionId, long definitionTableId);

    void deleteReturnDefinitionTables(ReturnDefinition definition);

    String getReturnDefinitionCodeById(long id) throws FinATypeException;

    ReturnDefinitionFormat getReturnDefinitionFormat(long returnDefinitionId);

    Map<Long, ReturnDefinitionFormat> getReturnDefinitionFormatsByReturnIds(List<Long> returnDefinitionIds);

    ReturnDefinitionFormat saveReturnDefinitionFormat(ReturnDefinitionFormat returnDefinitionFormat);

    List<ReturnDefinition> load(Map<ReturnDefinitionFilter, Object> filterObjectMap);

    List<DependenceModel> getDependencies(List<ReturnDefinition> definitionList);

    Map<Long, Collection<ReturnDefinition>> getReturnDefinitionsDependencies(boolean excludeDisableReturnDefinitions, long returnDefinitionId);

    Map<Long, String> gerReturnDefinitionIds(List<String> returnDefinitionCodes);

    List<String> loadDefinitionCodes();

    List<DependencyItem> orderReturnDefinitionIds(Map<Long, Long> definitionIdReturnId);

    Map<String, Integer> orderReturnDefinitionIds(List<String> returnDefinitionCodes);

    Map<Long, Collection<Long>> loadReturnDefinitionDependenciesIds();

    void getUsedReturnDefinitions(long returnDefinitionId, Set<Long> result);

    Collection<Long> getReturnDefinitionDependencies(long returnDefinitionId);

    net.fina.server.mdt.xml.v2.Node loadReturnDefinitionMdtV2(ReturnDefinition returnDefinition, Map<String, Boolean> activate, Map<Long, List<MDTComparison>> comparisons, Map<Long, String> languagesMap, Map<Long, Set<String>> dependencies, Map<Long, List<MDTNode>> allMdtNodesByParentId);

    List<ReturnDefinition> loadReturnDefinitionsById(List<Long> ids);

    ReturnDefinition getReturnDefinitionById(long id) throws FinATypeException;

    List<ReturnDefinition> loadReturnDefinitionsByTableType(ReturnTableType type);

    long getReturnDefinitionIdByCode(String code);

    List<ReturnDefinition> loadReturnDefinitionsByReturnTypeAndTableType(Long returnTypeId, ReturnTableType tableType);

    int count(Map<ReturnDefinitionFilter, Object> filterObjectMap);

    Map<String, ReturnDefinition> loadReturnDefinitionCodeObjectMap();

    List<ReturnDefinition> loadReturnDefinitionsByReturnType(List<Long> returnTypeIds);

    List<ReturnDefinition> loadActiveReturnDefinitions();

    void reorderDefinitionTables(long definitionId, List<Long> sortedTableIds);

    Map<ReturnType, List<ReturnDefinition>> loadRTypeDefinitionMapByIds(List<Long> definitionIds);

    List<ReturnDefinition> loadReturnDefinitionsByIds(Collection<Long> ids);
}
