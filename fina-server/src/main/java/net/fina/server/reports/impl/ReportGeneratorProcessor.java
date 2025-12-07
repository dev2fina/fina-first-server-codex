package net.fina.server.reports.impl;

import fina2.reportoo.ReportInfo;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.reports.ReportType;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.server.util.ObjectSerializer;
import net.fina.odstoolkit.AbstractFactory;
import net.fina.odstoolkit.AooDocumentPatcher;
import net.fina.odstoolkit.FactoryProducer;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.report.core.ReportUtil;
import net.fina.report.core.api.ReportProcessorConfig;
import net.fina.report.model.ReportModel;
import net.fina.server.aoo.AOOServiceManager;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.api.StoredReportLocal;
import net.fina.server.reports.entity.Report;
import net.fina.server.reports.entity.StoredReport;
import net.fina.server.reports.entity.StoredReportPk;
import net.fina.server.reports.event.StoredReportEvent;
import net.fina.server.reports.model.ReportGeneratorResult;
import net.fina.server.reports.util.ReportPrintUtil;
import net.fina.server.security.api.UserLocal;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
@Interceptors(RecordingAuditor.class)
public class ReportGeneratorProcessor {
    private Logger log = Logger.getLogger(getClass());

    private DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");

    @EJB
    private ReportLocal reportLocal;
    @EJB
    private StoredReportLocal storedReportLocal;
    @EJB
    private AOOServiceManager aooServiceManager;
    @EJB
    private UserLocal userLocal;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Inject
    private Event<StoredReportEvent> storedReportEvent;

