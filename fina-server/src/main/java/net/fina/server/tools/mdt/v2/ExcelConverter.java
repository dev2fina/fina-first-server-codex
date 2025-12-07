package net.fina.server.tools.mdt.v2;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.mdt.xml.v2.Description;
import net.fina.server.mdt.xml.v2.Node;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: nick Date: 1/28/13 Time: 12:54 AM To change
 * this template use File | Settings | File Templates.
 */
@SuppressWarnings("serial")
public class ExcelConverter implements Converter {

    private static final String DECRIPTION_CODE_SEPARATOR = ":";

    private Workbook workbook;

    private Map<String, Node> mdts;

    private String optionSheetName;

    private List<Option> optionList;

    private List<Node> allMdtNodes = new ArrayList<>();

    private Map<String, Map<String, Node>> sheetReferenceAndCode = new HashMap<>();
    private Map<String, String> nodeIdAndSheetName = new HashMap<>();

    private List<String> languageCodes;

    private int defaultOptionsCount = NodeOption.values().length + 1;

    public ExcelConverter(InputStream inputStream) throws InvalidFormatException, IOException {
        workbook = WorkbookFactory.create(inputStream);
    }

    public ExcelConverter(InputStream inputStream, String optionSheetName, List<String> languageCodes) throws Exception {
        this.optionSheetName = optionSheetName;
        this.languageCodes = languageCodes;

        this.mdts = new HashMap<>();

        init(inputStream);
    }

    public void init(InputStream inputStream) throws Exception {
        workbook = WorkbookFactory.create(inputStream);
        loadOptions();
    }

