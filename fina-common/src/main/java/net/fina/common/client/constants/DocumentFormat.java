package net.fina.common.client.constants;

/**
 * User: nikoloz.
 * Date: 8/31/13.
 * Time: 5:25 PM.
 */
public enum DocumentFormat {

    HTML("html", "text/html"),
    ODS("ods", "application/vnd.oasis.opendocument.spreadsheet"),
    PDF("pdf", "application/pdf"),
    XLS("xls", "application/vnd.ms-excel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    CSV("csv", "text/csv");

    private final String extension;
    private final String mediaType;

    /**
     * Creates the instance of the type.
     */
    private DocumentFormat(String extension, String mediaType) {
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMediaType() {
        return mediaType;
    }
}