    @TransactionTimeout(unit = TimeUnit.HOURS, value = ReportSession.REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public ReportGeneratorResult compileReport(Map<String, Object> parametersMap, String[] ids, int regenerate, long langId, String fileType, int folderId, boolean replaceAllFormulas) throws Exception {

        if (ids.length == 1 && ids[0].length() > 0 && reportLocal.getReportTypeById(Integer.parseInt(ids[0])) == ReportType.EXCEL) {
            return compileExcelReport(parametersMap, ids[0], regenerate, langId, fileType, replaceAllFormulas, -1);
        }

        return compileReport(parametersMap, ids, regenerate, langId, fileType, folderId, userLocal.getCurrentUserId());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = ReportSession.REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public ReportGeneratorResult compileExcelReport(Map<String, Object> parametersMap, String id, int regenerate, long langId, String fileType, boolean replaceAllFormulas, long userId) throws Exception {
        ReportProcessorConfig config = new ReportProcessorConfig();
        config.setReplaceAllFormulas(replaceAllFormulas);
        config.setLangId(langId);
        if (userId > 0) {
            config.setUserId(userId);
        } else {
            config.setUserId(userLocal.getCurrentUserId());
        }

        Object reportModelObject = parametersMap.get(id);
        ReportModel reportModel = (ReportModel) reportModelObject;

        try (StatisticsLogger statLog = new StatisticsLogger("FinA excel report generator. id:" + id + ", User:" + userLocal.getCurrentUserLogin(), log, Logger.Level.INFO)) {

            statLog.logMessage("Start excel report generation");

            StoredReport generatedStoredReport = null;
            if (regenerate <= 0) {
                statLog.logMessage("Fetching stored report");
                generatedStoredReport = storedReportLocal.findGeneratedReport(Integer.parseInt(id), (int) langId);
            }

            Report report = null;
            if (generatedStoredReport == null) {
                statLog.logMessage("Generating report");
                if (reportModel == null) {
                    report = reportLocal.generate(Integer.parseInt(id), config);
                } else {
                    report = reportLocal.generate(reportModel, config);
                }
            } else {
                report = reportLocal.findById(generatedStoredReport.getReportPk().getReportId());
            }

            statLog.logMessage("Excel report generated");

            ReportGeneratorResult result = new ReportGeneratorResult();
            result.setContent(generatedStoredReport == null ? report.getGeneratedContent() : generatedStoredReport.getReportResult());
            result.setFileType(fileType);
            result.setFileName(ReportPrintUtil.getReportName(report, langId));
            result.setReportType(ReportType.EXCEL);

            return result;
        }

    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = ReportSession.REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public ReportGeneratorResult compileReport(Map<String, Object> parametersMap, String[] ids, int regenerate, long langId, String fileType, int folderId, long userId) throws Exception {


        try (StatisticsLogger statLog = new StatisticsLogger("FinA report generator. ids:" + Arrays.toString(ids) + ", User:" + userLocal.getCurrentUserLogin(), log, Logger.Level.INFO)) {
            statLog.logMessage("Start report(s) generation");

            //Find generated folder content
            ReportGeneratorResult result = loadGeneratedFolderReport(folderId, langId, regenerate, parametersMap, statLog, fileType);

            if (result == null) {

                byte[] content = null;
                String fileName = null;
                AooWriterBase workBookOdfToolkitReader = null;

                List<String> sheetNames = new ArrayList<>();

                int count = 1;

                final int[] folderHash = {0};
                Map<Integer, ReportInfo> reportInfoMap = new HashMap<>();

                List<Integer> sortedReportIds = reportLocal.loadReportsByIdSorted(new ArrayList<>(parametersMap.keySet().stream().map(Integer::valueOf).collect(Collectors.toList())));

                boolean enableAsynchronousProcess = ConfigurationUtil.get().get("PARRALEL_REPORT_GENERATION") != null && ConfigurationUtil.get().get("PARRALEL_REPORT_GENERATION").equals("1");
                byte[] reportFinalResult = {};
                boolean experimentalAppendSheetEnable = ConfigurationUtil.get().get("EXP_APPEND_SHEET") != null && ConfigurationUtil.get().get("EXP_APPEND_SHEET").equals("1");

                //Generate report parallel generation
                if (enableAsynchronousProcess) {

                    List<Callable<Report>> reportTasks = new ArrayList<>();

                    int prefixCounter = 0;
                    for (long id : sortedReportIds) {
                        final int countFinal = ++prefixCounter;

                        Callable<Report> callable = new Callable<Report>() {
                            @Override
                            public Report call() throws Exception {
                                int reportId = Integer.parseInt(String.valueOf(id));

                                String logPrefix = countFinal + "/" + parametersMap.size() + ", Report Id:" + reportId + " | ";

                                return process(id, parametersMap, regenerate, langId, fileType, statLog, logPrefix, reportId, reportInfoMap, folderId, userId);
                            }
                        };

                        reportTasks.add(callable);

                    }

                    List<Future<Report>> generateResult = managedExecutorService.invokeAll(reportTasks);

                    AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");


                    if (ids.length > 1) {


                        AooDocumentPatcher patcher = new AooDocumentPatcher();
                        Map<String, byte[]> reportContents = new LinkedHashMap<>();
                        fileName = "reports_" + fileNameDateFormat.format(new Date());

                        for (Future<Report> future : generateResult) {

                            Report generatedReport = future.get();
                            if (experimentalAppendSheetEnable) {

                                String reportDescription = generatedReport.getDescription().getDescription(langId);
                                reportDescription = reportContents.keySet().contains(reportDescription) ? (reportDescription + "_" + fileNameDateFormat.format(new Date())) : reportDescription;
                                reportContents.put(reportDescription, generatedReport.getGeneratedContent());

                            } else {

                                if (workBookOdfToolkitReader == null) {
                                    workBookOdfToolkitReader = factory.getAooWriter();
                                    workBookOdfToolkitReader.init(workBookOdfToolkitReader.createEmptySpreadsheetDocument());
                                    workBookOdfToolkitReader.removeSheetByIndex(0);
                                }
                                String reportSheetName = generatedReport.getDescription().getDescription(langId);
                                if (reportSheetName.equalsIgnoreCase("NONAME")) {
                                    reportSheetName = generatedReport.getDescription().getDescription(langId) + "_" + fileNameDateFormat.format(new Date());
                                }

                                statLog.logStage(count + "Init AOO writer");
                                AooWriterBase temp = factory.getAooWriter();

                                temp.init(generatedReport.getGeneratedContent());

                                if (fileType.equalsIgnoreCase("xlsx") || fileType.equalsIgnoreCase("xls")) {
                                    fixSheetNames(sheetNames, reportSheetName);
                                    reportSheetName = sheetNames.get(sheetNames.size() - 1);
                                }
                                statLog.logStage(count + "Append sheet");
                                workBookOdfToolkitReader.appendSheet(temp.getCurrentSheet(), reportSheetName);
                            }

                            count++;
                        }

                        if (!reportContents.isEmpty()) {
                            statLog.logStage(count + "Append sheet");
                            reportFinalResult = patcher.appendSheet(reportContents);
                        }

                    } else {
                        content = generateResult.get(0).get().getGeneratedContent();
                        fileName = ReportPrintUtil.getReportName(generateResult.get(0).get(), langId);
                    }

                } else {

                    for (long id : sortedReportIds) {
                        int reportId = Integer.parseInt(String.valueOf(id));

                        String logPrefix = count++ + "/" + parametersMap.size() + ", Report Id:" + reportId + " | ";

                        AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");

                        Report report = process(id, parametersMap, regenerate, langId, fileType, statLog, logPrefix, reportId, reportInfoMap, folderId, userId);

                        if (ids.length > 1) {
                            if (workBookOdfToolkitReader == null) {
                                workBookOdfToolkitReader = factory.getAooWriter();
                                workBookOdfToolkitReader.init(workBookOdfToolkitReader.createEmptySpreadsheetDocument());
                                workBookOdfToolkitReader.removeSheetByIndex(0);
                                fileName = "reports_" + fileNameDateFormat.format(new Date());
                            }

                            String reportSheetName = report.getDescription().getDescription(langId);
                            if (reportSheetName.equalsIgnoreCase("NONAME")) {
                                reportSheetName = report.getDescription().getDescription(langId) + "_" + fileNameDateFormat.format(new Date());
                            }

                            statLog.logStage(logPrefix + "Init AOO writer");
                            AooWriterBase temp = factory.getAooWriter();
                            temp.init(report.getGeneratedContent());

                            if (fileType.equalsIgnoreCase("xlsx") || fileType.equalsIgnoreCase("xls")) {
                                fixSheetNames(sheetNames, reportSheetName);
                                reportSheetName = sheetNames.get(sheetNames.size() - 1);
                            }
                            statLog.logStage(logPrefix + "Append sheet");
                            workBookOdfToolkitReader.appendSheet(temp.getCurrentSheet(), reportSheetName);
                        } else {
                            content = report.getGeneratedContent();
                            fileName = ReportPrintUtil.getReportName(report, langId);
                        }

                    }
                }


                if (ids.length > 1) {
                    statLog.logStage("Get Generated report sheet document");
                    content = experimentalAppendSheetEnable ? reportFinalResult : workBookOdfToolkitReader.getSpreadsheetDocument();
                }

                // Save folder stored Report.
                saveGeneratedFolderStoredReport(folderId, langId, regenerate, folderHash[0], content, reportInfoMap, statLog, userId);

                if (fileName == null) {
                    fileName = UUID.randomUUID().toString();
                }

                result = new ReportGeneratorResult();
                result.setContent(content);
                result.setFileName(fileName);
                result.setFileType(fileType);
                result.setReportType(ReportType.DEFAULT);
            }

            return result;
        }
    }

    private Report process(long id, Map<String, Object> parametersMap, int regenerate, long langId, String fileType, StatisticsLogger statLog, String logPrefix, int reportId, Map<Integer, ReportInfo> reportInfoMap, int folderHash, long userId) throws Exception {

        Object reportModelObject = parametersMap.get(String.valueOf(id));

        Report report;

        StoredReport generatedStoredReport = null;

        if (reportModelObject == null) {
            if (regenerate <= 0) {
                statLog.logStage(logPrefix + "Find generated Report");
                generatedStoredReport = storedReportLocal.findGeneratedReport(reportId, (int) langId);
            }
            if (generatedStoredReport == null) {
                statLog.logStage("Generate (no parameters)");
                report = reportLocal.generate(reportId, langId, userId);
            } else {
                statLog.logStage(logPrefix + "Find report by id (no parameters)");
                report = reportLocal.findById(reportId);
            }
        } else {
            ReportModel reportModel = (ReportModel) reportModelObject;
            ReportInfo info = ReportUtil.getReportInfo(reportModel.getInfo());

            StoredReportPk storedReportPk = new StoredReportPk();
            storedReportPk.setLangId((int) langId);
            storedReportPk.setReportId(reportId);
            int reportHash = info.hashCode();

            reportInfoMap.put(reportModel.getId(), info);
            folderHash += reportHash;
            storedReportPk.setHashCode(reportHash);

            if (regenerate <= 0) {
                statLog.logStage(logPrefix + "Load generated Report");
                generatedStoredReport = storedReportLocal.loadGeneratedReport(storedReportPk);
            }
            if (generatedStoredReport == null) {
                statLog.logStage(logPrefix + "Generate (with parameters)");
                report = reportLocal.generate(reportModel, langId, userId);
            } else {
                statLog.logStage(logPrefix + "Find report by id (with parameters)");
                report = reportLocal.findById(reportId);
            }
        }

        byte[] content = null;

        if (generatedStoredReport != null) {
            content = generatedStoredReport.getReportResult();
        } else {
            content = report.getGeneratedContent();
        }

        if (generatedStoredReport == null) {
            statLog.logStage(logPrefix + "Convert to HTML");
            byte[] reportHtmlContent = ReportUtil.convert(content, fileType, aooServiceManager.getOfficeManager());

            StoredReportPk storedReportPk = new StoredReportPk();
            byte[] info;
            if (reportModelObject != null) {
                info = ((ReportModel) reportModelObject).getInfo();
            } else {
                info = report.getInfo();
            }
            ReportInfo reportInfo = ReportUtil.getReportInfo(info);

            storedReportPk.setHashCode(reportInfo.hashCode());
            storedReportPk.setLangId((int) langId);
            storedReportPk.setReportId(reportId);

            statLog.logStage(logPrefix + "Save HTML report");
            storedReportEvent.fire(new StoredReportEvent(storedReportPk, reportHtmlContent));
//            storedReportLocal.saveReportHtmlResult(storedReportPk, reportHtmlContent);
        }

        report.setGeneratedContent(content);

        return report;
    }

    private void fixSheetNames(List<String> sheetNames, String name) {
        try {
            String result = name.length() > 30 ? name.substring(0, 30) : name;
            result = (sheetNames.contains(result)) ? (result.length() > 25 ? (result.substring(0, result.length() - 5) + "-" + UUID.randomUUID().toString().substring(0, 4)) : result + "-" + UUID.randomUUID().toString().substring(0, 4)) : result;
            if (sheetNames.contains(result)) {
                fixSheetNames(sheetNames, result);
            } else {
                sheetNames.add(result);
            }
        } catch (Throwable t) {
            log.error(t.getMessage());
        }
    }

    private ReportGeneratorResult loadGeneratedFolderReport(int folderId, long langId, int regenerate, Map<String, Object> parametersMap, StatisticsLogger statLog, String fileType) {

        ReportGeneratorResult result = null;

        if (folderId > 0 && regenerate <= 0) {
            try {

                int folderHash = 0;

                for (Map.Entry<String, Object> entry : parametersMap.entrySet()) {

                    Object reportModelObject = entry.getValue();

                    if (reportModelObject != null) {
                        ReportModel reportModel = (ReportModel) reportModelObject;
                        ReportInfo info = ReportUtil.getReportInfo(reportModel.getInfo());
                        folderHash += info.hashCode();
                    }
                }

                StoredReportPk storedReportPk = new StoredReportPk();
                storedReportPk.setLangId((int) langId);
                storedReportPk.setReportId(folderId);
                storedReportPk.setHashCode(folderHash);

                statLog.logStage("Load generated Report. Folder Id:" + folderId);
                StoredReport storedReport = storedReportLocal.loadGeneratedReport(storedReportPk);

                if (storedReport != null) {
                    result = new ReportGeneratorResult();
                    result.setContent(storedReport.getReportResult());
                    result.setFileName("reports_" + fileNameDateFormat.format(new Date()));
                    result.setFileType(fileType);
                    result.setReportType(fileType.equals("excel") || fileType.equals("xlsx") ? ReportType.EXCEL : ReportType.DEFAULT);
                }

            } catch (Throwable t) {
                log.error(t.getMessage());
            }
        }

        return result;
    }

    private void saveGeneratedFolderStoredReport(int folderId, long langId, int regenerate, int folderHash, byte[] content, Map<Integer, ReportInfo> reportInfoMap, StatisticsLogger statLog, long userId) {

        if (folderId > 0) {
            try {
                statLog.logStage("Save folder stored Report. Folder Id:" + folderId);

                //Save stored report
                StoredReportPk storedReportPk = new StoredReportPk();
                storedReportPk.setHashCode(folderHash);
                storedReportPk.setLangId((int) langId);
                storedReportPk.setReportId(folderId);

                if (!storedReportLocal.existGeneratedReport(storedReportPk) || regenerate > 0) {

                    StoredReport storedReport = new StoredReport();
                    storedReport.setReportPk(storedReportPk);

                    ReportInfo info = ReportUtil.mergeReportInfo(reportInfoMap);
                    if (info != null) {
                        storedReport.setInfo(ObjectSerializer.serialize(info));
                    } else {
                        storedReport.setInfo(new byte[0]);
                    }

                    storedReport.setReportResult(content);
                    storedReport.setStoreDate(new Date());
                    storedReport.setUserId(userId);
                    storedReport.setReportType(ReportType.DEFAULT);

                    storedReportLocal.saveGeneratedReportSameTransaction(storedReport);
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
    }
}