    private void loadOptions() {
        optionList = new ArrayList<>();
        Sheet optionsSheet;

        if (optionSheetName == null) {
            optionsSheet = workbook.getSheet(workbook.getSheetAt(0).getSheetName());
        } else {
            optionsSheet = workbook.getSheet(optionSheetName);
        }

        if (optionsSheet == null) {
            throw new NullPointerException("Options Sheet - " + optionSheetName + " not found in workbook");
        }

        for (int i = 1; i <= optionsSheet.getLastRowNum(); i++) {
            Row row = optionsSheet.getRow(i);
            if (row != null) {
                Cell nameCell = row.getCell(0);
                Cell codeCell = row.getCell(1);
                Cell descriptionCell = row.getCell(4);
                if (nameCell != null) {
                    String name = getCellStringValue(nameCell);
                    if (name != null && (!name.isEmpty())) {
                        String ref = row.getCell(2).getStringCellValue();

                        ReturnTableType returnTableType = ReturnTableType.valueOf(row.getCell(3).getStringCellValue());

                        String[] rowCol = ref.split(":");

                        String code = null;
                        if (codeCell != null) {
                            code = getCellStringValue(codeCell);
                        }

                        String description = code;
                        if (descriptionCell != null) {
                            description = getCellStringValue(descriptionCell);
                        }

                        Option option = new Option(returnTableType, rowCol[0], rowCol[1], name, code, description);
                        optionList.add(option);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }

    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public Map<String, Node> convert() throws Exception {
        for (Option option : optionList) {
            Sheet sheet = workbook.getSheet(option.getSheetName());

            if (sheet == null) {
                throw new NullPointerException("Sheet - " + option.getSheetName() + " not found in workbook. Options Sheet - " + optionSheetName);
            }

            String rootName = option.getCode() == null ? sheet.getSheetName() + "_" + option.getStartRow() : option.getCode();
            Node root = createRoot(rootName, option.getDescription());

            switch (option.getReturnTableType()) {
                case MCT: {
                    loadMctMdt(root, sheet, option);
                    break;
                }
                case VCT: {
                    loadVctMdt(root, sheet, option);
                    break;
                }
            }

            mdts.put(rootName, root);
        }

        // Run Parser
        FormulaParser formulaParser = new FormulaParser(allMdtNodes, sheetReferenceAndCode, nodeIdAndSheetName);
        formulaParser.execute();

        return this.mdts;
    }

    @SuppressWarnings("incomplete-switch")
    private void loadMctMdt(Node root, Sheet sheet, Option option) {

        Map<Integer, Node> parents = new HashMap<>();

        Row row = sheet.getRow(option.getStartRow());
        for (int i = option.getStartColumn() + 2; i <= option.getEndColumn(); i += defaultOptionsCount) {
            Cell c = row.getCell(i);
            Cell codeCell = row.getCell(i + NodeOption.CODE.ordinal());
            if (codeCell == null || getCellStringValue(codeCell).isEmpty()) {
                throw new RuntimeException("Sheet - " + option.getSheetName() + ", column - " + i + ", row - " + option.getStartRow() + " is NULL (numbering start from 0).");
            }
            Node node = createNode(getCellStringValue(codeCell), getCellStringValue(c), MDTNodeTypes.NODE, c);
            node.setSequence((long) i);
            parents.put(i, node);

            root.getChildren().add(node);
        }

        for (int i = option.getStartRow() + 1; i <= option.getEndRow(); i++) {

            row = sheet.getRow(i);

            Cell descriptionCodeCell = row.getCell(option.getStartColumn() - 1);
            Cell descriptionCell = row.getCell(option.getStartColumn());

            for (int j = option.getStartColumn() + 2; j <= option.getEndColumn(); j += defaultOptionsCount) {

                Cell cell = row.getCell(j);

                Cell codeCell = row.getCell(j + NodeOption.CODE.ordinal());
                Cell typeCell = row.getCell(j + NodeOption.TYPE.ordinal());

                Node parentNode = parents.get(j);

                MDTNodeTypes type = null;
                try {
                    type = MDTNodeTypes.values()[getCellIntValue(typeCell)];
                } catch (ArrayIndexOutOfBoundsException ex) {
                    ArrayIndexOutOfBoundsException e = new ArrayIndexOutOfBoundsException("Sheet - " + typeCell.getSheet().getSheetName() + " Column - " + typeCell.getColumnIndex() + ", row -  " + typeCell.getRowIndex() + ", Invalid Node Type: " + ex.getMessage());
                    e.setStackTrace(ex.getStackTrace());
                    throw e;
                }

                String description = getCellStringValue(descriptionCell);
                if (descriptionCodeCell != null) {
                    String tmp = getCellStringValue(descriptionCodeCell);
                    if (tmp != null && (!tmp.isEmpty())) {
                        description = tmp + DECRIPTION_CODE_SEPARATOR + description;
                    }
                }

                Node node = createNode(getCellStringValue(codeCell), description, type, cell);
                parentNode.getChildren().add(node);

                switch (type) {
                    case INPUT: {
                        Cell dataTypeCell = row.getCell(j + NodeOption.DATA_TYPE.ordinal());
                        MDTNodeDataTypes dataType = MDTNodeDataTypes.values()[getCellIntValue(dataTypeCell)];
                        node.setDataType(dataType);
                        break;
                    }
                    case VARIABLE: {
                        Cell customEquationCell = row.getCell(j + NodeOption.CUSTOM_EQUATION.ordinal());
                        node.setEquation(getCellEquation(cell, customEquationCell));
                        break;
                    }
                }

                Cell requiredCell = row.getCell(j + NodeOption.REQUIRED.ordinal());
                node.setRequired(getCellIntValue(requiredCell) > 0);

                node.setSequence((long) i);

            }

        }
    }

    @SuppressWarnings("incomplete-switch")
    private void loadVctMdt(Node root, Sheet sheet, Option option) {

        int index = option.getStartRow();

        Row descriptionRow = sheet.getRow(index);
        Row codeRow = sheet.getRow(index + NodeOption.CODE.ordinal() + 1);
        Row typeRow = sheet.getRow(index + NodeOption.TYPE.ordinal() + 1);
        Row dataTypeRow = sheet.getRow(index + NodeOption.DATA_TYPE.ordinal() + 1);
        Row requiredRow = sheet.getRow(index + NodeOption.REQUIRED.ordinal() + 1);
        Row customEquationRow = sheet.getRow(index + NodeOption.CUSTOM_EQUATION.ordinal() + 1);
        Row formulaRow = sheet.getRow(index + NodeOption.CUSTOM_EQUATION.ordinal() + 2);

        for (int i = option.getStartColumn(); i <= option.getEndColumn(); i++) {

            Cell descriptionCell = descriptionRow.getCell(i);
            Cell codeCell = codeRow.getCell(i);
            Cell typeCell = typeRow.getCell(i);

            Cell formualCell = formulaRow.getCell(i);

            MDTNodeTypes type = MDTNodeTypes.values()[getCellIntValue(typeCell)];

            String description = getCellStringValue(descriptionCell);

            Node node = createNode(getCellStringValue(codeCell), description, type, formualCell);

            switch (type) {
                case INPUT: {
                    Cell dataTypeCell = dataTypeRow.getCell(i);
                    MDTNodeDataTypes dataType = MDTNodeDataTypes.values()[getCellIntValue(dataTypeCell)];
                    node.setDataType(dataType);
                    break;
                }
                case VARIABLE: {
                    Cell customEquationCell = customEquationRow.getCell(i);
                    node.setEquation(getCellEquation(formualCell, customEquationCell));
                    break;
                }
            }

            Cell requiredCell = requiredRow.getCell(i);
            node.setRequired(getCellIntValue(requiredCell) > 0);

            node.setSequence((long) i);

            root.getChildren().add(node);

        }

    }

    private Node createRoot(String code, String description) {
        return createNode(code, description, MDTNodeTypes.NODE, null);
    }

    private Node createNode(String code, String description, MDTNodeTypes mdtNodeType, Cell cell) {
        Node node = new Node();
        node.setRequired(false);
        node.setDataType(MDTNodeDataTypes.NUMERIC);

        // Replace &quot;
        code = code.replace("\"", "").replace("'", "");

        node.setCode(code);

        List<Description> descriptions = new ArrayList<>();
        for (String languageCode : languageCodes) {
            Description d = new Description();
            d.setLangCode(languageCode.trim());
            d.setValue((description == null || description.isEmpty()) ? "" : description);
            descriptions.add(d);
        }
        node.setDescriptions(descriptions.toArray(new Description[0]));

        node.setDisabled(false);
        node.setType(mdtNodeType);
        node.setSequence(0L);
        node.setEquation(" ");
        node.setEvalMethod(MDTNodeEvalMethods.UNKNOWN);
        node.setChildren(new ArrayList<Node>());


        allMdtNodes.add(node);

        if (cell != null) {
            CellReference cellReference = new CellReference(cell);

            System.out.println(cellReference.formatAsString());

            String sheetName = cell.getSheet().getSheetName();

            Map<String, Node> referenceAndCode = sheetReferenceAndCode.get(sheetName);
            if (referenceAndCode == null) {
                referenceAndCode = new HashMap<>();
                sheetReferenceAndCode.put(sheetName, referenceAndCode);
            }

            referenceAndCode.put(cellReference.formatAsString(false), node);

            nodeIdAndSheetName.put(node.getCode(), sheetName);
        }

        return node;
    }

    private String getCellEquation(Cell formualCell, Cell customEquationCell) {
        String equation = " ";
        if ((customEquationCell != null) && (getCellStringValue(customEquationCell) != null) && (!getCellStringValue(customEquationCell).trim().isEmpty())) {
            equation = getCellStringValue(customEquationCell);
        } else if (formualCell != null) {
            equation = getCellFormula(formualCell);
        }

        System.err.println(formualCell != null ? formualCell.getRowIndex() : "NULL");
        System.err.println("Native Equation = " + equation);

        return equation;
    }

    private String getCellFormula(Cell cell) {
        String formula = null;

        switch (cell.getCellType()) {
            case FORMULA: {
                formula = cell.getCellFormula();
                break;
            }
        }

        return formula;

    }

    private String getCellStringValue(Cell cell) {
        String value = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case BLANK: {
                    value = "";
                    break;
                }
                case BOOLEAN: {
                    value = Boolean.toString(cell.getBooleanCellValue());
                    break;
                }
                case ERROR: {
                    throw new RuntimeException(cell + " has Error: " + cell.getErrorCellValue());
                }
                case FORMULA: {
                    value = getFormulaEvaluator().evaluate(cell).formatAsString();
                    break;
                }
                case NUMERIC: {
                    double numericValue = cell.getNumericCellValue();
                    value = Long.toString((long) numericValue);
                    break;
                }
                case STRING: {
                    value = cell.getStringCellValue();
                    break;
                }

            }
        }
        return value;
    }

    private int getCellIntValue(Cell cell) {
        int value = -1;

        if (cell != null) {
            switch (cell.getCellType()) {
                case BLANK: {
                    value = 0;
                    break;
                }
                case BOOLEAN: {
                    value = cell.getBooleanCellValue() ? 1 : 0;
                    break;
                }
                case ERROR: {
                    throw new RuntimeException(cell + " has Error: " + cell.getErrorCellValue());
                }
                case FORMULA: {
                    String result = getFormulaEvaluator().evaluate(cell).formatAsString();
                    value = parseStringIntValue(result, cell);
                    break;
                }
                case NUMERIC: {
                    value = (int) cell.getNumericCellValue();
                    break;
                }
                case STRING: {
                    value = parseCellIntValue(cell);
                    break;
                }

            }
        }

        return value;
    }

    private FormulaEvaluator getFormulaEvaluator() {
        return workbook.getCreationHelper().createFormulaEvaluator();
    }

    private int parseStringIntValue(String value, Cell cell) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            NumberFormatException e = new NumberFormatException("Sheet - " + cell.getSheet().getSheetName() + ", column - " + cell.getColumnIndex() + ", row - " + cell.getRowIndex() + "(numbering start from 0). " + ex.getMessage());
            e.setStackTrace(ex.getStackTrace());
            throw e;
        }
    }

    private int parseCellIntValue(Cell cell) {
        return parseStringIntValue(cell.getStringCellValue(), cell);
    }

    @Override
    public List<String> getSheetNames() {
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheetNames.add(workbook.getSheetName(i));
        }
        return sheetNames;
    }
}
