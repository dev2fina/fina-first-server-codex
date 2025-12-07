package net.fina.server.fsop;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.fsop.impl.FsopReturnTemplateProcessor;
import net.fina.server.fsop.impl.FsopTemplateException;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.xml.Body;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.Item;
import net.fina.server.returns.xml.Return;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FsopReturnTemplateProcessorMctTest {

    private final static int DATA_SIZE = 10;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private FsopImportedReturnMetaModel importedReturn;
    private Return ret;
    private List<ProcessItem> processItems;

    @Before
    public void before() {
        importedReturn = new FsopImportedReturnMetaModel();
        importedReturn.setLanguageNumberFormat("#.#");
        importedReturn.setLanguageDateFormat("dd/mm/yyyy");
        importedReturn.setLanguageDateTimeFormat("dd/mm/yyyy hh:mm:ss");

        ret = new Return();
        ret.setHeader(new Header());

        Body body = new Body();
        body.setItems(new ArrayList<>());

        for (int i = 0; i < DATA_SIZE; i++) {
            Item item = new Item();
            item.setItemCode("code" + i);
            item.setRow(i);
            item.setValue("10");

            body.getItems().add(item);
        }

        ret.setBody(body);

        processItems = new ArrayList<>();

        for (int i = 0; i < DATA_SIZE; i++) {
            ProcessItem item = new ProcessItem();
            item.nodeId = i + 1;
            item.code = "code" + i;
            item.dataType = MDTNodeDataTypes.NUMERIC;
            item.tableType = ReturnTableType.MCT;
            item.tableId = 1;
            item.returnId = 1;
            item.nodeType = MDTNodeTypes.INPUT;
            item.tableEvalType = MDTNodeEvalMethods.UNKNOWN;
            item.nodeEvalMethod = MDTNodeEvalMethods.UNKNOWN;
            item.required = true;

            item.values.put(i, null);
            item.nValues.put(i, .0);
            item.idByRowNumber.put(i, (long) (i + 1));

            processItems.add(item);
        }
    }

    @Test
    public void testNull() {
        thrown.expect(NullPointerException.class);
        new FsopReturnTemplateProcessor(null, null, null);
    }

    @Test
    public void testNullRetAndItems() {
        new FsopReturnTemplateProcessor(importedReturn, null, null);
    }

    @Test
    public void testNullRetAndItemsProcess() {
        new FsopReturnTemplateProcessor(importedReturn, null, null).process();
    }

    @Test
    public void testEmpty() {
        FsopImportedReturnMetaModel ri = new FsopImportedReturnMetaModel();
        ri.setLanguageNumberFormat("#.#");
        ri.setLanguageDateFormat("dd/mm/yyyy");
        ri.setLanguageDateTimeFormat("dd/mm/yyyy hh:mm:ss");

        Map<Long, ProcessItem> result = new FsopReturnTemplateProcessor(ri, new Return(), new ArrayList<>()).process();

        Assert.assertEquals(result.size(), 0);

        Assert.assertEquals(ri.getStatus(), ImportStatus.IMPORTED);
        Assert.assertEquals(ri.getMessage(), "Process OK");
    }

    @Test
    public void testRequired() {
        thrown.expect(FsopTemplateException.class);
        try {
            new FsopReturnTemplateProcessor(importedReturn, new Return(), processItems).process();
        } catch (FsopTemplateException e) {
            Assert.assertEquals(e.getMessage().length(), 341);
            throw e;
        }
    }

    @Test
    public void fullTest() {
        Map<Long, ProcessItem> result = new FsopReturnTemplateProcessor(importedReturn, ret, processItems).process();

        List<Integer> rows = new ArrayList<>();

        for (ProcessItem item : result.values()) {

            for (Map.Entry<Integer, String> e : item.values.entrySet()) {
                Assert.assertEquals(e.getValue(), "10");
                rows.add(e.getKey());
            }
        }

        for (int i = 0; i < DATA_SIZE; i++) {
            assert rows.contains(i);
        }
    }
}
