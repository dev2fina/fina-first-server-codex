package net.fina.server.rvc.impl;

import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.xml.bind.JAXBException;
import net.fina.common.client.filter.MDTComparisonFilter;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.fsop.view.model.*;
import net.fina.odstoolkit.AbstractFactory;
import net.fina.odstoolkit.FactoryProducer;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.report.core.ReportUtil;
import net.fina.server.aoo.AOOServiceManager;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.xml.v2.Node;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.converter.ConvertOptions;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnDefinitionFormat;
import net.fina.server.returns.entity.ReturnItemLite;
import net.fina.server.returns.entity.ReturnType;
import net.fina.server.returns.impl.FsopSheetManager;
import net.fina.server.returns.impl.PoiReturnReviewProcessor;
import net.fina.server.returns.model.*;
import net.fina.server.returns.xml.Body;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.Item;
import net.fina.server.rvc.api.ReturnVersionControlLocal;
import net.fina.server.rvc.api.xml.MdtXmlParser;
import net.fina.server.rvc.api.xml.ReturnXmlParser;
import net.fina.server.rvc.api.xml.StatusXmlParser;
import net.fina.server.rvc.event.ReturnItemsStoreEvent;
import net.fina.server.rvc.event.ReturnStatusStoreEvent;
import net.fina.server.rvc.model.ReturnHeaderMetaModel;
import net.fina.server.rvc.xml.FileType;
import net.fina.server.rvc.xml.ObjectFactory;
import net.fina.server.rvc.xml.ReturnStatusesType;
import net.fina.server.rvc.xml.StatusType;
import net.fina.server.store.api.RepositoryLocal;
import net.fina.server.store.model.RepositoryFile;
import net.fina.server.store.qualifier.Return;
import net.fina.server.store.qualifier.Template;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
@Local(ReturnVersionControlLocal.class)
@Interceptors(RecordingAuditor.class)
public class ReturnVersionControlSession implements ReturnVersionControlLocal {

    @Inject
    private Logger log;
    @Inject
    private EntityManager em;

    @Return
    @Inject
    private Session returnRepositorySession;

    @Template
    @Inject
    private Session templateRepositorySession;

