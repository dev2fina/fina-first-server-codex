package net.fina.server.reports.proxy;

import fina2.period.PeriodPK;
import fina2.reportoo.ReportInfo;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.exception.OfficeTypeException;
import net.fina.common.client.reports.ScheduleReportStatus;
import net.fina.common.client.reports.ScheduledReportInfo;
import net.fina.common.server.util.ObjectSerializer;
import net.fina.common.shared.ContentModel;
import net.fina.common.shared.report.ReportInfoModel;
import net.fina.report.model.Parameter;
import net.fina.common.shared.report.ReportParameterType;
import net.fina.reporting.model.ScheduleReportCreationModel;
import net.fina.reporting.model.ScheduleReportMetaModel;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.aoo.AOOServiceManager;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.api.ScheduleReportLocal;
import net.fina.server.reports.api.StoredReportLocal;
import net.fina.server.reports.entity.*;
import net.fina.server.reports.util.ReportPrintUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class ReportScheduleProxySession {

    private final Logger log = Logger.getLogger(getClass().getName());
    @Inject
    private ScheduleReportLocal scheduleReportLocal;
    @Inject
    private StoredReportLocal storedReportLocal;
    @Inject
    private ReportLocal reportLocal;

    @Inject
    private AOOServiceManager aooServiceManager;


    public List<ScheduleReportMetaModel> load() {
        long langID = ThreadLocalHolder.getLanguage().getId();
        final List<ScheduledReportInfo> scheduledReportInfos = scheduleReportLocal.load();

        List<ScheduleReportMetaModel> parentFolders = scheduledReportInfos.stream().filter(ScheduledReportInfo::isFolder)
                .map(info -> new ScheduleReportMetaModel(info, langID)).collect(Collectors.toList());

        for (ScheduleReportMetaModel parentFolder : parentFolders) {
            parentFolder.setChildren(getChildrenScheduleReports(parentFolder, scheduledReportInfos, langID));
        }

        return parentFolders;
    }

    public void saveScheduleReport(ScheduleReportCreationModel scheduleReportCreationModel) {
        setParametersAndSave(scheduleReportCreationModel);
    }

    private List<ScheduleReportMetaModel> getChildrenScheduleReports(ScheduleReportMetaModel parent, List<ScheduledReportInfo> scheduledReportInfos, long langID) {
        List<ScheduleReportMetaModel> children = new ArrayList<>();
        for (ScheduledReportInfo info : scheduledReportInfos) {
            if (!info.isFolder() && info.getParentId() == parent.getReportId()) {
                children.add(new ScheduleReportMetaModel(info, langID));
            }
        }

        return children;
    }

    public void delete(int reportId, int langId, int hashCode) throws FinATypeException {
        scheduleReportLocal.delete(Collections.singletonList(new ScheduleReportId(reportId, langId, hashCode)));
    }

    public ContentModel review(int reportId, String fileType, int hashCode, String reportName, String contextPath, int langId) throws OfficeTypeException {
        try {
            StoredReportPk storedReportPk = new StoredReportPk();

            storedReportPk.setReportId(reportId);
            storedReportPk.setLangId(langId);
            storedReportPk.setHashCode(hashCode);

            StoredReport storedReport = storedReportLocal.loadGeneratedReport(storedReportPk);

            if (storedReport == null) {
                return getEmptyExcelReportContent(reportName);
            }

            if (storedReport.getReportType() == null) {
                return ReportPrintUtil.print(aooServiceManager.getOfficeManager(), storedReport.getReportResult(), fileType, reportName, contextPath);
            } else {
                switch (storedReport.getReportType()) {
                    case DEFAULT:
                        return ReportPrintUtil.print(aooServiceManager.getOfficeManager(), storedReport.getReportResult(), fileType, reportName, contextPath);
                    case PENTAHO:
                        //TODO Show generated pentaho report
                        break;
                    case EXCEL:
                        return ReportPrintUtil.print(null, storedReport.getReportResult(), fileType, reportName, contextPath);
                }
            }
        } catch (OfficeTypeException e) {
            throw e;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return null;
    }

    public void runScheduledReport(List<ScheduleReportMetaModel> scheduleReports) {
        scheduleReports.forEach(r -> {
            reportLocal.generateScheduleReport(new ScheduleReportId(r.getReportId(), r.getLangId(), r.getHashcode()));
        });
    }


    private void setParametersAndSave(ScheduleReportCreationModel model) {
        try {
            long langId = ThreadLocalHolder.getLanguage().getId();
            ScheduleReport scheduleReport = new ScheduleReport();
            scheduleReport.setReportId(model.getReportId());
            scheduleReport.setLangId((int) langId);
            scheduleReport.setStatus(ScheduleReportStatus.STATUS_SCHEDULED);
//            scheduleReport.setState(state);

            Report report = reportLocal.findById(scheduleReport.getReportId());
            ReportInfo reportInfo = (ReportInfo) ObjectSerializer.deSerialize(report.getInfo());

            if (model.getReportParametersInfo() != null) {

                scheduleReport.setOnDemand(model.isOnDemand() ? 1 : 0);
                scheduleReport.setScheduleTime(model.getScheduleTime());
                scheduleReport.setRepositoryNodeId(model.getRepositoryNodeId());
                scheduleReport.setFileStorageLocation(model.getFileStorageLocation());
                scheduleReport.setNotificationMails(model.getNotificationMails());
                scheduleReport.setRepositoryFolderName(model.getRepositoryFolderName());

                for (ReportInfoModel parameter : model.getReportParametersInfo().getParameters()) {
                    List data;
                    if (parameter.getType() == ReportParameterType.PERIOD) {
                        data = new ArrayList<>();
                        for (int i = 0; i < parameter.getValues().size(); i++) {
                            data.add(new PeriodPK(Integer.parseInt(parameter.getValues().get(i))));
                        }
                    } else {
                        data = parameter.getValues();
                    }
                    ((Parameter) reportInfo.parameters.get(parameter.getName())).setValues(data);
                }


                for (ReportInfoModel iterator : model.getReportParametersInfo().getIterators()) {
                    List data;
                    if (iterator.getType() == ReportParameterType.PERIOD) {
                        data = new ArrayList<>();
                        for (int i = 0; i < iterator.getValues().size(); i++) {
                            data.add(new PeriodPK(Integer.parseInt(iterator.getValues().get(i))));
                        }
                    } else {
                        data = iterator.getValues();
                    }
                    ((net.fina.report.model.Iterator) reportInfo.iterators.get(iterator.getName())).setValues(data);
                }

            }


            scheduleReport.setInfo(ObjectSerializer.serialize(reportInfo));
            scheduleReport.setHashcode(reportInfo.hashCode());
            scheduleReportLocal.save(scheduleReport);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private ContentModel getEmptyExcelReportContent(String reportName) throws FinATypeException {
        byte[] excelContent;

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            workbook.createSheet("Sheet1");
            workbook.write(bos);
            excelContent = bos.toByteArray();

        } catch (IOException e) {
            throw new FinATypeException("Failed to create empty excel file");
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Disposition", "attachment; filename*=UTF-8''" +
                URLEncoder.encode(reportName + ".xlsx", StandardCharsets.UTF_8).replace("+", "%20"));

        ContentModel contentModel = new ContentModel(
                excelContent,
                reportName + ".xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        contentModel.setHeaders(headers);
        return contentModel;
    }
}
