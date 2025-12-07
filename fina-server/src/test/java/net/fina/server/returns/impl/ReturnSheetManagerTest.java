package net.fina.server.returns.impl;

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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class ReturnSheetManagerTest {
    private final static String[] SAMPLE_NODE_NAMES = {
            "Baaka Tsutkhashvili",
            "Vamekh Goiati",
            "Rati Bertyvi",
            "Shotari Jantbelidze",
            "Qvemex Goliati",
            "Nikoloz Gochiashvili",
            "Tengij Merabishvili",
            "Udescriptiones",
            "Testadze Tazo",
            "Unamesi",
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
        initTable();
        initProcessItems();
        initReturnData();
    }

    private void initReturnData() {
        returnData = new RDataMetaModel();
        returnData.setReturnId(1);
        returnData.setVersionId(1);
        returnData.setPeriodId(1);
        returnData.setFromDate(new Date());
        returnData.setToDate(new Date());
        returnData.setFiId(1);
        returnData.setFiCode("blah01");
        returnData.setFiDescription("Blah Bank of Georgia");
        returnData.setDefinitionId(1);
        returnData.setReturnCode("RET01");
        returnData.setReturnDescription("Return name");
        returnData.setStatus(ProcessStatus.STATUS_PROCESSED);
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
            item.idByRowNumber.put(1, 1L);
            item.values.put(1, "123");
            item.nValues.put(1, 123.0);
            item.description = n.getDescription().getDescription(LANG_ID);

            processItemMap.put(item.nodeId, item);
        }
    }

    private void initNodes() {
        allMdtNodes = new HashMap<>();

        root = new MDTNode();
        root.setId(1);
        root.setDescription(new Description(LANG_ID, 0, "Root"));
        root.setDescription(new Description(1, 1, "Root"));

        List<MDTNode> columns = new ArrayList<>();

        for (int i = 2; i < 5; i++) {
            MDTNode n = create(i, root.getId(), MDTNodeTypes.NODE);
            columns.add(n);
        }
        allMdtNodes.put(root.getId(), columns);

        for (MDTNode column : columns) {
            List<MDTNode> rows = new ArrayList<>();
            for (int j = 5; j < 8; j++) {
                MDTNode n = create(j, column.getId(), MDTNodeTypes.NODE);
                rows.add(n);
            }
            allMdtNodes.put(column.getId(), rows);
        }
    }

    private MDTNode create(int index, long parent, MDTNodeTypes type) {
        MDTNode n = new MDTNode();
        n.setId(index);
        n.setCode("n" + index);
        n.setParentId(parent);
        n.setDescription(new Description(LANG_ID, 0, SAMPLE_NODE_NAMES[(int) (Math.random() * 10)]));
        n.setType(type);
        n.setEvalMethod(MDTNodeEvalMethods.UNKNOWN);

        tmp.add(n);

        return n;
    }

    private void initTable() {
        table = new DefinitionTable();
        table.setNode(root);
        table.setType(ReturnTableType.MCT);
        table.setEvalType(MDTNodeEvalMethods.UNKNOWN);
    }

    @Test
    public void test() throws Exception {
        AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");
        AooWriterBase odfToolkitReader = factory.getAooWriter(Files.readAllBytes(new File("./src/test/resources/ReturnReview/template_SGL.ods").toPath()));

        ReturnTableProcessorBase processorBase = new ReturnTableProcessor(allMdtNodes);
        RTableMetaModel tableMetaModel = processorBase.process(table, processItemMap, LANG_ID, returnData);
        returnData.setTables(Collections.singletonList(tableMetaModel));

        ProcessReturnInfo returnInfo = new ProcessReturnInfo();
        returnInfo.setLangId(1L);
        returnInfo.setNumberFormat("#.#");
        returnInfo.setDateFormat("dd-MM-yyyy");
        returnInfo.setDateTimeFormat("dd-MM-yyyy HH:mm:ss");

        ReturnSheetManager sheetManager = new ReturnSheetManager(odfToolkitReader, returnData, returnInfo);
        sheetManager.execute();

        Files.write(new File("./target/template_" + UUID.randomUUID().toString() + "_.ods").toPath(), odfToolkitReader.getSpreadsheetDocument());
    }

}
