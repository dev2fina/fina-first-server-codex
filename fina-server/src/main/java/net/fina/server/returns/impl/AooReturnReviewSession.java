package net.fina.server.returns.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Default;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.exception.OfficeTypeException;
import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.odstoolkit.AbstractFactory;
import net.fina.odstoolkit.FactoryProducer;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.report.core.ReportUtil;
import net.fina.server.aoo.AOOServiceManager;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.api.ReturnDataStoreLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnDefinitionTemplateRepositoryLocal;
import net.fina.server.returns.api.ReturnReviewLocal;
import net.fina.server.returns.entity.ReturnDefinitionFormat;
import net.fina.server.returns.model.RDataMetaModel;
import net.fina.server.returns.model.RReviewModel;
import net.fina.server.returns.qualifier.AooReturnReview;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Stateless
@Local(ReturnReviewLocal.class)
@Default
@AooReturnReview
@Interceptors(RecordingAuditor.class)
public class AooReturnReviewSession implements ReturnReviewLocal {
    private final Logger log = Logger.getLogger(getClass());

    @EJB
    private LanguageLocal languageLocal;
    @EJB
    private ReturnDataStoreLocal returnDataStoreLocal;
    @EJB
    private ProcessingStoreLocal processingStoreLocal;
    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;
    @EJB
    private AOOServiceManager aooServiceManager;
    @EJB
    private ReturnDefinitionTemplateRepositoryLocal returnDefinitionTemplateRepositoryLocal;

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1L)
    public RReviewModel loadConvertedReturnsContent(List<Long> returnIds, String fileType, long versionId, long langId) throws OfficeTypeException {


        try (StatisticsLogger statLog = new StatisticsLogger(String.format("Review return(s). R id(s):%s, V Id:%s, Lang Id:%s", returnIds.toString(), versionId, langId))) {
            statLog.logMessage("Start");

            RReviewModel result = new RReviewModel();

            Language language = languageLocal.getLanguageByCode(languageLocal.getLanguageCodeById(langId));
            DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            byte[] content = null;
            String fileName = "";

            AooWriterBase workBookOdfToolkitReader = null;

            statLog.logStage("Get mdt nodes by parent id");
            final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();

            statLog.logStage("Load return values");
            final Map<Long, ProcessItem> returnItems = processingStoreLocal.loadReturnNodesValue(returnIds, versionId, null, langId, allMdtNodesByParentId);

            statLog.logStage("Load return formats");
            final Map<Long, ReturnDefinitionFormat> formats = returnDefinitionLocal.getReturnDefinitionFormatsByReturnIds(returnIds);

            final AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");

            int index = 1;
            for (Long returnId : returnIds) {

                String prefix = (index++) + "/" + returnIds.size() + " R id: " + returnId + " - ";

                statLog.logStage(prefix + "loadReturnData");
                final RDataMetaModel rdm = returnDataStoreLocal.loadReturnData(returnId, versionId, langId, true, returnItems);

                ProcessReturnInfo returnInfo = new ProcessReturnInfo();
                returnInfo.setLangId(langId);
                returnInfo.setNumberFormat(language.getNumberFormat().trim());
                returnInfo.setDateFormat(language.getDateFormat().trim());
                returnInfo.setDateTimeFormat(language.getDateTimeFormat().trim());

                ReturnDefinitionFormat format = formats.get(rdm.getDefinitionId());

                statLog.logStage(prefix + "Load format");
                AooWriterBase odfToolkitReader = factory.getAooWriter(format.getFormat());

                statLog.logStage(prefix + "execute ReturnSheetManager");

                ReturnSheetManager sheetManager = new ReturnSheetManager(odfToolkitReader, rdm, returnInfo);
                sheetManager.execute();

                if (returnIds.size() > 1) {
                    if (workBookOdfToolkitReader == null) {
                        workBookOdfToolkitReader = factory.getAooWriter();
                        workBookOdfToolkitReader.init(workBookOdfToolkitReader.createEmptySpreadsheetDocument());
                        workBookOdfToolkitReader.removeSheetByIndex(0);

                        fileName = rdm.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, rdm.getToDate()) + "_" + rdm.getVersionCode();
                    }
                    statLog.logStage(prefix + "appendSheet");
                    workBookOdfToolkitReader.appendSheet(odfToolkitReader.getCurrentSheet(), rdm.getReturnCode());
                } else {
                    fileName = rdm.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, rdm.getToDate()) + "_" + rdm.getVersionCode() + "_" + rdm.getReturnCode();
                    content = odfToolkitReader.getSpreadsheetDocument();
                }
            }

            if (returnIds.size() > 1 && workBookOdfToolkitReader != null) {
                content = workBookOdfToolkitReader.getSpreadsheetDocument();
            }

            statLog.logStage("Convert to '" + fileType + "'");
            result.setContent(ReportUtil.convert(content, fileType, aooServiceManager.getOfficeManager()));
            result.setFileName(fileName);
            return result;
        } catch (OfficeTypeException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return new RReviewModel();
    }

    private String getFileNameDate(DateFormat fileNameDateFormat, Date date) {
        try {
            return fileNameDateFormat.format(date);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return date.toString();
        }
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1L)
    public RReviewModel loadConvertedReturnsContent(List<Long> returnIds, String fileType, long versionId, long langId, String returnTypeCode) throws OfficeTypeException {

        try (StatisticsLogger statLog = new StatisticsLogger(String.format("FAST Review return(s). R id(s):%s, V Id:%s, Lang Id:%s", returnIds.toString(), versionId, langId))) {
            statLog.logMessage("Start");

            RReviewModel result = new RReviewModel();

            Language language = languageLocal.getLanguageByCode(languageLocal.getLanguageCodeById(langId));
            DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String fileName = "";


            statLog.logStage("Load package('" + returnTypeCode + "') template");
            final byte[] template = returnDefinitionTemplateRepositoryLocal.loadPackageTemplate(returnTypeCode);

            if (template == null) {
                return loadConvertedReturnsContent(returnIds, fileType, versionId, langId);
            }

            statLog.logStage("Get mdt nodes by parent id");
            final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();

            statLog.logStage("Load return values");
            final Map<Long, ProcessItem> returnItems = processingStoreLocal.loadReturnNodesValue(returnIds, versionId, null, langId, allMdtNodesByParentId);

            statLog.logStage("Open package('" + returnTypeCode + "') template");
            final AbstractFactory factory = FactoryProducer.getFactory("jOpenDocument");
            AooWriterBase aooWriter = factory.getAooWriter(template);

            int index = 1;
            List<String> sheets = new ArrayList<>();
            for (Long returnId : returnIds) {

                String prefix = (index++) + "/" + returnIds.size() + " R id: " + returnId + " - ";

                statLog.logStage(prefix + "loadReturnData");
                final RDataMetaModel rdm = returnDataStoreLocal.loadReturnData(returnId, versionId, langId, true, returnItems);

                ProcessReturnInfo returnInfo = new ProcessReturnInfo();
                returnInfo.setLangId(langId);
                returnInfo.setNumberFormat(language.getNumberFormat().trim());
                returnInfo.setDateFormat(language.getDateFormat().trim());
                returnInfo.setDateTimeFormat(language.getDateTimeFormat().trim());

                statLog.logStage(prefix + "execute ReturnSheetManager");
                String sheetName = rdm.getReturnCode();
                aooWriter.setCurrentSheet(sheetName);
                sheets.add(sheetName);

                ReturnSheetManager sheetManager = new ReturnSheetManager(aooWriter, rdm, returnInfo);
                sheetManager.execute();

                fileName = rdm.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, rdm.getToDate()) + "_" + rdm.getVersionCode();
            }

            statLog.logStage("Remove extra sheets");
            aooWriter.removeExtraSheets(sheets);

            if (!fileType.equals("ods")) {
                statLog.logStage("Convert to '" + fileType + "'");
                result.setContent(ReportUtil.convert(aooWriter.getSpreadsheetDocument(), fileType, aooServiceManager.getOfficeManager()));
            } else {
                result.setContent(aooWriter.getSpreadsheetDocument());
            }
            result.setFileName(fileName);

            return result;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            log.info("Try old version");
            return loadConvertedReturnsContent(returnIds, fileType, versionId, langId);
        }

    }
}
