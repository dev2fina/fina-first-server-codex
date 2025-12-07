package net.fina.server.tools.mdt.v2;

import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.server.mdt.xml.v2.Node;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FormulaParser {

    private List<Node> allMdtNodes;
    private Map<String, Map<String, Node>> sheetReferenceAndCode;
    private Map<String, String> nodeCodeAndSheetName;

    public FormulaParser(List<Node> allMdtNodes, Map<String, Map<String, Node>> sheetReferenceAndCode, Map<String, String> nodeIdAndSheetName) {
        this.allMdtNodes = allMdtNodes;
        this.sheetReferenceAndCode = sheetReferenceAndCode;
        this.nodeCodeAndSheetName = nodeIdAndSheetName;
    }

    public void execute() {

        for (Node node : allMdtNodes) {
            if (node.getType() == MDTNodeTypes.VARIABLE) {

                String equation = node.getEquation();

                if (equation != null && (!equation.trim().isEmpty())) {

                    String[] references = equation.split("\\+|\\-|\\*|\\/");

                    for (int i = 0; i < references.length; i++) {
                        String ref = references[i];

                        ref = ref.replace("(", "").replace(")", "");

                        System.out.println("ref = " + ref);

                        String sheetName;
                        String cellName = ref;

                        // MBR302!E14+MBR302!G14+MBR302!J14+MBR302!K14
                        if (ref.contains("!")) {
                            String[] sheetAndCellStrings = ref.split("!");
                            sheetName = sheetAndCellStrings[0].replace("'", "");
                            cellName = sheetAndCellStrings[1];
                        } else {
                            sheetName = nodeCodeAndSheetName.get(node.getCode());
                        }

                        Map<String, Node> sheetRefs = sheetReferenceAndCode.get(sheetName);

                        if (Pattern.compile("\\$?([A-Za-z]+)\\$?([0-9]+)").matcher(cellName).matches()) {

                            if (sheetRefs == null || sheetRefs.get(cellName) == null) {
                                throw new RuntimeException("Sheet Name = " + sheetName + ", Cell = " + ref + " is required. Referenced: Node Code - " + node.getCode());
                            }

                            Node n = sheetRefs.get(cellName);
                            node.getDependentNodeCodes().add(n.getCode());

                            XSSFEvaluationWorkbook xssfew = XSSFEvaluationWorkbook.create(new XSSFWorkbook());
                            Ptg[] parsedFormula = org.apache.poi.ss.formula.FormulaParser.parse(equation, xssfew, FormulaType.CELL, 0);

                            Ptg[] newPtgs = new Ptg[parsedFormula.length];
                            for (int j = 0; j < parsedFormula.length; j++) {
                                Ptg ptg = parsedFormula[j];
                                if (ptg instanceof RefPtgBase && ptg.toFormulaString().equals(ref)) {
                                    newPtgs[j] = new StringPtg("[PLACEHOLDER]");
                                } else {
                                    newPtgs[j] = ptg;
                                }
                            }

                            equation = FormulaRenderer.toFormulaString(xssfew, newPtgs).replace("\"[PLACEHOLDER]\"",
                                    "tree.lookup(\"" + n.getCode() + "\")");
                        } else {
                            System.out.println(cellName + " - isn't cell name.");
                        }
                    }
                }

                if (equation != null) {
                    equation = "return " + equation + ";";
                } else {
                    equation = " ";
                }

                System.out.println("equation = " + equation);

                node.setEquation(equation);
            }
        }
    }
}
