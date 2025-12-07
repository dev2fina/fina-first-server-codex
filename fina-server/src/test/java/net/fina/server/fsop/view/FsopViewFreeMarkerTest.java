package net.fina.server.fsop.view;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.returns.model.RDataMetaModel;
import net.fina.server.returns.model.RItemMetaModel;
import net.fina.server.returns.model.RTableMetaModel;
import net.fina.server.returns.model.RTableRowMetaModel;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.*;

public class FsopViewFreeMarkerTest {

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

    private RDataMetaModel rd;

    @Before
    public void before() {
        rd = new RDataMetaModel();
        rd.setReturnId(1);
        rd.setVersionId(1);
        rd.setPeriodId(1);
        rd.setFromDate(new Date());
        rd.setToDate(new Date());
        rd.setFiId(1);
        rd.setFiCode("blah99");
        rd.setFiDescription("Blah Bank of Georgia");
        rd.setDefinitionId(1);

        List<RTableMetaModel> tables = new ArrayList<>();
        rd.setTables(tables);

        //-----------MCT---------------------------
        RTableMetaModel mct = new RTableMetaModel();
        tables.add(mct);

        mct.setTableId(1);
        mct.setDescription("MCT table");
        mct.setType(ReturnTableType.MCT);
        mct.setEvalMethod(MDTNodeEvalMethods.UNKNOWN);
        mct.setSequence(1);

        List<RTableRowMetaModel> rows = new ArrayList<>();
        mct.setRows(rows);

        long nodeId = 1;
        for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
            RTableRowMetaModel row = new RTableRowMetaModel();

            for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
                RItemMetaModel i = new RItemMetaModel();
                i.setNodeId(nodeId);
                i.setRowNumber(rowIndex);
                i.setNodeType(MDTNodeTypes.VARIABLE.ordinal());
                i.setCode("node_code_" + nodeId);
                i.setDescription(SAMPLE_NODE_NAMES[(int) (Math.random() * 10)]);
                i.setValue(Double.toString((int) (Math.random() * 1000)));

                row.getRowItems().add(i);

                nodeId++;
            }

            mct.getRows().add(row);
        }

        //-----------VCT---------------------------
        RTableMetaModel vct = new RTableMetaModel();
        tables.add(vct);

        vct.setTableId(1);
        vct.setDescription("VCT table");
        vct.setType(ReturnTableType.VCT);
        vct.setEvalMethod(MDTNodeEvalMethods.SUM);
        vct.setSequence(1);

        List<RTableRowMetaModel> vctRows = new ArrayList<>();
        vct.setRows(vctRows);

        for (int rowIndex = 0; rowIndex < 5; rowIndex++) {
            RTableRowMetaModel row = new RTableRowMetaModel();

            for (int columnIndex = 0; columnIndex < 10; columnIndex++) {
                RItemMetaModel i = new RItemMetaModel();
                i.setNodeId(nodeId);
                i.setRowNumber(rowIndex);
                i.setNodeType(MDTNodeTypes.INPUT.ordinal());
                i.setCode("node_code_" + nodeId);
                i.setDescription(SAMPLE_NODE_NAMES[(int) (Math.random() * 10)]);
                i.setValue(Double.toString((int) (Math.random() * 1000)));
                i.setDataType(MDTNodeDataTypes.NUMERIC);

                row.getRowItems().add(i);

                nodeId++;
            }

            vct.getRows().add(row);
        }
    }

    @Test
    public void test() throws TransformerException, IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File("./src/test/resources/fsop/view/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setNumberFormat("#,##0.00");

        Template temp = cfg.getTemplate("fsop.ftl");

        // Create the root hash
        Map<String, Object> root = new HashMap<>();
        root.put("return", this.rd);
        root.put("title", "FSOP review");

        Writer out = new OutputStreamWriter(new FileOutputStream("./target/test.html"));
        temp.process(root, out);

        System.out.println("End test");
    }
}
