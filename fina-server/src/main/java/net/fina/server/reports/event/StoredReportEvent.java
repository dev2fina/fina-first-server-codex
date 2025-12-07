package net.fina.server.reports.event;

import net.fina.server.reports.entity.StoredReportPk;

public class StoredReportEvent {
    private final StoredReportPk storedReportPk;
    private final byte[] htmlResult;

    public StoredReportEvent(StoredReportPk storedReportPk, byte[] htmlResult) {
        this.storedReportPk = storedReportPk;
        this.htmlResult = htmlResult;
    }

    public StoredReportPk getStoredReportPk() {
        return storedReportPk;
    }

    public byte[] getHtmlResult() {
        return htmlResult;
    }
}
