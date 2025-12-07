package net.fina.server.reports.api;

import fina2.reportoo.ReportInfo;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReportFilter;
import net.fina.common.client.reports.ReportType;
import net.fina.report.core.api.ReportProcessorConfig;
import net.fina.report.model.ReportModel;
import net.fina.report.model.ReportPropertyModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.reports.entity.Report;
import net.fina.server.reports.entity.ReportTemplate;
import net.fina.server.reports.entity.ScheduleReport;
import net.fina.server.reports.entity.ScheduleReportId;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nikoloz
 * Date: 7/23/13
 * Time: 12:32 PM
 */
public interface ReportLocal {

    Report generate(ReportModel reportModel, long langId, long userId) throws Exception;

    Report generate(ReportModel reportModel, ReportProcessorConfig config) throws Exception;

    Report generate(int reportId, long langId) throws Exception;

    Report generate(int reportId, long langId, long userId) throws Exception;

    Report generate(ReportModel reportModel, long langId) throws Exception;

    Report generate(int reportId, ReportProcessorConfig config) throws Exception;

    void generateScheduleReport(ScheduleReportId scheduleReportId);

    void generate(ScheduleReport scheduleReport);

    Report save(Report report, long langId) throws FinATypeException;

    ReportTemplate loadReportTemplates(int reportId, long langId);

    Report findById(int reportId);

    List<Integer> loadChildReportId(int parentId);

    List<Report> load(Map<ReportFilter, Object> filter);

    long count(Map<ReportFilter, Object> filter);

    Report createEmptyReport(int parentId, String code, Description description, Integer sequence, int type, ReportType reportType) throws FinATypeException;

    void delete(Integer... reportIds) throws FinATypeException;

    void deleteEmptyReportFolder(int folderId) throws FinATypeException;

    boolean checkIsCodeUnique(Report report);

    int findMaxChildSequence(int parentId);

    boolean hasUserAccess(int reportId);

    List<ReportPropertyModel> loadReportProperties(int reportId);

    void reorder(int neigbourReportId, int selectedReportId, boolean upDown);

    List<Integer> loadReportsByIdSorted(Collection<Integer> ids);

    void moveReport(Report report);

    ReportType getReportTypeById(int reportId);

    String getReportPath(int reportId);

    List<Report> loadReportPathArray(int id);

    boolean reorderReports(int id, int parentId, boolean upDown);

    void updateReportInfo(int reportId, ReportInfo reportInfo) throws FinATypeException;

    void updateReportTemplate(int reportId, byte[] template) throws FinATypeException;

    byte[] renameNamedRange(ReportType reportType,byte[] template,String oldName,String newName) throws FinATypeException;
}
