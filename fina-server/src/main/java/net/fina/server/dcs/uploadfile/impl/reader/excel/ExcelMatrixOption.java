package net.fina.server.dcs.uploadfile.impl.reader.excel;

import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.dcs.uploadfile.model.RegAdvancedFileType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelMatrixOption extends MatrixOptionBase {
    public ExcelMatrixOption(Cell[] cells) throws DcsTypeException {
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
            if (cells.length > 10 && cells[10] != null && cells[10].getCellType() == CellType.BOOLEAN) {
                this.encryptEnabled = cells[10].getBooleanCellValue();
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
            throw generateOptionsSheetIllegalCellTypeCase(fileType, ConverterDcsTypeException.ExcelCellType.TEXT);
        }
    }


    private void setMatrixForEachType(Cell matrixForEachType) throws ConverterDcsTypeException {
        // XXX setMatrixForEachType
        try {
            this.matrixForEachType = matrixForEachType.getStringCellValue();
        } catch (IllegalStateException illegalStateException) {
            log.error(illegalStateException, illegalStateException);
            throw generateOptionsSheetIllegalCellTypeCase(matrixForEachType, ConverterDcsTypeException.ExcelCellType.TEXT);
        }
    }


    private void setPattern(Cell pattern) throws ConverterDcsTypeException {
        // XXX setPattern
        try {
            this.pattern = pattern.getStringCellValue();
        } catch (IllegalStateException illegalStateException) {
            log.error(illegalStateException, illegalStateException);
            throw generateOptionsSheetIllegalCellTypeCase(pattern, ConverterDcsTypeException.ExcelCellType.TEXT);
        }
    }


    private void setPeriod(Cell period) throws ConverterDcsTypeException {
        // XXX setPeriod
        try {
            this.period = period.getStringCellValue();
        } catch (IllegalStateException illegalStateException) {
            log.error(illegalStateException, illegalStateException);
            throw generateOptionsSheetIllegalCellTypeCase(period, ConverterDcsTypeException.ExcelCellType.TEXT);
        }
    }


    private void setPeriodTypeLabel(Cell periodTypeLabel) throws ConverterDcsTypeException {
        // XXX setPeriodTypeLabel
        try {
            this.periodTypeLabel = periodTypeLabel.getStringCellValue();
        } catch (IllegalStateException illegalStateException) {
            log.error(illegalStateException, illegalStateException);
            throw generateOptionsSheetIllegalCellTypeCase(periodTypeLabel, ConverterDcsTypeException.ExcelCellType.TEXT);
        }
    }


    private void setVersion(Cell version) throws ConverterDcsTypeException {
        // XXX setVersion
        try {
            this.version = version.getStringCellValue();
        } catch (IllegalStateException illegalStateException) {
            log.error(illegalStateException, illegalStateException);
            throw generateOptionsSheetIllegalCellTypeCase(version, ConverterDcsTypeException.ExcelCellType.TEXT);
        }
    }


    public void setWorkBookPassword(Cell workBookPassword) {
        try {
            this.workBookPassword = workBookPassword.getStringCellValue();
        } catch (IllegalStateException illegalStateException) {
            log.error(illegalStateException, illegalStateException);
            throw generateOptionsSheetIllegalCellTypeCase(workBookPassword, ConverterDcsTypeException.ExcelCellType.TEXT);
        }
    }

    public void setDigitalSignatureCheckEnabled(Cell enabled) {
        try {
            this.digitalSignatureCheckEnabled = enabled.getBooleanCellValue();
        } catch (IllegalStateException | NumberFormatException ex) {
            log.error(ex, ex);
            throw generateOptionsSheetIllegalCellTypeCase(enabled, ConverterDcsTypeException.ExcelCellType.TEXT);
        }
    }

    public void setProcessFileType(ProcessEngine processEngine) {
        this.processEngine = processEngine;
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

    protected ConverterDcsTypeException generateOptionsSheetIllegalCellTypeCase(Cell cell, ConverterDcsTypeException.ExcelCellType type) {
        ConverterDcsTypeException converterDcsTypeException;
        converterDcsTypeException = new ConverterDcsTypeException("options", cell.getRowIndex(), cell.getColumnIndex(), type);
        converterDcsTypeException.setType(DcsTypeException.Type.ILLEGAL_CELL_TYPE);
        return converterDcsTypeException;
    }
}
