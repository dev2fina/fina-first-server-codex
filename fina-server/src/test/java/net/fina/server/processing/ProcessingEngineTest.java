package net.fina.server.processing;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.server.i18n.entity.Language;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.impl.ProcessingErrorHandler;
import net.fina.server.processing.model.Dependent;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;
import net.fina.server.processing.script.ScriptEngineBase;
import net.fina.server.processing.script.ScriptEngineFactory;
import net.fina.common.server.StatisticsLogger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * nikoloz on 8/18/15.
 */
public class ProcessingEngineTest {

    @Rule
    public Timeout globalTimeout = new Timeout(2000, TimeUnit.MILLISECONDS);

    @Test
    public void testCase01() {
        StatisticsLogger statisticsLogger = new StatisticsLogger("Start processing engine test case 01");
        statisticsLogger.logMessage("Start Process");
        final long returnId = 1;

        //Process items
        List<ProcessItem> items = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {

            ProcessItem item = new ProcessItem();
            item.returnId = returnId;
            item.code = "code" + i;
            item.dataType = MDTNodeDataTypes.NUMERIC;

            Dependent dependent = new Dependent();
            dependent.dependentIds = new ArrayList<>();

            item.dependent = dependent;

            item.description = "Item " + i;
            item.nodeId = i;
            item.tableId = 1;
            item.idByRowNumber.put(i, (long) i);
            item.values.put(i, (i * 10) + "");
            item.sequence = i;

            if (i == 4) {
                item.nodeType = MDTNodeTypes.VARIABLE;
                item.equation = "return tree.lookup('code1')+tree.lookup('code2')+tree.lookup('code3')";
                dependent.dependentIds.add(1L);
                dependent.dependentIds.add(2L);
                dependent.dependentIds.add(3L);
            } else {
                item.nodeType = MDTNodeTypes.INPUT;

            }
            items.add(item);
        }

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
        language.setNumberFormat("#,##0.00");
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
        ProcessResult processResult = processing.process(processItems);

        statisticsLogger.logStage("Finish process");

        Assert.assertEquals(processResult.getStatus(), ProcessStatus.STATUS_PROCESSED);

        List<ProcessItem> processedItems = new ArrayList<>(processItems);

        Assert.assertEquals(processedItems.get(0).values.get(1), "10");
        Assert.assertEquals(processedItems.get(1).values.get(2), "20");
        Assert.assertEquals(processedItems.get(2).values.get(3), "30");
        Assert.assertEquals(processedItems.get(3).values.get(4), "60.0");

        statisticsLogger.close();
    }
}
