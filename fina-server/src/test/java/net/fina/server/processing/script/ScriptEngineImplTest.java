package net.fina.server.processing.script;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.processing.model.Dependent;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.script.js.JSTree;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptEngineImplTest {

    private Map<String, ProcessItem> packageReturnItemsByCode;

    @Before
    public void beforTest() {

        packageReturnItemsByCode = new HashMap<>();

        final long returnId = 1;

        List<ProcessItem> items = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ProcessItem item = new ProcessItem();
            item.returnId = returnId;
            item.code = "code" + i;
            item.dataType = MDTNodeDataTypes.NUMERIC;
            item.tableId = 1;
            item.tableType = ReturnTableType.VCT;
            item.tableEvalType = MDTNodeEvalMethods.SUM;

            Dependent dependent = new Dependent();
            dependent.dependentIds = new ArrayList<>();

            item.nodeId = i;
            item.sequence = i;

            for (int j = 0; j < 5; j++) {
                item.idByRowNumber.put(j, (long) j);
                item.values.put(j, j + i + 10 + "");
            }

            items.add(item);
        }

        for (ProcessItem item : items) {
            packageReturnItemsByCode.put(item.code, item);
        }
    }

    @Test
    public void test() {
        ProcessItem item = new ProcessItem();

        JSTree jsTree = new JSTree(this.packageReturnItemsByCode, item, 1, null, null);

        String source = ScriptEngineBase.createFunction("return tree.lookup('code1')+tree.lookup('code2')+tree.lookup('code3')+tree.sumifs('code1','code2','>1')");

        String result = ScriptEngineFactory.get().call(jsTree, source);

        Assert.assertEquals(result, "200.0");

    }
}
