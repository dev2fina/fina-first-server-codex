package net.fina.first.ecm.node.impl;

import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PathElementRepresentation;
import net.fina.ecm.alfresco.api.dictionary.model.ClassPropertyRepresentation;
import net.fina.ecm.alfresco.api.dictionary.model.ConstraintRepresentation;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.node.api.NodeExporter;
import net.fina.first.ecm.node.model.ExportTemplate;
import net.fina.messages.MessagesUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelNodeExporter implements NodeExporter {

    private final AlfrescoClient client;
    private final SimpleDateFormat dateFormat;
    private CellStyle headerRowStyle;
    private CellStyle dateCellStyle;
    private Workbook workbook;

    public ExcelNodeExporter(AlfrescoClient client) {
        this.client = client;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    @Override
    public byte[] getExportNodeHierarchyContent(ExportTemplate exportTemplate, String rootFolderId, String childNodeType, QueryBody filterQueryBody) throws Throwable {

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(exportTemplate.getFullTemplateFilePath()); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook = WorkbookFactory.create(in);
            headerRowStyle = getHeaderStyle();
            dateCellStyle = getDateStyle();

            String generalSheetName = "General";
            Sheet generalSheet = workbook.getSheet(generalSheetName);
            if (generalSheet == null) {
                generalSheet = workbook.createSheet(generalSheetName);
            }

            int startRowNum = generalSheet.getLastRowNum();
            startRowNum = startRowNum != 0 ? startRowNum + 2 : startRowNum;

            exportNodeChildren(null, true, generalSheet, startRowNum, rootFolderId, childNodeType, filterQueryBody);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private int exportNodeChildren(String title, boolean isRoot, Sheet sheet, int startRow, String rootFolderId, String childNodeType, QueryBody filterQueryBody) {
        if (childNodeType == null || childNodeType.isEmpty()) {
            childNodeType = getChildNodeType(rootFolderId);
        }

        if (title != null) {
            sheet.createRow(startRow).createCell(0).setCellValue(title);
            startRow++;
        }
        Row headerRow = sheet.createRow(startRow);
        headerRow.setRowStyle(headerRowStyle);

        if (childNodeType != null && !childNodeType.isEmpty()) {
            childNodeType = childNodeType.replace(':', '_');

            List<ClassPropertyRepresentation> classPropertiesResult = client.getDictionaryAPI().getClassPropertiesCall(childNodeType);
            if (classPropertiesResult != null && !classPropertiesResult.isEmpty()) {

                int startColumn = 0;

                List<String> sequenceFieldNames = getSequenceFieldNames(classPropertiesResult, childNodeType);
                if (!sequenceFieldNames.isEmpty()) {
                    classPropertiesResult.sort(Comparator.comparingInt(o -> sequenceFieldNames.indexOf(o.getName())));
                }

                Map<String, ClassPropertyRepresentation> schema = new LinkedHashMap<>();
                List<String> hiddenFieldNames = getHiddenFieldNames(classPropertiesResult, childNodeType);

                // header
                for (ClassPropertyRepresentation cpr : classPropertiesResult) {
                    if (!hiddenFieldNames.contains(cpr.getName())) {
                        Cell cell = headerRow.createCell(startColumn);
                        cell.setCellValue(cpr.getTitle());
                        cell.setCellStyle(headerRowStyle);
                        sheet.autoSizeColumn(startColumn);
                        startColumn++;

                        schema.put(cpr.getName(), cpr);
                    }
                }

                startColumn = 0;

                List<NodeRepresentation> nodeRepresentations;
                if (isRoot && filterQueryBody != null) {
                    nodeRepresentations = getFilteredRegistryNodes(filterQueryBody);
                } else {
                    String where = "(nodeType='" + (isRoot ? (EcmConstants.REGISTRY_TYPE_REGISTRY + " INCLUDESUBTYPES") : childNodeType.replace("_", ":")) + "')";
                    nodeRepresentations = client.getNodesAPI().listNodeChildrenCall(rootFolderId, null, Integer.MAX_VALUE, null, where, new IncludeParam(Collections.singletonList("properties")), null, null, null).getObjects();
                }

                if (nodeRepresentations != null && !nodeRepresentations.isEmpty()) {

                    for (NodeRepresentation nr : nodeRepresentations) {

                        if (!isRoot && nr.isFolder() && nr.getNodeType().equalsIgnoreCase("cm:folder")) {
                            startRow = exportNodeChildren(nr.getName(), false, sheet, startRow, nr.getId(), null, filterQueryBody);
                        } else {
                            startRow++;

                            Row row = sheet.createRow(startRow);

                            Map<String, Object> properties = nr.getProperties();

                            if (childNodeType.equalsIgnoreCase("cm_folder")) {
                                Cell cell = row.createCell(startColumn);
                                cell.setCellValue(nr.getName());
                                sheet.autoSizeColumn(startColumn);
                            } else {
                                for (Map.Entry<String, ClassPropertyRepresentation> schemaEntry : schema.entrySet()) {
                                    initCellWithValue(row, startColumn, schemaEntry.getValue(), properties.get(schemaEntry.getKey()));
                                    startColumn++;
                                }
                            }

                            startColumn = 0;

                            if (isRoot) {
                                exportNodeChildren(null, false, workbook.createSheet(nr.getName()), 0, nr.getId(), null, filterQueryBody);
                            } else if (nr.isFolder()) {
                                startRow = exportNodeChildren(nr.getName(), false, sheet, sheet.getLastRowNum() + 2, nr.getId(), null, filterQueryBody);
                            }

                        }
                    }
                }
            }
            startRow += 2;
        }

        sheet.createRow(startRow).createCell(0); // empty row

        return startRow;
    }

    private List<NodeRepresentation> getFilteredRegistryNodes(QueryBody queryBody) {
        List<NodeRepresentation> result = new ArrayList<>();
        ResultSetRepresentation<ResultNodeRepresentation> filteredResultNodeRepresentations = client.getSearchAPI().search(queryBody);
        String fiRegistryRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ROOT_FOLDER_PATH_KEY);

        for (ResultNodeRepresentation node : filteredResultNodeRepresentations.getObjects()) {
            if (node.getNodeType() != null && node.getPath().getName() != null && node.getPath().getName().contains(fiRegistryRootFolderPath)) {
                //get registry fi node id from path element
                int pathSplitSize = fiRegistryRootFolderPath.split("/").length + 2;
                List<PathElementRepresentation> refPathList = node.getPath().getElement().size() > pathSplitSize ? node.getPath().getElement().subList(pathSplitSize - 1, pathSplitSize) : new ArrayList<>();
                if (!refPathList.isEmpty() && !result.contains(node)) {
                    result.add(client.getNodesAPI().getNodeCall(refPathList.get(0).getId()));
                } else {
                    result.add(node);
                }
            }
        }

        return result;
    }

    private void initCellWithValue(Row row, int column, ClassPropertyRepresentation classProperty, Object value) throws NullPointerException {

        Cell cell = row.createCell(column);

        if (classProperty != null && value != null) {
            switch (classProperty.getDataType()) {
                case "d:text":
                case "d:mltext":
                    if (classProperty.isMultiValued() && (value instanceof List)) {
                        List<String> valueArray = (List<String>) value;
                        StringBuilder exportValue = new StringBuilder();
                        for (String s : valueArray) {
                            if (exportValue.length() > 0) {
                                exportValue.append(", ");
                            }
                            exportValue.append(MessagesUtil.getString(FirstUtil.getValue(s, String.class)));
                        }
                        cell.setCellValue(exportValue.toString());
                    } else {
                        cell.setCellValue(MessagesUtil.getString(FirstUtil.getValue(value, String.class)));
                    }
                    break;
                case "d:boolean":
                    Boolean v = FirstUtil.getValue(value, Boolean.class);
                    cell.setCellValue(v != null && v ? MessagesUtil.getString("OK") : MessagesUtil.getString("NO"));
                    break;
                case "d:int":
                    cell.setCellValue(FirstUtil.getValue(value, Integer.class));
                    break;
                case "d:long":
                    cell.setCellValue(FirstUtil.getValue(value, Long.class));
                    break;
                case "d:float":
                    cell.setCellValue(FirstUtil.getValue(value, Float.class));
                    break;
                case "d:double":
                    cell.setCellValue(FirstUtil.getValue(value, Double.class));
                    break;
                case "d:date":
                case "d:datetime":
                    String stringValue = FirstUtil.getValue(value, String.class);
                    try {
                        cell.setCellValue(dateFormat.parse(stringValue));
                        cell.setCellStyle(dateCellStyle);
                    } catch (Throwable t) {
                        cell.setCellValue(stringValue);
                    }

                    break;
                default:
                    cell.setCellValue(FirstUtil.getValue(value, String.class));
                    break;
            }

            row.getSheet().autoSizeColumn(column);

            return;
        }

        cell.setCellValue(" - ");
    }

    private CellStyle getDateStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));
        return style;
    }

    private CellStyle getHeaderStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        return style;
    }

    private List<String> getSequenceFieldNames(List<ClassPropertyRepresentation> classProperties, String baseTypeName) {
        String sequenceFieldPropertyName = baseTypeName.replace('_', ':') + "Sequence";
        return getConstraintListValues(classProperties, sequenceFieldPropertyName);
    }

    private List<String> getHiddenFieldNames(List<ClassPropertyRepresentation> classProperties, String baseTypeName) {
        String hiddenFieldPropertyName = baseTypeName.replace('_', ':') + "HiddenFieldNames";
        return getConstraintListValues(classProperties, hiddenFieldPropertyName);
    }

    private List<String> getConstraintListValues(List<ClassPropertyRepresentation> classProperties, String searchPropertyName) {
        searchPropertyName = searchPropertyName.replace('_', ':');
        for (ClassPropertyRepresentation cpr : classProperties) {
            if (cpr.getName().equalsIgnoreCase(searchPropertyName)) {
                List<ConstraintRepresentation> constraints = cpr.getConstraints();
                if (constraints != null && !constraints.isEmpty()) {
                    for (ConstraintRepresentation constraint : constraints) {
                        if (constraint.getType().equalsIgnoreCase("LIST")) {
                            List<Map<String, Object>> parameters = constraint.getParameters();
                            for (Map<String, Object> parameter : parameters) {
                                Object allowedValues = parameter.get("allowedValues");
                                if (allowedValues != null) {
                                    return (List<String>) allowedValues;
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private String getChildNodeType(String rootFolderId) {
        String result = null;

        NodeRepresentation nr = client.getNodesAPI().getNodeCall(rootFolderId);
        if (nr.getAspects() != null && nr.getAspects().contains("fina:folderConfig")) {
            result = FirstUtil.getValue(nr.getProperties().get("fina:folderConfigChildType"), String.class);
        }

        // get type from first node
        if (result == null || result.trim().isEmpty()) {
            List<NodeRepresentation> nodeRepresentations = client.getNodesAPI().listNodeChildrenCall(rootFolderId, null, 1, null, null, null, null, null, null).getObjects();
            if (nodeRepresentations != null && !nodeRepresentations.isEmpty()) {
                result = nodeRepresentations.get(0).getNodeType();
            }
        }

        return result;
    }
}
