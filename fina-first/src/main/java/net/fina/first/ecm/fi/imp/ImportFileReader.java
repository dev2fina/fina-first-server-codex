package net.fina.first.ecm.fi.imp;

import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.fi.imp.model.RegistryModel;
import net.fina.first.ecm.fi.imp.model.RegistryModelHelp;
import net.fina.first.ecm.fi.imp.model.RegistryValueModel;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ImportFileReader {

    public List<RegistryModel> readFileByMapping(InputStream fileInputStream) throws IOException, InvalidFormatException {

        List<RegistryModel> result = new ArrayList<>();

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream); Workbook workbook = WorkbookFactory.create(bufferedInputStream)) {

            RegistryModel mapping = readMapping(workbook.getSheet("mapping"));

            Sheet listSheet = workbook.getSheet("list");

            RegistryModel lastRegistry = null;

            for (Row row : listSheet) {

                if (row.getRowNum() > 2) {

                    String branchType = getCellStringValue(row.getCell(2));

                    RegistryModel branchMapping = mapping.getDetailsMapping().get("Branches");
                    RegistryModel branch = readData(row, branchMapping);

                    if (branchType == null || branchType.isEmpty() || branchType.equals("ფილიალი")) {

                        RegistryValueModel valueModel = new RegistryValueModel();
                        valueModel.setValue("SUBDIVISION");
                        branch.getProperties().put(EcmConstants.BRANCH_PROP_TYPE, valueModel);
                        branch.setRegistryType(getBranchNodeType(branch.getRegistryType(), "SUBDIVISION"));

                        lastRegistry.getDetailsValue().get("Branches").add(branch);
                    } else {
                        RegistryModel registryModel = readData(row, mapping);

                        for (Map.Entry<String, RegistryModel> detailsEntry : mapping.getDetailsMapping().entrySet()) {
                            if (!detailsEntry.getKey().equals("Branches")) {
                                registryModel.getDetailsValue().put(detailsEntry.getKey(), readDataList(row, detailsEntry.getValue()));
                            }
                        }

                        registryModel.getDetailsValue().put("Branches", new ArrayList<>());

                        RegistryValueModel valueModel = new RegistryValueModel();
                        valueModel.setValue("HEAD_OFFICE");
                        branch.getProperties().put(EcmConstants.BRANCH_PROP_TYPE, valueModel);
                        branch.setRegistryType(getBranchNodeType(branch.getRegistryType(), "HEAD_OFFICE"));

                        registryModel.getDetailsValue().get("Branches").add(branch);

                        // Set FI registry property values from head office properties according to FI status
                        registryModel.getProperties().put(EcmConstants.REGISTRY_PROP_REGISTRATION_DATE, branch.getProperties().get(EcmConstants.BRANCH_PROP_LEGAL_ACT_DATE));

                        RegistryValueModel lastActionDate;
                        if ("CANCELLATION".equals(registryModel.getProperties().get(EcmConstants.REGISTRY_PROP_ACTION_TYPE).getValue())
                                && "INACTIVE".equals(registryModel.getProperties().get(EcmConstants.REGISTRY_PROP_LICENSE_STATUS).getValue())
                                && branch.getProperties().get(EcmConstants.BRANCH_PROP_CANCELLATION_DATE) != null) { // If FI is cancelled, set legal act date and last action date to cancellation date of the head office
                            lastActionDate = branch.getProperties().get(EcmConstants.BRANCH_PROP_CANCELLATION_DATE);
                            registryModel.getProperties().put(EcmConstants.REGISTRY_PROP_ACT_DATE, lastActionDate);
                            RegistryValueModel cancellationLegalActNumber =  registryModel.getProperties().get(EcmConstants.REGISTRY_PROP_ACT_NUMBER);
                            cancellationLegalActNumber.setValue(((String)cancellationLegalActNumber.getValue()).split(",")[0]);
                        } else {
                            lastActionDate = branch.getProperties().get(EcmConstants.BRANCH_PROP_LEGAL_ACT_DATE);
                        }
                        registryModel.getProperties().put(EcmConstants.REGISTRY_PROP_LAST_ACTION_DATE, lastActionDate);

                        result.add(registryModel);

                        lastRegistry = registryModel;
                    }
                }
            }
        }

        return result;
    }

    List<RegistryModel> readDataList(Row row, RegistryModel mapping) {

        RegistryModel oneRowData = readData(row, mapping);

        Map<Integer, RegistryModel> lineModels = new HashMap<>();

        for (Map.Entry<String, RegistryValueModel> e : oneRowData.getProperties().entrySet()) {
            String valueType = e.getValue().getValueType();
            Object defaultValue = e.getValue().getDefaultValue();

            if (valueType != null && !(valueType.isEmpty()) && valueType.startsWith("list") && e.getValue().getValue() != null) {

                String value = e.getValue().getValue().toString();

                String[] lines = value.split("\\n");

                for (int i = 0, valuesLength = lines.length; i < valuesLength; i++) {
                    String line = lines[i];

                    String[] lineValues = line.split(",");

                    int valueIndex = Integer.parseInt(valueType.split(",")[1]);

                    String propertyValue = null;
                    if (valueIndex < lineValues.length) {
                        propertyValue = lineValues[valueIndex];
                    } else if (defaultValue != null && !defaultValue.toString().isEmpty()) {
                        propertyValue = defaultValue.toString();
                    }

                    if (propertyValue != null) {
                        RegistryModel temp = lineModels.get(i);
                        if (temp == null) {
                            temp = RegistryModelHelp.copy(oneRowData);
                            lineModels.put(i, temp);
                        }

                        RegistryValueModel valueModel = new RegistryValueModel();
                        valueModel.setValue(propertyValue);

                        temp.getProperties().put(e.getKey(), valueModel);
                    }
                }

            }
        }

        if (lineModels.isEmpty()) {
            ArrayList<RegistryModel> result = new ArrayList<>();
            result.add(oneRowData);
            return result;
        }

        return new ArrayList<>(lineModels.values());
    }

    private String getBranchNodeType(String branchTypePrefix, String branchType) {
        String result = branchTypePrefix;
        if ("HEAD_OFFICE".equals(branchType)) {
            result += "HeadOffice";
        } else {
            result += "Subdivision";
        }

        return result;
    }

    RegistryModel readData(Row row, RegistryModel mapping) {

        RegistryModel result = RegistryModelHelp.copy(mapping);

        for (Map.Entry<String, RegistryValueModel> entry : result.getProperties().entrySet()) {

            if (entry.getValue().getReference() != null && !(entry.getValue().getReference().isEmpty())) {

                int colIdx = CellReference.convertColStringToIndex(entry.getValue().getReference());

                Cell cell = row.getCell(colIdx);

                if (cell != null) {
                    if (entry.getValue().getValueType() != null) {
                        switch (entry.getValue().getValueType()) {
                            case "text":
                                entry.getValue().setValue(getCellStringValue(cell));
                                break;
                            case "list":
                                String listValueString = (String) getCellValue(cell);
                                if (listValueString == null || listValueString.isEmpty()) {
                                    String defaultValueStr = (String) entry.getValue().getDefaultValue();
                                    if (defaultValueStr != null) {
                                        listValueString = defaultValueStr;
                                    }
                                }

                                if (listValueString != null && !listValueString.isEmpty()) {
                                    List<String> res = Arrays.stream(listValueString.split(",")).map(String::trim).collect(Collectors.toList());
                                    entry.getValue().setValue(res);
                                }
                                break;

                            default:
                                entry.getValue().setValue(getCellValue(cell));
                                break;
                        }
                    } else {
                        entry.getValue().setValue(getCellValue(cell));
                    }

                    if (cell.getAddress() != null) {
                        entry.getValue().setReference(cell.getAddress().formatAsString());
                    }
                }

            } else {
                entry.getValue().setValue(entry.getValue().getDefaultValue());
            }
        }

        return result;
    }

    RegistryModel readMapping(Sheet sheet) {

        RegistryModel mapping = null;

        RegistryModel detailMapping = null;

        for (Row row : sheet) {

            if (row.getRowNum() > 0) {

                if (mapping == null) {
                    mapping = new RegistryModel();
                    mapping.setRegistryType(getCellStringValue(row.getCell(2)));
                } else {

                    String objectName = getCellStringValue(row.getCell(1));

                    if (objectName != null && !(objectName.isEmpty())) {
                        detailMapping = new RegistryModel();
                        detailMapping.setRegistryType(getCellStringValue(row.getCell(2)));

                        Map<String, RegistryValueModel> folderProperties = new HashMap<>();

                        RegistryValueModel childType = createValueModel(row.getCell(6));

                        folderProperties.put("fina:folderConfigChildType", childType);
                        folderProperties.put("fina:folderConfigVisible", createValueModel(row.getCell(7)));
                        folderProperties.put("fina:folderConfigTree", createValueModel(row.getCell(8)));
                        folderProperties.put("fina:folderConfigSequence", createValueModel(row.getCell(9)));
                        mapping.getFolderProps().put(objectName, folderProperties);

                        detailMapping.setRegistryType(childType.getValue().toString());

                        mapping.getDetailsMapping().put(objectName, detailMapping);
                    } else {
                        RegistryValueModel valueModel = new RegistryValueModel();
                        valueModel.setReference(getCellStringValue(row.getCell(3)));
                        valueModel.setDefaultValue(getCellValue(row.getCell(4)));
                        valueModel.setValueType(getCellStringValue(row.getCell(5)));

                        if (detailMapping == null) {
                            mapping.getProperties().put(getCellStringValue(row.getCell(2)), valueModel);
                        } else {
                            detailMapping.getProperties().put(getCellStringValue(row.getCell(2)), valueModel);
                        }
                    }
                }
            }
        }
        return mapping;
    }

    private RegistryValueModel createValueModel(Cell cell) {
        RegistryValueModel valueModel = new RegistryValueModel();
        valueModel.setDefaultValue(getCellStringValue(cell));
        valueModel.setValue(valueModel.getDefaultValue());
        return valueModel;
    }

    private String getCellStringValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    private Object getCellValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case FORMULA:
                    cellValue = cell.getCellFormula();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = cell.getDateCellValue();
                    } else {
                        cellValue = Double.toString(cell.getNumericCellValue());
                    }
                    break;

                case BLANK:
                    cellValue = "";
                    break;
                case BOOLEAN:
                    cellValue = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case ERROR:
                    cellValue = cell.getErrorCellValue();
                    break;
                default:
                    cellValue = cell.getStringCellValue();
                    break;
            }
        }
        return cellValue;
    }
}
