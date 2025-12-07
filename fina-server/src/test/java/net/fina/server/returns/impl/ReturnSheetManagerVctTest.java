package net.fina.server.returns.impl;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.odstoolkit.AbstractFactory;
import net.fina.odstoolkit.FactoryProducer;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.model.RDataMetaModel;
import net.fina.server.returns.model.RTableMetaModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.*;

public class ReturnSheetManagerVctTest {
    private final static String[] SAMPLE_NODE_NAMES = {
            "Column 1",
            "Column 2"
    };

    private static final int LANG_ID = 1;

    private List<MDTNode> tmp = new ArrayList<>();

    private MDTNode root;
    private Map<Long, List<MDTNode>> allMdtNodes;
    private DefinitionTable table;
    private Map<Long, ProcessItem> processItemMap;
    private RDataMetaModel returnData;

    @Before
    public void before() {
        initNodes();
        initReturnData();
    }

    private void initNodes() {
        allMdtNodes = new HashMap<>();

        root = new MDTNode();
        root.setId(1);
        root.setDescription(new Description(LANG_ID, 0, "Root"));

        List<MDTNode> rows = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MDTNode n = create(i + 2, root.getId(), MDTNodeTypes.INPUT);
            rows.add(n);
        }

        allMdtNodes.put(root.getId(), rows);
    }

    private void initReturnData() {
        returnData = new RDataMetaModel();
        returnData.setReturnId(1);
        returnData.setVersionId(1);
        returnData.setPeriodId(1);
        returnData.setFromDate(new Date());
        returnData.setToDate(new Date());
        returnData.setFiId(1);
        returnData.setFiCode("demo");
        returnData.setFiDescription("Demo bank");
        returnData.setDefinitionId(1);
        returnData.setReturnCode("RET01");
        returnData.setReturnDescription("Demo VCT Return");
        returnData.setReturnTypeCode("RET_TYPE_1");
        returnData.setStatus(ProcessStatus.STATUS_PROCESSED);
        returnData.setStatusName(ProcessStatus.STATUS_PROCESSED.name());
        returnData.setPeriodTypeCode("MONTHLY");
        returnData.setPeriodTypeDescription("Monthly Returns");
        returnData.setUserLogin("TEST");
        returnData.setVersionCode("ORIG_TEST");
        returnData.setVersionDescription("ORIG TEST");
        returnData.setUserName("Unit Test User");
    }

    private MDTNode create(int index, long parent, MDTNodeTypes type) {
        MDTNode n = new MDTNode();
        n.setId(index);
        n.setCode("n" + index);
        n.setParentId(parent);
        n.setDescription(new Description(LANG_ID, 0, SAMPLE_NODE_NAMES[index - 2]));
        n.setType(type);
        n.setEvalMethod(MDTNodeEvalMethods.UNKNOWN);
        n.setDataType(index == 2 ? MDTNodeDataTypes.TEXT : MDTNodeDataTypes.NUMERIC);

        tmp.add(n);

        return n;
    }

    private void initTable(long visibleLevel) {
        table = new DefinitionTable();
        table.setNode(root);
        table.setType(ReturnTableType.VCT);
        table.setEvalType(MDTNodeEvalMethods.SUM);
        table.setVisibleLevel(visibleLevel);

        initProcessItems();
    }

    private void initProcessItems() {
        processItemMap = new HashMap<>();

        for (MDTNode n : tmp) {
            ProcessItem item = new ProcessItem();
            item.nodeId = n.getId();
            item.parentId = n.getParentId();
            item.code = n.getCode();
            item.nodeType = n.getType();
            item.dataType = n.getDataType();
            item.tableType = table.getType();
            item.nodeEvalMethod = table.getEvalType();

            for (int i = 0; i < 2; i++) {
                item.idByRowNumber.put(i, 1L);
                item.values.put(i, "123");
                item.nValues.put(i, 123.0);
            }

            processItemMap.put(item.nodeId, item);
        }
    }

    @Test
    public void testTotalTop() throws Exception {
        initTable(11);

        ReturnTableProcessorBase processorBase = new ReturnTableProcessor(allMdtNodes);
        RTableMetaModel tableMetaModel = processorBase.process(table, processItemMap, LANG_ID, returnData);

        Assert.assertEquals(tableMetaModel.getRows().size(), 3);
        Assert.assertEquals(tableMetaModel.getRows().get(0).getRowItems().size(), 2);

        returnData.setTables(Collections.singletonList(tableMetaModel));

        AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");
        AooWriterBase odfToolkitReader = factory.getAooWriter(Files.readAllBytes(new File("./src/test/resources/net.fina.server.returns.impl/vct_format_template_total_top.ods").toPath()));

        ProcessReturnInfo returnInfo = new ProcessReturnInfo();
        returnInfo.setLangId(1L);
        returnInfo.setNumberFormat("#.#");
        returnInfo.setDateFormat("dd-MM-yyyy");
        returnInfo.setDateTimeFormat("dd-MM-yyyy hh:mm:ss");

        ReturnSheetManager sheetManager = new ReturnSheetManager(odfToolkitReader, returnData, returnInfo);
        sheetManager.execute();

        Files.write(new File("./target/template_vct_total_top_" + UUID.randomUUID().toString() + "_.ods").toPath(), odfToolkitReader.getSpreadsheetDocument());
    }


    @Test
    public void testTotalBottom() throws Exception {
        initTable(10);

        ReturnTableProcessorBase processorBase = new ReturnTableProcessor(allMdtNodes);
        RTableMetaModel tableMetaModel = processorBase.process(table, processItemMap, LANG_ID, returnData);

        Assert.assertEquals(tableMetaModel.getRows().size(), 3);
        Assert.assertEquals(tableMetaModel.getRows().get(0).getRowItems().size(), 2);

        returnData.setTables(Collections.singletonList(tableMetaModel));

        AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");
        AooWriterBase odfToolkitReader = factory.getAooWriter(Files.readAllBytes(new File("./src/test/resources/net.fina.server.returns.impl/vct_format_template.ods").toPath()));

        ProcessReturnInfo returnInfo = new ProcessReturnInfo();
        returnInfo.setLangId(1L);
        returnInfo.setNumberFormat("#.#");
        returnInfo.setDateFormat("dd-MM-yyyy");
        returnInfo.setDateTimeFormat("dd-MM-yyyy hh:mm:ss");

        ReturnSheetManager sheetManager = new ReturnSheetManager(odfToolkitReader, returnData, returnInfo);
        sheetManager.execute();

        Files.write(new File("./target/template_vct_total_bottom_" + UUID.randomUUID().toString() + "_.ods").toPath(), odfToolkitReader.getSpreadsheetDocument());
    }
}
