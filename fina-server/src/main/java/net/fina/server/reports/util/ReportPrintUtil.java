package net.fina.server.reports.util;

import jakarta.ws.rs.core.MediaType;
import net.fina.common.client.exception.OfficeTypeException;
import net.fina.common.shared.ContentModel;
import net.fina.report.core.ReportUtil;
import net.fina.server.reports.entity.Report;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ReportPrintUtil {

    public static ContentModel print(OfficeManager officeManager, byte[] reportContent, String fileType, String fileName, String contextPath) throws IOException, OfficeTypeException {
        return printContent(officeManager, reportContent, fileType, fileName, contextPath);
    }

    private static ContentModel printContent(OfficeManager officeManager, byte[] reportContent, String fileType, String fileName, String contextPath) throws IOException, OfficeTypeException {
        if (officeManager != null) {
            reportContent = ReportUtil.convert(reportContent, fileType, officeManager);
        }

        Map<String, String> headers = new HashMap<>();
        if (!fileType.equalsIgnoreCase("html")) {
            String filenameEncoded = URLEncoder.encode(fileName + "." + fileType.toLowerCase(), StandardCharsets.UTF_8).replace("+", "%20");
            headers.put("Content-Disposition", "attachment; filename*=UTF-8''" + filenameEncoded);
        }

        String contentType = MediaType.APPLICATION_OCTET_STREAM;

        switch (fileType.toLowerCase()) {
            case "pdf":
                contentType = "application/pdf";
                break;
            case "xls":
                contentType = "application/vnd.ms-excel";
                break;
            case "xlsx":
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                break;
            case "rtf":
                contentType = "application/rtf";
                break;
            case "txt":
                contentType = MediaType.TEXT_PLAIN;
                break;
            case "csv":
                contentType = "application/csv";
                break;
            case "ods":
                contentType = "application/vnd.oasis.opendocument.spreadsheet";
                break;
            case "html":
                contentType = MediaType.TEXT_HTML;
                return printHtml(null, reportContent, fileName, contentType, contextPath);
        }

        ContentModel result = new ContentModel(reportContent, fileName, contentType);
        result.setHeaders(headers);

        return result;

    }

    public static ContentModel printHtml(OfficeManager officeManager, byte[] reportContent, String fileName, String contextPath) throws IOException, OfficeTypeException {
        return printHtml(officeManager, reportContent, fileName, MediaType.TEXT_HTML, contextPath);
    }

    private static ContentModel printHtml(OfficeManager officeManager, byte[] reportContent, String fileName, String contentType, String contextPath) throws IOException, OfficeTypeException {
        if (officeManager != null) {
            reportContent = ReportUtil.convert(reportContent, "html", officeManager);
        }

        StringBuilder additionalHtml = new StringBuilder("<TITLE>" + fileName + "</TITLE>")
                /**
                 <script src="../js/jquery.min.js"></script>
                 <script src="../js/BackToTop.js"></script>
                 <link rel="stylesheet" type="text/css" href="../js/BackToTop.css" title="Style">
                 */
                .append(" <script src='").append(contextPath).append("/js/jquery.min.js'").append("></script>")
                .append(" <script src='").append(contextPath).append("/js/BackToTop.js'").append("></script>")
                .append("<link rel='stylesheet' type='text/css' href='").append(contextPath).append("/js/BackToTop.css'").append(" title='Style'>");
        reportContent = new String(reportContent).replace("<TITLE></TITLE>", additionalHtml).getBytes(StandardCharsets.UTF_8);
        return new ContentModel(reportContent, fileName, contentType);
    }

    public static String getReportName(Report report, long langId) {
        if (report.getDescription() != null && report.getDescription().getDescription(langId) != null) {
            String description = report.getDescription().getDescription(langId);
            return description.equals("NONAME") ? report.getCode() : description;
        }

        return report.getCode();
    }

}
