package net.fina.server.reports.impl;

import net.fina.auditlog.api.AuditLogLocal;
import net.fina.auditlog.entity.AuditLog;
import net.fina.auditlog.impl.AuditLogJsonSerializer;
import net.fina.common.client.constants.OperationType;
import net.fina.common.server.util.CommonUtil;
import net.fina.messages.MessagesUtil;
import net.fina.odstoolkit.reader.AOOReader;
import net.fina.odstoolkit.reader.ReportCell;
import net.fina.odstoolkit.reader.jOpenDocumentAooReader;
import net.fina.server.reports.entity.ReportTemplate;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by vamekh on 01-Dec-2015.
 */
public class ReportModificationAuditLogger {

    private Logger log = Logger.getLogger(getClass());

    private AuditLogLocal auditLogLocal;

    private ReportTemplate oldTemplate;
    private ReportTemplate newTemplate;
    private AOOReader oldTemplateReader;
    private AOOReader newTemplateReader;

    @Inject
    public ReportModificationAuditLogger(AuditLogLocal auditLogLocal) {
        this.auditLogLocal = auditLogLocal;
    }

    public void compare(ReportTemplate oldTemplate, ReportTemplate newTemplate) throws Exception {
        this.oldTemplate = oldTemplate;
        this.newTemplate = newTemplate;
        oldTemplateReader = new jOpenDocumentAooReader(oldTemplate.getTemplate());
        newTemplateReader = new jOpenDocumentAooReader(newTemplate.getTemplate());


        int oldTemplRowN = oldTemplateReader.getLastRow();
        int oldTemplColN = oldTemplateReader.getLastCol();
        int newTemplRowN = newTemplateReader.getLastRow();
        int newTemplColN = newTemplateReader.getLastCol();
        String oldVal;
        String newVal;

        for (int i = 0; i <= Math.min(oldTemplRowN, newTemplRowN); i++) {
            for (int j = 0; j <= Math.min(oldTemplColN, newTemplColN); j++) {
                oldVal = getStringData(oldTemplateReader, i, j);
                newVal = getStringData(newTemplateReader, i, j);

                if (!oldVal.equals(newVal)) {
                    createAuditLogEntry(i, j, oldVal, newVal);
                }
            }
        }

        if (oldTemplRowN > newTemplRowN) {
            addMissingRows(oldTemplateReader, newTemplRowN + 1, false);
        } else if (oldTemplRowN < newTemplRowN) {
            addMissingRows(newTemplateReader, oldTemplRowN + 1, true);
        }

        if (oldTemplColN > newTemplColN) {
            addMissingColumns(oldTemplateReader, newTemplColN + 1, newTemplRowN, false);
        } else if (oldTemplColN < newTemplColN) {
            addMissingColumns(newTemplateReader, oldTemplColN + 1, oldTemplRowN, true);
        }
    }

    /**
     * @param isInsert true if new Rows were added, false - otherwise
     */
    private void addMissingRows(AOOReader aooReader, int startRow, boolean isInsert) {
        String tmp;
        for (int i = startRow; i <= aooReader.getLastRow(); i++) {
            for (int j = 0; j <= aooReader.getLastCol(); j++) {
                tmp = getStringData(aooReader, i, j);
                if (!tmp.trim().isEmpty()) {
                    if (isInsert) {
                        createAuditLogEntry(i, j, "", tmp);
                    } else {
                        createAuditLogEntry(i, j, tmp, "");
                    }
                }
            }
        }
    }

    /**
     * @param isInsert true if new Rows were added, false - otherwise
     * @param endRow   parameter to prevent reading same cells twice when both rows and columns
     *                 were added or removed, these cells are read in addMissingRows(...) method
     */
    private void addMissingColumns(AOOReader aooReader, int startCol, int endRow, boolean isInsert) {
        String tmp;
        for (int i = startCol; i <= aooReader.getLastCol(); i++) {
            for (int j = 0; j <= endRow; j++) {
                tmp = getStringData(aooReader, j, i);
                if (!tmp.trim().isEmpty()) {
                    if (isInsert) {
                        createAuditLogEntry(j, i, "", tmp);
                    } else {
                        createAuditLogEntry(j, i, tmp, "");
                    }
                }
            }
        }
    }

    private String getStringData(AOOReader aooReader, int row, int col) {
        String res = aooReader.getFormula(col, row);
        if (res == null) {
            ReportCell cell = aooReader.getData(col, row);
            res = cell.getValue() == null ? (cell.getTextValue() == null ? "" : cell.getTextValue()) : Double.toString(cell.getValue());
        } else {
            // Convert result to human readable format
            res = res.replace("of:=", "=").replace("COM.SUN.STAR.SHEET.ADDIN.CALCADDINS.", "");
        }
        return res;
    }

    private void createAuditLogEntry(int row, int col, String oldVal, String newVal) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActorId(auditLogLocal.getCurrentUserLogin() + " " + CommonUtil.getCurrentClientIpAddress());
        auditLog.setEntityId(getObjectJson(newTemplate.getReportTemplatePk()));
        auditLog.setEntityName(MessagesUtil.getString(newTemplate.getClass().getName()));
        auditLog.setEntityName(MessagesUtil.getString(newTemplate.getClass().getName()));
        auditLog.setEntityProperty(getObjectJson(Arrays.asList("TEMPLATE")));
        auditLog.setOperationType(OperationType.EDIT);
        auditLog.setRelevanceTime(new Date());
        auditLog.setEntityPropertyOldValue("Cell (" + row + ", " + col + ") = [" + oldVal + "]");
        auditLog.setEntityPropertyNewValue("Cell (" + row + ", " + col + ") = [" + newVal + "]");

        auditLogLocal.storeAuditLog(auditLog);
    }

    private String getObjectJson(Object obj) {
        if (obj == null) {
            return "NULL";
        }
        try {
            return AuditLogJsonSerializer.getInstance().getJsonWriter().writeValueAsString(obj);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
