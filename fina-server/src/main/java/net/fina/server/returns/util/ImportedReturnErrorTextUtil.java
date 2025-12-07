package net.fina.server.returns.util;

import net.fina.common.client.constants.ImportStatus;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.reg.validator.InputValidator;
import net.fina.server.reg.validator.ValidationErrorType;
import net.fina.server.returns.entity.ImportedReturn;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ImportedReturnErrorTextUtil {
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat shortFormatter = new SimpleDateFormat("yyyy/MM/dd");

    public static byte[] buildErrorMessage(ImportedReturn importedReturn) {
        return buildErrorMessage(importedReturn.getFullMessage(),
                importedReturn.getReturnCode(),
                importedReturn.getStatus(),
                importedReturn.getImportStart(),
                importedReturn.getImportEnd(),
                importedReturn.getPeriodStart(),
                importedReturn.getPeriodEnd(),
                importedReturn.getVersionCode());
    }

    public static byte[] buildErrorMessage(FsopImportedReturnMetaModel importedReturn) {
        return buildErrorMessage(importedReturn.getMessage(),
                importedReturn.getReturnCode(),
                importedReturn.getStatus(),
                importedReturn.getImportStart(),
                importedReturn.getImportEnd(),
                importedReturn.getPeriodStart(),
                importedReturn.getPeriodEnd(),
                importedReturn.getVersionCode());
    }

    private static byte[] buildErrorMessage(String message,
                                            String returnCode,
                                            ImportStatus status,
                                            Date importStart,
                                            Date importEnd,
                                            Date periodStart,
                                            Date periodEnd,
                                            String version) {


        StringBuilder sb = new StringBuilder();

        sb.append("Timestamp : ")
                .append(formatter.format(new Date()))
                .append("\r\n\r\n")
                .append("-----------------------------------------------------------------------------\n")
                .append("Code : ").append(returnCode);

        if (version != null) {
            sb.append("\n").append("Version : ").append(version);
        }

        sb.append("\n").append("\t").append("Period Start : ").append(periodStart != null ? shortFormatter.format(periodStart) : "")
                .append("\n").append("Period End : ").append(periodEnd != null ? shortFormatter.format(periodEnd) : "")
                .append("\n").append("Status : ").append(status.toString())
                .append("\n").append("Import Start : ").append(importStart != null ? formatter.format(importStart) : "")
                .append("\n").append("Import End : ").append(importEnd != null ? formatter.format(importEnd) : "")
                .append("\n").append("Message : ").append(message)
                .append("\n\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
