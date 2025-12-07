package net.fina.server.processing.script.js;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.processing.model.Dependent;
import net.fina.server.processing.model.ProcessItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JSTreeTest {

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
    public void testLookup() {
        ProcessItem item = new ProcessItem();
        JSTree jsTree = new JSTree(this.packageReturnItemsByCode, item, 1, null, null);

        double value = jsTree.lookup("code1");

        System.out.println("value: " + value);

        assertEquals(65d, value, 0);
    }

    @Test
    public void testSumif() {
        ProcessItem item = new ProcessItem();
        item.returnId = 2;
        item.tableId = 1;
        item.tableType = ReturnTableType.MCT;

        JSTree jsTree = new JSTree(this.packageReturnItemsByCode, item, 1, null, null);

        double value = jsTree.sumif("code1", "11", "code2");

        System.out.println("value: " + value);

        assertEquals(12d, value, 0);
    }

    @Test
    public void testRowCount() {
        ProcessItem item = new ProcessItem();
        item.returnId = 1;
        item.tableId = 1;
        item.tableType = ReturnTableType.VCT;

        JSTree jsTree = new JSTree(this.packageReturnItemsByCode, item, 1, null, null);

        assertEquals(5, jsTree.rowCount("code1"));
    }

    @Test
    public void testSumifs() {
        print();

        ProcessItem item = new ProcessItem();
        item.returnId = 1;
        item.tableId = 2;
        item.tableType = ReturnTableType.VCT;

        JSTree jsTree = new JSTree(this.packageReturnItemsByCode, item, 1, null, null);

        double result = jsTree.sumifs("code0", "code1", ">12", "code2", "<16");

        assertEquals(25, result, 0);
    }

    private void print() {
        for (Map.Entry<String, ProcessItem> e : packageReturnItemsByCode.entrySet()) {

            System.out.println(e.getKey());

            for (Map.Entry<Integer, String> value : e.getValue().values.entrySet()) {
                System.out.println(value.getKey() + " : " + value.getValue());
            }
        }

    }

    @Test
    public void testDecimalCount() {
        ProcessItem item = new ProcessItem();
        item.returnId = 1;
        item.tableId = 1;
        item.tableType = ReturnTableType.VCT;
        item.code = "code5";
        for (int j = 0; j < 3; j++) {
        }
        item.idByRowNumber.put(1, (long) 1);
        item.values.put(1, "123.10");
        item.idByRowNumber.put(2, (long) 2);
        item.values.put(2, "23.101");
        this.packageReturnItemsByCode.put(item.code, item);

        JSTree jsTree = new JSTree(this.packageReturnItemsByCode, item, 1, null, null);
        assertEquals(2, jsTree.decimalCount());
        jsTree=new JSTree(this.packageReturnItemsByCode, item, 2, null, null);
        assertEquals(3, jsTree.decimalCount());

    }
}
