package net.fina.server.reports.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReportFilter;
import net.fina.common.client.reports.ScheduledReportInfo;
import net.fina.common.shared.SortField;
import net.fina.common.shared.report.StoredReportModelSimple;
import net.fina.reporting.model.ScheduleReportMetaModel;
import net.fina.server.reports.entity.StoredReport;
import net.fina.server.reports.entity.StoredReportPk;
import net.fina.server.reports.event.StoredReportEvent;

import java.util.List;
import java.util.Map;

public interface StoredReportLocal {

    StoredReport loadGeneratedReport(StoredReportPk reportPk);

    StoredReport loadGeneratedReportLite(StoredReportPk reportPk);

    boolean isGenerated(StoredReportPk reportPk);

    void saveGeneratedReport(StoredReport report) throws FinATypeException;

    void saveGeneratedReportSameTransaction(StoredReport report) throws FinATypeException;

    void delete(StoredReportPk reportPk) throws FinATypeException;

    void delete(List<StoredReportPk> reportPks) throws FinATypeException;

    List<ScheduledReportInfo> loadStoredReports(Map<ReportFilter, Object> filter, SortField sortField);

    void saveReportHtmlResult(StoredReportEvent event);

    StoredReport findGeneratedReport(int reportId, int langId);

    List<ScheduledReportInfo> loadStatistics(int id, long langId, String reportName);

    List<ScheduledReportInfo> getGeneratedReports(int id, long langId, long userId, String reportName);

    boolean existGeneratedReport(StoredReportPk reportPk);

    List<StoredReportModelSimple> loadDistinctStoredReports(long langId);
}
