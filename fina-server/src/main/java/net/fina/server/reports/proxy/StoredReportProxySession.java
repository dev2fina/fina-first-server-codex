package net.fina.server.reports.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReportFilter;
import net.fina.common.client.reports.ReportConstants;
import net.fina.common.client.reports.ReportType;
import net.fina.common.client.reports.ScheduledReportInfo;
import net.fina.common.shared.SortField;
import net.fina.common.shared.report.StoredReportModelSimple;
import net.fina.reporting.model.ReportInfoDetailModel;
import net.fina.reporting.model.ScheduleReportMetaModel;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.api.StoredReportLocal;
import net.fina.server.reports.entity.StoredReport;
import net.fina.server.reports.entity.StoredReportPk;
import org.apache.commons.collections.comparators.NullComparator;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class StoredReportProxySession {
    private final Logger log = Logger.getLogger(getClass().getName());
    @Inject
    private StoredReportLocal storedReportLocal;
    @Inject
    private ReportLocal reportLocal;
    @Inject
    private ReportProxySession reportProxySession;

    public List<ScheduleReportMetaModel> loadStoredReportFolders() {
        long langId = ThreadLocalHolder.getLanguage().getId();

        Map<ReportFilter, Object> filter = new HashMap<>();
        filter.put(ReportFilter.TYPE, ReportConstants.NODETYPE_FOLDER);
        filter.put(ReportFilter.REPORT_TYPE, ReportType.DEFAULT);

        return reportLocal.load(filter).stream().map(report -> {
            ScheduleReportMetaModel result = new ScheduleReportMetaModel();
            result.setReportId(report.getId());
            result.setReportCode(report.getCode());
            result.setReportName(report.getDescription().getDescription(langId));
            result.setFolder(true);
            return result;
        }).collect(Collectors.toList());
    }

    public List<ScheduleReportMetaModel> loadChildren(int parentId, SortField sortField) {
        Map<ReportFilter, Object> reportFilterObjectMap = new HashMap<>();
        reportFilterObjectMap.put(ReportFilter.FOLDER_ID, parentId);

        List<ScheduleReportMetaModel> models = new ArrayList<>();
        List<ScheduledReportInfo> scheduledReportInfos = storedReportLocal.loadStoredReports(reportFilterObjectMap, sortField);
        for (ScheduledReportInfo scheduledReportInfo : scheduledReportInfos) {
            long reportLangId = scheduledReportInfo.getLangId();
            models.add(new ScheduleReportMetaModel(scheduledReportInfo, reportLangId));
        }

//        models.sort((o1, o2) -> new NullComparator(false).compare(o1.getReportName(), o2.getReportName()));

        return models;
    }

    @RolesAllowed(PermissionIdNames.STORED_REPORTS_DELETE)
    public void delete(int reportId, int langId, int hashCode) throws FinATypeException {
        StoredReportPk pk = new StoredReportPk();
        pk.setReportId(reportId);
        pk.setHashCode(hashCode);
        pk.setLangId(langId);
        storedReportLocal.delete(pk);
    }

    public ReportInfoDetailModel loadStoredReportIterators(int reportId, int hashCode) {
        long langID = ThreadLocalHolder.getLanguage().getId();
        StoredReportPk reportPk = new StoredReportPk();
        reportPk.setReportId(reportId);
        reportPk.setHashCode(hashCode);
        reportPk.setLangId((int) langID);

        StoredReport storedReport = storedReportLocal.loadGeneratedReport(reportPk);

        if (storedReport != null) {
            return reportProxySession.getReportIteratorsModel(storedReport.getReportPk().getReportId(), storedReport.getInfo());
        }

        return null;
    }

    public List<StoredReportModelSimple> loadDistinctStoredReports(long langId) {
        return storedReportLocal.loadDistinctStoredReports(langId);
    }
}
