package net.fina.server.processing.script;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.processing.model.Dependent;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.script.js.JSTree;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptEngineImplDecimalsTest {
    private Map<String, ProcessItem> packageReturnItemsByCode;

    @Before
    public void beforeTest() {

        packageReturnItemsByCode = new HashMap<>();

        final long returnId = 1;

        List<ProcessItem> items = new ArrayList<>();

        // Create text column process items
        ProcessItem textItem = initializeProcessItem(returnId, "code0", MDTNodeDataTypes.TEXT, 1, ReturnTableType.VCT, MDTNodeEvalMethods.UNKNOWN, 0, 0);

        for (int j = 0; j < 5; j++) {
            textItem.idByRowNumber.put(j, (long) j);
        }

        textItem.values.put(0, "AU");
        textItem.values.put(1, "HK");
        textItem.values.put(2, "HN");
        textItem.values.put(3, "HR");
        textItem.values.put(4, "GE");

        items.add(textItem);

        // Add 2 number column process items
        for (int i = 1; i < 3; i++) {
            ProcessItem item = initializeProcessItem(returnId, "code" + i, MDTNodeDataTypes.NUMERIC, 1, ReturnTableType.VCT, MDTNodeEvalMethods.SUM, i, i);

            for (int j = 0; j < 5; j++) {
                item.idByRowNumber.put(j, (long) j);
            }

            item.values.put(0, "417.53");
            item.values.put(1, "225.89");
            item.values.put(2, "701.18");
            item.values.put(3, "8550.5");
            item.values.put(4, "813.65"); // sum is 10708.75

            items.add(item);
        }

        for (ProcessItem item : items) {
            packageReturnItemsByCode.put(item.code, item);
        }
    }

    private ProcessItem initializeProcessItem(long returnId, String code, MDTNodeDataTypes nodeDataType, long tableId, ReturnTableType tableType, MDTNodeEvalMethods evalMethod, long nodeId, int sequence) {
        ProcessItem processItem = new ProcessItem();
        processItem.returnId = returnId;
        processItem.code = code;
        processItem.dataType = nodeDataType;
        processItem.tableId = tableId;
        processItem.tableType = tableType;
        processItem.tableEvalType = evalMethod;
        processItem.nodeId = nodeId;
        processItem.sequence = sequence;

        Dependent dependent = new Dependent();
        dependent.dependentIds = new ArrayList<>();

        return processItem;
    }

    @Test
    public void test() {
        ProcessItem item = new ProcessItem();

        JSTree jsTree = new JSTree(this.packageReturnItemsByCode, item, 1, null, null);

        String source = ScriptEngineBase.createFunction("return tree.lookup('code1')-tree.sumif('code0','=GE','code1')+tree.lookup('code2')-tree.sumif('code0','=GE','code2')");

        String result = ScriptEngineFactory.get().call(jsTree, source);

        System.out.println(result); // result should be 19790.2, actual value is  19790.199999999997
    }
}
