package net.fina.server.reports.event;

import net.fina.report.model.ReportAlertMetaModel;

import java.util.List;

public class ReportAlertsEvent {

    private final List<ReportAlertMetaModel> alerts;

    public ReportAlertsEvent(List<ReportAlertMetaModel> alerts) {
        this.alerts = alerts;
    }

    public List<ReportAlertMetaModel> getAlerts() {
        return alerts;
    }
}
