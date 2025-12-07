package net.fina.server.returns.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.api.ReturnDataStoreLocal;
import net.fina.server.returns.api.ReturnReviewLocal;
import net.fina.server.returns.api.ReturnTypeLocal;
import net.fina.server.returns.converter.ConvertOptions;
import net.fina.server.returns.entity.ReturnType;
import net.fina.server.returns.model.RDataMetaModel;
import net.fina.server.returns.model.RReviewModel;
import net.fina.server.returns.model.ReturnReviewConfigModel;
import net.fina.server.returns.qualifier.PoiReturnReview;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Stateless
@Local(ReturnReviewLocal.class)
@PoiReturnReview
@Interceptors(RecordingAuditor.class)
public class PoiReturnReviewSession implements ReturnReviewLocal {
    private Logger log = Logger.getLogger(getClass());

    @EJB
    private LanguageLocal languageLocal;
    @EJB
    private ReturnDataStoreLocal returnDataStoreLocal;
    @EJB
    private ProcessingStoreLocal processingStoreLocal;
    @Inject
    private ReturnTypeLocal returnTypeLocal;

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1L)
    public RReviewModel loadConvertedReturnsContent(List<Long> returnIds, String fileType, long versionId, long langId) {
        return processReviewModel(returnIds, fileType, versionId, langId, null);
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1L)
    public RReviewModel loadConvertedReturnsContent(List<Long> returnIds, String fileType, long versionId, long langId, String returnTypeCode) {
        return processReviewModel(returnIds, fileType, versionId, langId, returnTypeCode);
    }

    private RReviewModel processReviewModel(List<Long> returnIds, String fileType, long versionId, long langId, String returnTypeCode) {
        RReviewModel result = new RReviewModel();
        try (StatisticsLogger statLog = new StatisticsLogger(String.format("Review return(s). R id(s):%s, V Id:%s, Lang Id:%s", returnIds.toString(), versionId, langId));) {

            statLog.logMessage("Start");

            ReturnType returnType;
            if (returnTypeCode != null) {
                returnType = returnTypeLocal.getReturnTypeByCode(returnTypeCode);
            } else {
                returnType = returnTypeLocal.getReturnTypeByReturnId(returnIds.stream().findFirst().get());
            }

            Language language = languageLocal.getLanguageByCode(languageLocal.getLanguageCodeById(langId));
            DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateTimeFormat = new SimpleDateFormat(language.getDateTimeFormat());

            statLog.logStage("Get mdt nodes by parent id");
            final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();
            statLog.logStage("Load return values");
            final Map<Long, ProcessItem> returnItems = processingStoreLocal.loadReturnNodesValue(returnIds, versionId, null, langId, allMdtNodesByParentId);

            int index = 1;
            String fileName = "";

            Map<ProcessReturnInfo, RDataMetaModel> infoRDataMetaModelMap = new LinkedHashMap<>();

            for (Long returnId : returnIds) {
                String prefix = (index++) + "/" + returnIds.size() + " R id: " + returnId + " - ";

                statLog.logStage(prefix + "loadReturnData");
                final RDataMetaModel rdm = returnDataStoreLocal.loadReturnData(returnId, versionId, langId, true, returnItems);

                ProcessReturnInfo returnInfo = new ProcessReturnInfo();
                returnInfo.setLangId(langId);
                returnInfo.setReturnId(returnId);
                returnInfo.setNumberFormat(language.getNumberFormat().trim());
                returnInfo.setDateFormat(language.getDateFormat().trim());
                returnInfo.setDateTimeFormat(language.getDateTimeFormat().trim());
                infoRDataMetaModelMap.put(returnInfo, rdm);

                if (returnIds.size() > 1) {
                    fileName = rdm.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, rdm.getToDate()) + "_" + rdm.getVersionCode() + "_" + rdm.getReturnCode();
                } else {
                    fileName = rdm.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, rdm.getToDate()) + "_" + rdm.getVersionCode() + "_" + rdm.getReturnCode();
                }

            }

            ConvertOptions convertOptions = fileType != null && !fileType.toUpperCase().equals("XLSX") ? ConvertOptions.valueOf(fileType.toUpperCase()) : null;
            ReturnReviewConfigModel configModel = new ReturnReviewConfigModel.Builder()
                    .df(fileNameDateFormat, dateTimeFormat)
                    .langId(langId)
                    .infoRDataMetaModelMap(infoRDataMetaModelMap)
                    .template(returnType.getFormat())
                    .convertOptions(convertOptions)
                    .build();

            statLog.logStage("execute ReturnSheetManager");

            result.setContent(new PoiReturnReviewProcessor(configModel)
                    .process());
            result.setFileName(fileName);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return result;
    }

    private String getFileNameDate(DateFormat fileNameDateFormat, Date date) {
        try {
            return fileNameDateFormat.format(date);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return date.toString();
        }
    }
}
