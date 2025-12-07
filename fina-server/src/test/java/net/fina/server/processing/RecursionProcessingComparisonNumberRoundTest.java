package net.fina.server.processing;

import net.fina.common.client.mdt.MDTComparisonConditions;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.server.StatisticsLogger;
import net.fina.server.i18n.entity.Language;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.impl.ProcessingErrorHandler;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.Dependent;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;
import net.fina.server.processing.script.ScriptEngineBase;
import net.fina.server.processing.script.ScriptEngineFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class RecursionProcessingComparisonNumberRoundTest {

    private final long returnId = 1;

    private List<ProcessItem> items;
    private Map<Long, List<ComparisonItem>> comparisons;

    @Before
    public void before() {

        //Process items
        items = new ArrayList<>();

        //Comparisons
        comparisons = new HashMap<>();

        ProcessItem item = new ProcessItem();
        item.returnId = returnId;
        item.code = "mdt.variable.demo.1";

        Dependent dependent = new Dependent();
        dependent.dependentIds = new ArrayList<>();

        item.dependent = dependent;

        item.description = "Demo Variable ";
        item.nodeId = 1;
        item.tableId = 1;
        item.idByRowNumber.put(0, 1L);
        item.sequence = 0;
        item.dataType = MDTNodeDataTypes.NUMERIC;
        item.values.put(0, "0.99121111");
        item.nodeType = MDTNodeTypes.INPUT;

        List<ComparisonItem> comparisonItems = new ArrayList<>();

        ComparisonItem c1 = new ComparisonItem();
        c1.condition = MDTComparisonConditions.EQUALS;
        c1.equation = "return 111.9913";
        c1.numberPattern = "#,##0.00";
        c1.nodeId = item.nodeId;
        comparisonItems.add(c1);

        ComparisonItem c2 = new ComparisonItem();
        c2.condition = MDTComparisonConditions.EQUALS;
        c2.equation = "return 111.9912";
        c2.numberPattern = "#,##0.000";
        c2.nodeId = item.nodeId;
        comparisonItems.add(c2);

        ComparisonItem c3 = new ComparisonItem();
        c3.condition = MDTComparisonConditions.EQUALS;
        c3.equation = "return 111.9912";
        c3.nodeId = item.nodeId;
        comparisonItems.add(c3);

        comparisons.put(item.nodeId, comparisonItems);

        items.add(item);
    }

    @Test
    public void test() {
        StatisticsLogger statisticsLogger = new StatisticsLogger("Start processing engine test case 01");
        statisticsLogger.logMessage("Start Process");

        final Map<Long, ProcessItem> packageReturnItemsById = new HashMap<>();
        for (ProcessItem item : items) {
            packageReturnItemsById.put(item.nodeId, item);
        }

        final Map<Long, Map<Long, ProcessItem>> packageReturnItemsByReturnId = new HashMap<>();
        final Map<String, ProcessItem> packageReturnItemsByCode = new HashMap<>();

        for (ProcessItem pItem : packageReturnItemsById.values()) {
            Map<Long, ProcessItem> returnItemsMap = packageReturnItemsByReturnId.get(pItem.returnId);
            if (returnItemsMap == null) {
                returnItemsMap = new HashMap<>();
                packageReturnItemsByReturnId.put(pItem.returnId, returnItemsMap);
            }
            returnItemsMap.put(pItem.nodeId, pItem);
            packageReturnItemsByCode.put(pItem.code, pItem);
        }

        //TODO Empty
        final Map<Long, MDTNode> allIdNodes = new HashMap<>();

        final ProcessReturnModel returnModel = new ProcessReturnModel();
        returnModel.setId(returnId);
        returnModel.setFiId(1);
        returnModel.setPeriodId(1);
        returnModel.setReturnDefinitionCode("CODE");
        returnModel.setVersionId(1);

        final Language language = new Language();
        language.setId(1);
        language.setCode("language.");
        language.setNumberFormat("#,##0.0");
        language.setDateFormat("dd/MM/yyyy");
        language.setDateTimeFormat("dd/MM/yyyy hh:mm:ss");

        statisticsLogger.logStage("Compile equations");
        packageReturnItemsByReturnId.get(returnId).values().stream().filter(item -> item.nodeType == MDTNodeTypes.VARIABLE).forEach(item -> {
            String script = ScriptEngineBase.createFunction(item.equation);
            Object cs = ScriptEngineFactory.get().find(script);
            if (cs == null) {
                ScriptEngineFactory.get().compile(script, item.code);
            }
        });

        statisticsLogger.logStage("Create Processing Base");
        ProcessingErrorHandler processingErrorHandler = new ProcessingErrorHandler(returnModel, language.getCode());
        ProcessingUtil processingUtil = new ProcessingUtil(processingErrorHandler, language.getNumberFormat().trim(), language.getDateFormat().trim(), language.getDateTimeFormat().trim(), language.getId());
        ProcessingBase processing = ProcessingFactory.createRecursionProcessing(packageReturnItemsById, packageReturnItemsByCode, allIdNodes, processingUtil, false, null, null);

        statisticsLogger.logStage("Process...");
        Collection<ProcessItem> processItems = packageReturnItemsByReturnId.get(returnId).values();
        processing.process(processItems);

        statisticsLogger.logStage("Validation...");

        ProcessResult processResult = processing.ruleValidation(processItems, comparisons);

        statisticsLogger.logMessage(processResult.getProcessNote());
        statisticsLogger.close();
    }
}
