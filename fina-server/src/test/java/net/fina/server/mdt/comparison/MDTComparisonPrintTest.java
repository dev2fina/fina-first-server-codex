package net.fina.server.mdt.comparison;


import net.fina.common.client.mdt.MDTComparisonConditions;
import net.fina.common.shared.ContentModel;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.mdt.ProcessStage;
import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.util.MDTComparisonPrintUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MDTComparisonPrintTest {
    private String outFolderPath;
    private List<MDTComparison> MDTComparisons;
    LanguageSampleModel languageMetaModel = new LanguageSampleModel();

    @Before
    public void before() throws Exception {
        languageMetaModel.setCode("en_US");
        languageMetaModel.setId(1L);
        outFolderPath = "./target/MDTComparisonPrint/";
        File outFile = new File(outFolderPath);
        if (!outFile.exists()) {
            outFile.mkdir();
        }

        MDTComparisons = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            MDTComparisons.add(createMDTComparison(i));
        }
    }


    @Test
    public void testMDTComparisonPrintXlsx() throws Exception {
        printMDTComparisonByType();
    }


    private void printMDTComparisonByType() throws Exception {
        ContentModel contentModel = MDTComparisonPrintUtil.generateFile( MDTComparisons, "", languageMetaModel);
        byte[] content = contentModel.getContent();

        String outFileName = outFolderPath + "convert_"
                + new SimpleDateFormat("dd_HH_mm_ss").format(new Date()) + "." + "xlsx".toLowerCase();
        Files.write(new File(outFileName).toPath(), content);
    }


    private MDTComparison createMDTComparison(int index) {
        MDTComparison comparison = new MDTComparison();
        comparison.setId(index);

        MDTNode node = new MDTNode();
        node.setId(index);
        node.setCode("NODE_" + index);
        node.setDescription(new Description(1, 1, "Node" + index));
        comparison.setNode(node);

        comparison.setCondition(MDTComparisonConditions.EQUALS);
        comparison.setLeftEquation("return tree.lookup(\"NODE_" + index + "\");");
        comparison.setRightEquation("return tree.lookup(\"NODE_" + index + "_ALT\");");
        comparison.setTemplate("Comparison Template " + index);
        comparison.setNumberPattern("PATTERN_" + index);
        comparison.setVersion(1);
        comparison.setProcessStage(ProcessStage.DEFAULT);
        comparison.setRowNumber(index);
        comparison.setValue("Value_" + index);
        return comparison;
    }


}
