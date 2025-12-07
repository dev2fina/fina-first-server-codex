package net.fina.server.fsop;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.fsop.impl.FsopReturnTemplateProcessor;
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

public class FsopReturnTemplateProcessorVctTest {

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
        importedReturn.setLanguageDateFormat("dd/mm/yyy");
        importedReturn.setLanguageDateTimeFormat("dd/mm/yyy hh:mm:ss");

        ret = new Return();
        ret.setHeader(new Header());

        Body body = new Body();
        body.setItems(new ArrayList<>());

        for (int i = 0; i < DATA_SIZE; i++) {
            for (int j = 0; j < 3; j++) {
                Item item = new Item();
                item.setItemCode("code" + i);
                item.setRow(j);
                item.setValue("10" + i);

                body.getItems().add(item);
            }
        }

        ret.setBody(body);

        processItems = new ArrayList<>();

        for (int i = 0; i < DATA_SIZE; i++) {
            ProcessItem item = new ProcessItem();
            item.nodeId = i + 1;
            item.code = "code" + i;
            item.dataType = MDTNodeDataTypes.NUMERIC;
            item.tableType = ReturnTableType.VCT;
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
    public void fullTest() {

        FsopReturnTemplateProcessor processor = new FsopReturnTemplateProcessor(importedReturn, ret, processItems);
        Map<Long, ProcessItem> result = processor.process();

        Assert.assertEquals(importedReturn.getMessage(), "Process OK");

        Assert.assertEquals(importedReturn.getStatus(), ImportStatus.IMPORTED);

        Assert.assertEquals(result.get(1L).values.get(2), "100");
    }
}