    @EJB
    private RepositoryLocal repositoryLocal;
    @EJB
    private ReturnDefinitionLocal definitionLocal;
    @EJB
    private MDTNodeLocal mdtNodeLocal;
    @EJB
    private LanguageLocal languageLocal;
    @EJB
    private ProcessingStoreLocal processingStoreLocal;
    @Inject
    private AOOServiceManager aooServiceManager;
    @Inject
    private ReturnDefinitionLocal returnDefinitionLocal;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeReturn(ReturnItemsStoreEvent itemsStoreEvent, ReturnStatusStoreEvent statusStoreEvent) {
        try {
            long returnId = itemsStoreEvent.getReturnId();

            ReturnHeaderMetaModel returnHeader = loadReturnHeader(returnId);


            String path = getFileBasePath(returnHeader);
            long start = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();

            RepositoryFile returnFile = createRepositoryFile(path, ReturnVersionControlFileTypes.RETURN_FILE_NAME, getReturnFile(returnHeader, itemsStoreEvent));
            repositoryLocal.saveFile(returnRepositorySession, returnFile);
            sb.append("\n")
                    .append("--------------------------------------------------------------------")
                    .append("\n")
                    .append("save return file : ").append(System.currentTimeMillis() - start).append("ms")
                    .append("\n")
                    .append("--------------------------------------------------------------------");
            log.info(sb.toString());
            start = System.currentTimeMillis();
            RepositoryFile mdtFile = createRepositoryFile(path, ReturnVersionControlFileTypes.MDT_FILE_NAME, getMdtFile(itemsStoreEvent));

            repositoryLocal.saveFile(returnRepositorySession, mdtFile);
            sb = new StringBuilder();
            sb.append("\n")
                    .append("--------------------------------------------------------------------")
                    .append("\n")
                    .append("save MDT file : ").append(System.currentTimeMillis() - start).append("ms")
                    .append("\n")
                    .append("--------------------------------------------------------------------");
            log.info(sb.toString());


            start = System.currentTimeMillis();
            repositoryLocal.saveFile(returnRepositorySession,
                    createRepositoryFile(
                            path,
                            ReturnVersionControlFileTypes.STATUS_FILE_NAME,
                            getStatusFile(statusStoreEvent, returnFile.getFileVersionId(), mdtFile.getFileVersionId(), path, returnHeader))
            );
            sb = new StringBuilder();
            sb.append("\n")
                    .append("--------------------------------------------------------------------")
                    .append("\n")
                    .append("save Status file : ").append(System.currentTimeMillis() - start).append("ms")
                    .append("\n")
                    .append("--------------------------------------------------------------------");
            log.info(sb.toString());

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    @Override
    public RepositoryFile loadReturnFileByIdAndFileType(long returnId, String fileName) {
        ReturnHeaderMetaModel returnHeader = loadReturnHeader(returnId);

        RepositoryFile repositoryFile = new RepositoryFile();

        String path = getFileBasePath(returnHeader);


        repositoryFile.setFileName(fileName);
        repositoryFile.setPath(path);
        try {
            repositoryFile.setContent(IOUtils.toByteArray(repositoryLocal.getLatestVersionFileStream(returnRepositorySession, path, fileName)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return repositoryFile;
    }

    @Override
    public InputStream getFileStreamByVersion(String parentPath, String fileName, String versionId) {
        return repositoryLocal.getFileStreamByVersion(returnRepositorySession, parentPath, fileName, versionId);
    }

    @Override
    public FsopReviewModel getFsopReviewModel(List<FsopDataMetaModel> dataModels, String langCode, String fileType) {
        FsopReviewModel reviewModel = new FsopReviewModel();

        Language language = languageLocal.getLanguageByCode(langCode);
        DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateTimeFormat = new SimpleDateFormat(language.getDateTimeFormat());
        DateFormat statusSheetDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        try {
            Set<Long> returnDefinitionList = returnDefinitionLocal.gerReturnDefinitionIds(Collections.singletonList(dataModels.get(0).getReturnCode())).keySet();
            long definitionId = 0;
            if (returnDefinitionList.size() > 0) {
                definitionId = returnDefinitionList.stream().findFirst().orElse(null);
            }

            ReturnType returnType = returnDefinitionLocal.getReturnDefinitionById(definitionId).getReturnType();

            if (returnType.isExcelTemplate()) {
                reviewModel = getPoiFsopReview(dataModels,
                        language,
                        fileNameDateFormat,
                        dateTimeFormat,
                        statusSheetDateFormat,
                        fileType,
                        definitionId,
                        returnType);
            } else {

                reviewModel = getAooFsopReview(dataModels,
                        language,
                        fileNameDateFormat,
                        statusSheetDateFormat,
                        fileType,
                        definitionId);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return reviewModel;
    }

    private FsopReviewModel getPoiFsopReview(List<FsopDataMetaModel> dataModels,
                                             Language language,
                                             DateFormat fileNameDateFormat,
                                             DateFormat dateTimeFormat,
                                             DateFormat statusSheetDateFormat,
                                             String fileType,
                                             long definitionId,
                                             ReturnType returnType) throws Exception {
        FsopReviewModel result = new FsopReviewModel();
        Map<ProcessReturnInfo, RDataMetaModel> infoRDataMetaModelMap = new LinkedHashMap<>();

        String fileName = "";
        long langId = language.getId();

        for (FsopDataMetaModel fsopDataMetaModel : dataModels) {

            ProcessReturnInfo returnInfo = new ProcessReturnInfo();
            returnInfo.setLangId(langId);
            returnInfo.setReturnId(definitionId);
            returnInfo.setNumberFormat(language.getNumberFormat().trim());
            returnInfo.setDateFormat(language.getDateFormat().trim());
            returnInfo.setDateTimeFormat(language.getDateTimeFormat().trim());
            infoRDataMetaModelMap.put(returnInfo, convertFromFsopModel(fsopDataMetaModel));

            if (dataModels.size() > 1) {
                fileName = fsopDataMetaModel.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, fsopDataMetaModel.getToDate()) + "_" + fsopDataMetaModel.getVersionCode() + "_" + fsopDataMetaModel.getReturnCode();
            } else {
                fileName = fsopDataMetaModel.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, fsopDataMetaModel.getToDate()) + "_" + fsopDataMetaModel.getVersionCode() + "_" + fsopDataMetaModel.getReturnCode();
            }

        }

        ConvertOptions convertOptions = fileType != null && !fileType.toUpperCase().equals("XLSX") ? ConvertOptions.valueOf(fileType.toUpperCase()) : null;
        ReturnReviewConfigModel configModel = new ReturnReviewConfigModel.Builder()
                .df(fileNameDateFormat, dateTimeFormat)
                .langId(1)
                .infoRDataMetaModelMap(infoRDataMetaModelMap)
                .template(returnType.getFormat())
                .convertOptions(convertOptions)
                .build();


        result.setContent(new PoiReturnReviewProcessor(configModel)
                .process());
        result.setFileName(fileName);

        return result;
    }


    private FsopReviewModel getAooFsopReview(List<FsopDataMetaModel> dataModels,
                                             Language language,
                                             DateFormat fileNameDateFormat,
                                             DateFormat statusSheetDateFormat,
                                             String fileType,
                                             long definitionId) throws Exception {
        String fileName = "";
        byte[] content = null;
        FsopReviewModel reviewModel = new FsopReviewModel();


        AooWriterBase workBookOdfToolkitReader = null;
        final AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");

        ReturnDefinitionFormat format = returnDefinitionLocal.getReturnDefinitionFormat(definitionId);

        for (FsopDataMetaModel dataModel : dataModels) {
            AooWriterBase odfToolkitReader = factory.getAooWriter(format != null ? format.getFormat() : null);
            ProcessReturnInfo returnInfo = new ProcessReturnInfo();
            returnInfo.setLangId(language.getId());
            returnInfo.setNumberFormat(language.getNumberFormat().trim());
            returnInfo.setDateFormat(language.getDateFormat().trim());
            returnInfo.setDateTimeFormat(language.getDateTimeFormat().trim());

            FsopSheetManager sheetManager = new FsopSheetManager(odfToolkitReader, dataModel, returnInfo);
            sheetManager.execute();

            if (dataModels.size() > 1) {
                if (workBookOdfToolkitReader == null) {
                    workBookOdfToolkitReader = factory.getAooWriter();
                    workBookOdfToolkitReader.init(workBookOdfToolkitReader.createEmptySpreadsheetDocument());
                    workBookOdfToolkitReader.removeSheetByIndex(0);

                    fileName = dataModel.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, dataModel.getToDate()) + "_" + dataModel.getVersionCode();
                }

                workBookOdfToolkitReader.appendSheet(odfToolkitReader.getCurrentSheet(), dataModel.getReturnCode() + "_" + statusSheetDateFormat.format(new Date(Long.valueOf(dataModel.getStatusDate()))));
            } else {
                fileName = dataModel.getFiCode().trim() + "_" + getFileNameDate(fileNameDateFormat, dataModel.getToDate()) + "_" + dataModel.getVersionCode() + "_" + dataModel.getReturnCode();
                content = odfToolkitReader.getSpreadsheetDocument();
            }
        }

        if (dataModels.size() > 1 && workBookOdfToolkitReader != null) {
            content = workBookOdfToolkitReader.getSpreadsheetDocument();
        }

        reviewModel.setContent(ReportUtil.convert(content, fileType, aooServiceManager.getOfficeManager()));
        reviewModel.setFileName(fileName);

        return reviewModel;
    }


    private RDataMetaModel convertFromFsopModel(FsopDataMetaModel fsopDataMetaModel) throws Exception {
        RDataMetaModel result = new RDataMetaModel();
        BeanUtils.copyProperties(result, fsopDataMetaModel);
        List<RTableMetaModel> rTableMetaModels = new ArrayList<>();
        for (FsopTableMetaModel fsopTableMetaModel : fsopDataMetaModel.getTables()) {
            RTableMetaModel rt = new RTableMetaModel();
            BeanUtils.copyProperties(rt, fsopTableMetaModel);
            List<RTableRowMetaModel> rTableRowMetaModelList = new ArrayList<>();
            rt.setRows(rTableRowMetaModelList);
            for (FsopTableRowMetaModel fsopTableRowMetaModel : fsopTableMetaModel.getRows()) {
                RTableRowMetaModel rTableRowMetaModel = new RTableRowMetaModel();
                BeanUtils.copyProperties(rTableRowMetaModel, fsopTableRowMetaModel);
                rTableRowMetaModelList.add(rTableRowMetaModel);
                List<RItemMetaModel> rItemMetaModelList = new ArrayList<>();
                rTableRowMetaModel.setRowItems(rItemMetaModelList);
                for (FsopItemMetaModel fsopItemMetaModel : fsopTableRowMetaModel.getRowItems()) {
                    RItemMetaModel rItemMetaModel = new RItemMetaModel();
                    BeanUtils.copyProperties(rItemMetaModel, fsopItemMetaModel);
                    rItemMetaModelList.add(rItemMetaModel);
                }
            }
            rTableMetaModels.add(rt);
        }
        result.setTables(rTableMetaModels);

        return result;
    }

    private ReturnHeaderMetaModel loadReturnHeader(long returnId) {

        String qlString = "SELECT " +
                " r.schedule.returnDefinition.code, " +
                " r.schedule.fi.code, " +
                " r.schedule.period.fromDate," +
                " r.schedule.period.toDate, " +
                " r.returnVersion.code, " +
                " rs.status, " +
                " rs.note, " +
                " rs.statusDate, " +
                " rs.user.login, " +
                " r.schedule.returnDefinition.returnType.code" +
                " FROM IN_RETURNS as r, IN_RETURN_STATUSES as rs" +
                " WHERE " +
                " r.id=:returnId " +
                " AND rs.id=(select MAX(irs.id) from IN_RETURN_STATUSES as irs where irs.returns.id=r.id) ";

        Object[] queryResult = em.createQuery(qlString, Object[].class).setParameter("returnId", returnId).getSingleResult();

        DateFormat df = new SimpleDateFormat(ReturnVersionControlFileTypes.SYSTEM_DATE_FORMAT);

        ReturnHeaderMetaModel model = new ReturnHeaderMetaModel();

        model.setReturnCode(queryResult[0].toString());
        model.setFiCode(queryResult[1].toString());

        if (queryResult[2] != null) {
            model.setFromDate(df.format((Date) queryResult[2]));
        }

        if (queryResult[3] != null) {
            model.setToDate(df.format((Date) queryResult[3]));
        }

        model.setVersionCode(queryResult[4].toString());

        if (queryResult[5] != null) {
            model.setStatus(((ProcessStatus) queryResult[5]).getCode());
        }

        if (queryResult[6] != null) {
            model.setStatusNote(queryResult[6].toString());
        }

        if (queryResult[7] != null) {
            model.setStatusDate(Long.toString(((Date) queryResult[7]).getTime()));
        }

        model.setStatusUser(queryResult[8].toString());

        model.setReturnTypeCode(queryResult[9].toString());

        return model;
    }

    private RepositoryFile createRepositoryFile(String path, String fileName, byte[] content) {
        RepositoryFile repositoryFile = new RepositoryFile();
        repositoryFile.setPath(path);
        repositoryFile.setFileName(fileName);
        repositoryFile.setContent(content);
        return repositoryFile;
    }

    private byte[] getReturnFile(ReturnHeaderMetaModel returnHeader, ReturnItemsStoreEvent event) {
        return ReturnXmlParser.getInstance().convert(processItemsToReturn(returnHeader, event));
    }

    private net.fina.server.returns.xml.Return processItemsToReturn(ReturnHeaderMetaModel returnHeader, ReturnItemsStoreEvent event) {
        net.fina.server.returns.xml.Return result = new net.fina.server.returns.xml.Return();

        Header header = new Header();
        header.setBankCode(returnHeader.getFiCode());
        header.setPeriodFrom(returnHeader.getFromDate());
        header.setPeriodEnd(returnHeader.getToDate());
        header.setReturnCode(returnHeader.getReturnCode());
        header.setVer(returnHeader.getVersionCode());
        result.setHeader(header);

        List<Item> items = new ArrayList<>();

        if (event.getValues() != null) {
            for (ProcessItem pItem : event.getValues()) {
                for (Map.Entry<Integer, Long> e : pItem.idByRowNumber.entrySet()) {
                    Item i = new Item();
                    i.setItemCode(pItem.code);
                    i.setRow(e.getKey());
                    i.setValue(pItem.values.get(e.getKey()));
                    items.add(i);
                }
            }
        } else if (event.getReturnItemLites() != null) {
            for (ReturnItemLite itemLite : event.getReturnItemLites()) {
                Item i = new Item();
                i.setItemCode(itemLite.getNodeCode());
                i.setRow((int) itemLite.getRowNumber());
                i.setValue(itemLite.getValue());
                items.add(i);
            }
        }

        Body body = new Body();
        body.setItems(items);

        result.setBody(body);

        return result;
    }

    private byte[] getMdtFile(ReturnItemsStoreEvent event) {
        try {
            Map<String, Boolean> activate = new HashMap<>();

            activate.put("id", true);
            activate.put("code", true);
            activate.put("parentId", true);
            activate.put("type", true);
            activate.put("dataType", true);
            activate.put("equation", true);
            activate.put("sequence", true);
            activate.put("evalMethod", true);
            activate.put("disabled", true);
            activate.put("required", true);
            activate.put("dependents", true);
            activate.put("descriptions", true);
            activate.put("comparisons", true);
            activate.put("optional", true);

            Map<Long, List<MDTComparison>> comparisons = new HashMap<>();
            List<MDTComparison> mdtComparisons = event.getComparisons();
            if (mdtComparisons == null || mdtComparisons.isEmpty()) {
                mdtComparisons = mdtNodeLocal.loadComparisons(new EnumMap<>(MDTComparisonFilter.class), null);
            }

            for (MDTComparison comparison : mdtComparisons) {
                long nid = comparison.getNode().getId();
                comparisons.putIfAbsent(nid, new ArrayList<>());
                comparisons.get(nid).add(comparison);
            }

            Map<Long, String> languagesMap = languageLocal.getLanguageIdCodeMap();

            final Map<Long, Set<String>> dependencies = new HashMap<>();

            Map<Long, List<MDTNode>> allMdtNodesByParentId = event.getAllMdtNodesByParentId();
            if (allMdtNodesByParentId == null || allMdtNodesByParentId.isEmpty()) {
                allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();
            }


            long rdStart = System.currentTimeMillis();
            Node node = definitionLocal.loadReturnDefinitionMdtV2(getReturnDefinitionByReturnId(event.getReturnId()), activate, comparisons, languagesMap, dependencies, allMdtNodesByParentId);
            log.info("loadReturnDefinitionMdtV2 took : " + (System.currentTimeMillis() - rdStart) + "ms");
            initListNodesV2(allMdtNodesByParentId, node);

            return MdtXmlParser.getInstance().convert(node);

        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }
        return new byte[0];
    }

    private void initListNodesV2(Map<Long, List<MDTNode>> allMdtNodesByParentId, Node nodeV2) {
        initListNodeV2(allMdtNodesByParentId, nodeV2);
        List<net.fina.server.mdt.xml.v2.Node> nodes = nodeV2.getChildren();
        if (nodes != null && !nodes.isEmpty()) {
            for (net.fina.server.mdt.xml.v2.Node node : nodes) {
                if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                    initListNodesV2(allMdtNodesByParentId, node);
                } else if (node.getType() == MDTNodeTypes.LIST) {
                    initListNodeV2(allMdtNodesByParentId, node);
                }
            }
        }
    }

    private void initListNodeV2(Map<Long, List<MDTNode>> allMdtNodesByParentId, Node node) {
        if (node.getType() == MDTNodeTypes.LIST) {
            int hash = String.valueOf(node.getCode()).hashCode();
            List<MDTNode> dataItemNodes = allMdtNodesByParentId.get(Long.valueOf(node.getEquation().trim()));
            StringBuilder dataItems = new StringBuilder();
            if (dataItemNodes != null) {
                for (MDTNode dataItemNode : dataItemNodes) {
                    dataItems.append(dataItemNode.getEquation().trim()).append(hash);
                }
                node.setEquation(dataItems.toString());
            }
        }
    }

    private byte[] getStatusFile(ReturnStatusStoreEvent statusStoreEvent, String returnFileId, String mdtFileId, String parentPath, ReturnHeaderMetaModel returnHeader) throws JAXBException, IOException, RepositoryException {

        ObjectFactory objectFactory = new ObjectFactory();


        ReturnStatusesType statusesType = objectFactory.createReturnStatusesType();

        InputStream statusFileInputStream = repositoryLocal.getLatestVersionFileStream(returnRepositorySession, parentPath, ReturnVersionControlFileTypes.STATUS_FILE_NAME);
        if (statusFileInputStream != null) {
            statusesType = StatusXmlParser.getInstance().convert(statusFileInputStream);
        }

        StatusType statusType = objectFactory.createStatusType();
        statusType.setId(Long.toString(statusStoreEvent.getId()));
        statusType.setReturnId(Long.toString(statusStoreEvent.getReturnId()));
        statusType.setReturnVersionId(Long.toString(statusStoreEvent.getVersionId()));
        statusType.setNote(statusStoreEvent.getNote());
        statusType.setUserId(Long.toString(statusStoreEvent.getUserId()));
        statusType.setStatusDate(Long.toString(statusStoreEvent.getStatusDate().getTime()));
        statusType.setStatus(statusStoreEvent.getStatus().getCode());

        statusesType.getStatus().add(statusType);

        FileType fileType = objectFactory.createFileType();
        fileType.setReturnFileVersionId(returnFileId);
        fileType.setMdtFileVersionId(mdtFileId);
        fileType.setReturnTemplateVersionId(repositoryLocal.getLatestVersionId(templateRepositorySession, "/" + returnHeader.getReturnTypeCode() + "/" + returnHeader.getReturnTypeCode() + ".ods"));
        statusType.setFile(fileType);

        return StatusXmlParser.getInstance().convert(statusesType);
    }

    private ReturnDefinition getReturnDefinitionByReturnId(long returnId) {
        return em.createQuery("select r.schedule.returnDefinition from IN_RETURNS r where r.id=:returnId ", ReturnDefinition.class).setParameter("returnId", returnId).getSingleResult();
    }

    private String getFileBasePath(ReturnHeaderMetaModel returnHeader) {
        return "/"
                + returnHeader.getFromDate() + "_" + returnHeader.getToDate() + "/"
                + returnHeader.getFiCode() + "/"
                + returnHeader.getVersionCode() + "/"
                + returnHeader.getReturnCode();
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
