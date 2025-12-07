package net.fina.server.dcs.uploadfile.impl.reader.excel;

import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelMatrixReader extends ExcelBaseReader {
    private final List<MatrixOptionBase> options = new ArrayList<MatrixOptionBase>();

    public ExcelMatrixReader(String excelPath, Map<String, Object> properties) throws ConverterDcsTypeException {
        try {
            initWorkBook(excelPath, properties);
            checkOptionsIndex();
        } catch (InvalidFormatException | IOException exception) {
            DcsTypeException.Type type = DcsTypeException.Type.MAIN_MATRIX_NOT_SET;

            log.error(type.getCode());
            log.error(exception.getMessage(), exception);
            log.error("excelFilePath = " + excelPath);

            throw new ConverterDcsTypeException(exception, type, type.getReplaceableCode());
        }
    }

    private void initOptions() {
        StringBuilder exceptionString = new StringBuilder();
        Sheet sheet = workbook.getSheetAt(super.optionsSheetindex);
        int lastRowNum = lastRowNumb(sheet);

        for (int currentRow = 1; currentRow <= lastRowNum; currentRow++) {// zero are names;
            Row row = sheet.getRow(currentRow);
            int LastCellNum = row.getLastCellNum();
            Cell[] cells = new Cell[LastCellNum];
            for (int currentColumn = 0; currentColumn < LastCellNum; currentColumn++) {
                cells[currentColumn] = row.getCell(currentColumn);
            }
            try {
                this.options.add(new ExcelMatrixOption(cells));
            } catch (DcsTypeException dcsTypeException) {
                exceptionString.append(dcsTypeException.getMessage());
                log.error(exceptionString.toString());
            }
        }
    }

    public List<MatrixOptionBase> getOptions() {
        initOptions();
        return options;
    }
/*
    public class Option {

        private String fiType;
        private String matrixForEachType;
        private String pattern;
        private String period;
        private String periodTypeLabel;
        private String version;
        private String workBookPassword;
        private boolean digitalSignatureCheckEnabled;
        private ProcessEngine processEngine;
        private String filePermissionGroup;


        private RegAdvancedFileType regAdvancedFileType;

        public Option(Cell[] cells) throws DcsTypeException {
            try {
                this.setFIType(cells[0]);
                this.setMatrixForEachType(cells[1]);
                this.setPattern(cells[2]);
                this.setPeriod(cells[3]);
                this.setPeriodTypeLabel(cells[4]);
                this.setVersion(cells[5]);
                if (cells.length > 6) {
                    this.setWorkBookPassword(cells[6]);
                }
                if (cells.length > 7) {
                    this.setDigitalSignatureCheckEnabled(cells[7]);
                }
                this.setProcessFileType(getProcessFileTypeSafe(cells));
                this.setRegAdvancedFileType(getRegAdvancedFileTypeSafe(cells));
                if (cells.length > 10 && cells[10] != null && cells[10].getStringCellValue() != null && !cells[10].getStringCellValue().isEmpty()) {
                    this.filePermissionGroup = cells[10].getStringCellValue();
                }

            } catch (ConverterDcsTypeException converterDecsTypeException) {
                throw new DcsTypeException(converterDecsTypeException.getMessage());
            }
        }

        public String getFIType() {
            return fiType;
        }

        private void setFIType(Cell fileType) throws ConverterDcsTypeException {
            // XXX: setFIType
            try {
                this.fiType = fileType.getStringCellValue();
            } catch (IllegalStateException illegalStateException) {
                log.error(illegalStateException.getMessage(), illegalStateException);
                throw generateOptionsSheetIllegalCellTypeCase(fileType, ExcelCellType.TEXT);
            }
        }

        public String getMatrixForEachType() {
            return matrixForEachType;
        }

        private void setMatrixForEachType(Cell matrixForEachType) throws ConverterDcsTypeException {
            // XXX setMatrixForEachType
            try {
                this.matrixForEachType = matrixForEachType.getStringCellValue();
            } catch (IllegalStateException illegalStateException) {
                log.error(illegalStateException, illegalStateException);
                throw generateOptionsSheetIllegalCellTypeCase(matrixForEachType, ExcelCellType.TEXT);
            }
        }

        public String getPattern() {
            return pattern;
        }

        private void setPattern(Cell pattern) throws ConverterDcsTypeException {
            // XXX setPattern
            try {
                this.pattern = pattern.getStringCellValue();
            } catch (IllegalStateException illegalStateException) {
                log.error(illegalStateException, illegalStateException);
                throw generateOptionsSheetIllegalCellTypeCase(pattern, ExcelCellType.TEXT);
            }
        }

        public String getPeriod() {
            return period;
        }

        private void setPeriod(Cell period) throws ConverterDcsTypeException {
            // XXX setPeriod
            try {
                this.period = period.getStringCellValue();
            } catch (IllegalStateException illegalStateException) {
                log.error(illegalStateException, illegalStateException);
                throw generateOptionsSheetIllegalCellTypeCase(period, ExcelCellType.TEXT);
            }
        }

        public String getPeriodTypeLabel() {
            return periodTypeLabel;
        }

        private void setPeriodTypeLabel(Cell periodTypeLabel) throws ConverterDcsTypeException {
            // XXX setPeriodTypeLabel
            try {
                this.periodTypeLabel = periodTypeLabel.getStringCellValue();
            } catch (IllegalStateException illegalStateException) {
                log.error(illegalStateException, illegalStateException);
                throw generateOptionsSheetIllegalCellTypeCase(periodTypeLabel, ExcelCellType.TEXT);
            }
        }

        public String getVersion() {
            return version;
        }

        private void setVersion(Cell version) throws ConverterDcsTypeException {
            // XXX setVersion
            try {
                this.version = version.getStringCellValue();
            } catch (IllegalStateException illegalStateException) {
                log.error(illegalStateException, illegalStateException);
                throw generateOptionsSheetIllegalCellTypeCase(version, ExcelCellType.TEXT);
            }
        }

        public String getWorkBookPassword() {
            return workBookPassword;
        }

        public void setWorkBookPassword(Cell workBookPassword) {
            try {
                this.workBookPassword = workBookPassword.getStringCellValue();
            } catch (IllegalStateException illegalStateException) {
                log.error(illegalStateException, illegalStateException);
                throw generateOptionsSheetIllegalCellTypeCase(workBookPassword, ExcelCellType.TEXT);
            }
        }

        public boolean isDigitalSignatureCheckEnabled() {
            return digitalSignatureCheckEnabled;
        }

        public void setDigitalSignatureCheckEnabled(Cell enabled) {
            try {
                this.digitalSignatureCheckEnabled = enabled.getBooleanCellValue();
            } catch (IllegalStateException | NumberFormatException ex) {
                log.error(ex, ex);
                throw generateOptionsSheetIllegalCellTypeCase(enabled, ExcelCellType.TEXT);
            }
        }

        public ProcessEngine getProcessFileType() {
            return processEngine;
        }

        public void setProcessFileType(ProcessEngine processEngine) {
            this.processEngine = processEngine;
        }

        public RegAdvancedFileType getRegAdvancedFileType() {
            return regAdvancedFileType;
        }

        public void setRegAdvancedFileType(RegAdvancedFileType regAdvancedFileType) {
            this.regAdvancedFileType = regAdvancedFileType;
        }

        private ProcessEngine getProcessFileTypeSafe(Cell[] cells) {
            try {
                if (cells.length > 8 && cells[8] != null && cells[8].getStringCellValue() != null && !cells[8].getStringCellValue().trim().isEmpty()) {
                    return ProcessEngine.valueOf(cells[8].getStringCellValue().toUpperCase().trim());
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }

            return ProcessEngine.FINA;
        }

        private RegAdvancedFileType getRegAdvancedFileTypeSafe(Cell[] cells) {
            try {
                if (cells.length > 9 && cells[9] != null && cells[9].getStringCellValue() != null && !cells[9].getStringCellValue().trim().isEmpty()) {
                    return RegAdvancedFileType.valueOf(cells[9].getStringCellValue().toUpperCase().trim());
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }

            return null;
        }

        public String getFilePermissionGroup() {
            return filePermissionGroup;
        }

        @Override
        public String toString() {
            return "Option [fiType=" + fiType + ", matrixForEachType=" + matrixForEachType + ", pattern=" + pattern + ", period=" + period + ", periodTypeLabel=" + periodTypeLabel + ", version=" + version + "]";
        }
    }*/
}