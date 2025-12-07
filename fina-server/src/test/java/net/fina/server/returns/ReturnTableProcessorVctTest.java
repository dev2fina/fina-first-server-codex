package net.fina.server.returns;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.impl.ReturnFreeMarkerTemplate;
import net.fina.server.returns.impl.ReturnTableProcessor;
import net.fina.server.returns.impl.ReturnTableProcessorBase;
import net.fina.server.returns.model.RDataMetaModel;
import net.fina.server.returns.model.RTableMetaModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class ReturnTableProcessorVctTest {
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

    private void initNodes() {
        allMdtNodes = new HashMap<>();

        root = new MDTNode();
        root.setId(1);
        root.setDescription(new Description(LANG_ID, 0, "Root"));

        List<MDTNode> rows = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MDTNode n = create(i + 2, root.getId(), MDTNodeTypes.NODE);
            rows.add(n);
        }

        allMdtNodes.put(root.getId(), rows);
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
        table.setType(ReturnTableType.VCT);
        table.setEvalType(MDTNodeEvalMethods.UNKNOWN);
    }

    @Test
    public void test() {
        ReturnTableProcessorBase processorBase = new ReturnTableProcessor(allMdtNodes);
        RTableMetaModel tableMetaModel = processorBase.process(table, processItemMap, LANG_ID, returnData);

        Assert.assertEquals(tableMetaModel.getRows().size(), 3);
        Assert.assertEquals(tableMetaModel.getRows().get(0).getRowItems().size(), 4);

    }

    @Test
    public void testFsopView() throws IOException, TemplateException {

        ReturnTableProcessorBase processorBase = new ReturnTableProcessor(allMdtNodes);
        RTableMetaModel tableMetaModel = processorBase.process(table, processItemMap, LANG_ID, returnData);
        returnData.setTables(Collections.singletonList(tableMetaModel));

        Template template = ReturnFreeMarkerTemplate.getInstance().getTemplate();

        // Create the root hash
        Map<String, Object> root = new HashMap<>();
        root.put("return", returnData);
        root.put("title", "FSOP JUnit view");

        StringWriter stringWriter = new StringWriter();
        template.process(root, stringWriter);
        System.out.println(stringWriter.toString());
    }

    @Test
    public void testEmpty() throws IOException, TemplateException {

        ReturnTableProcessorBase processorBase = new ReturnTableProcessor(allMdtNodes);
        RTableMetaModel tableMetaModel = processorBase.process(table, new HashMap<>(), LANG_ID, returnData);
        returnData.setTables(Collections.singletonList(tableMetaModel));

        Template template = ReturnFreeMarkerTemplate.getInstance().getTemplate();

        // Create the root hash
        Map<String, Object> root = new HashMap<>();
        root.put("return", returnData);
        root.put("title", "FSOP JUnit view");

        StringWriter stringWriter = new StringWriter();
        template.process(root, stringWriter);
        System.out.println(stringWriter.toString());
    }
}
