package net.fina.server.reports.api;

import java.util.List;
import java.util.Map;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ScheduleReportFilter;
import net.fina.common.client.reports.ScheduleReportStatus;
import net.fina.common.client.reports.ScheduledReportInfo;
import net.fina.server.reports.entity.ScheduleReport;
import net.fina.server.reports.entity.ScheduleReportId;

public interface ScheduleReportLocal {

    List<ScheduleReport> load(Map<ScheduleReportFilter, Object> fiFilterObjectMap);

    public List<ScheduledReportInfo> load();

    public void save(ScheduleReport scheduleReport) throws FinATypeException;

    void updateStatus(ScheduleReport scheduleReport, ScheduleReportStatus status);

    void delete(List<ScheduleReportId> scheduledReportIds) throws FinATypeException;

    public byte[] loadScheduleReportInfo(ScheduleReportId scheduleReportId);
}
