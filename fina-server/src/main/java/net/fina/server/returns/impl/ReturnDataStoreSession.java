package net.fina.server.returns.impl;


import freemarker.template.Template;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.xml.bind.ValidationEventHandler;
import net.fina.common.client.exception.FinAAccessDeniedException;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.messages.MessagesUtil;
import net.fina.security.auth.CustomPrincipal;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fsop.FsopXmlParser;
import net.fina.server.fsop.ImportedReturnValidationEventHandler;
import net.fina.server.fsop.api.FsopTemplateLocal;
import net.fina.server.fsop.impl.FsopReturnTemplateProcessor;
import net.fina.server.fsop.impl.FsopTemplateException;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.api.ReturnDataStoreLocal;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.returns.entity.Return;
import net.fina.server.returns.entity.ReturnVersion;
import net.fina.server.returns.model.*;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;

import java.io.StringWriter;
import java.util.*;

@Stateless
@Local(ReturnDataStoreLocal.class)
@Interceptors(RecordingAuditor.class)
public class ReturnDataStoreSession implements ReturnDataStoreLocal {

    private Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @EJB
    private ProcessingStoreLocal processingStoreLocal;
    @EJB
    private FsopTemplateLocal fsopTemplateLocal;
    @EJB
    private UserLocal userLocal;
    @EJB
    private FiLocal fiLocal;
    @EJB
    private FileContentManagementSession fileSession;

    @Override
    public RDataMetaModel loadReturnData(long returnId, long versionId, long langId) {
        return loadReturnData(returnId, versionId, langId, false);
    }

    @Override
    public RDataMetaModel loadReturnData(long returnId, long versionId, long langId, boolean review) {
        return loadReturnData(returnId, versionId, langId, review, null);
    }

    @Override
    public RDataMetaModel loadReturnData(long returnId, long versionId, long langId, boolean review, Map<Long, ProcessItem> returnItems) {
        this.checkReturnPermission(returnId);

        RDataMetaModel rdm = null;

        try (StatisticsLogger statLog = new StatisticsLogger(String.format("Get Return Data. R id:%s, V Id:%s, Lang Id:%s", returnId, versionId, langId))) {

            statLog.logMessage("Start");

            statLog.logStage("Load Return Header");
            rdm = getReturnHeaderData(returnId, versionId, langId);

            statLog.logStage("Check return readonly");
            Object canAmend = userLocal.getCallerPrincipal().getReturnVersions().get(versionId);
            if (canAmend == null || (!(boolean) canAmend)) {
                rdm.setReadonly(true);
            } else {
                rdm.setReadonly(rdm.getStatus() == ProcessStatus.STATUS_ACCEPTED);
            }

            List<MDTDependentNode> mdtDependentNodes = null;
            if (!review) {
                statLog.logStage("Get all mdt dependent nodes");
                mdtDependentNodes = processingStoreLocal.loadAllMdtDependentNodes();
            }

            statLog.logStage("Get mdt nodes by parent id");
            final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();

            statLog.logStage("Get return Definition dependencies");
            final Collection<Long> definitionDependencies = processingStoreLocal.getReturnDefinitionDependencies(rdm.getDefinitionId());

            ReturnTableProcessorBase tableProcessor = new ReturnTableProcessor(allMdtNodesByParentId);

            if (returnItems == null) {
                statLog.logStage("Load return values");
                returnItems = processingStoreLocal.loadReturnNodesValue(Collections.singletonList(returnId), versionId, mdtDependentNodes, langId, allMdtNodesByParentId);
            }

            if (!review) {
                List<RItemMetaModel> dependencies = getDependencies(statLog, rdm, definitionDependencies, mdtDependentNodes, langId, allMdtNodesByParentId, tableProcessor);
                rdm.setDependencies(dependencies);

                statLog.logStage("Compile dependencies");
                Set<Long> nodeIds = new HashSet<>(returnItems.keySet());
                for (RItemMetaModel item : dependencies) {
                    nodeIds.add(item.getNodeId());
                }
                List<MDependentMetaModel> dependentMetaModels = getMdtNodeDependencies(mdtDependentNodes, nodeIds);
                rdm.setDependentNodes(dependentMetaModels);

                statLog.logStage("Get Comparisons.");
                List<MComparisonMetaModel> comparisons = getComparisons(returnItems.keySet());
                rdm.setComparisons(comparisons);
            } else {
                //Set review empty data
                rdm.setDependencies(new ArrayList<>());
                rdm.setDependentNodes(new ArrayList<>());
                rdm.setComparisons(new ArrayList<>());
            }

            statLog.logStage("Load return definition tables");
            final List<DefinitionTable> definitionTables = getDefinitionTables(rdm.getDefinitionId());

            statLog.logStage("Compile table");
            rdm.setTables(getTables(tableProcessor, definitionTables, returnItems, langId, rdm));

        }

        return rdm;
    }

    private RDataMetaModel getReturnHeaderData(long returnId, long versionId, long langId) {

        List<Object[]> returnHeaderData = em.createQuery(
                        "select " +
                                "r.schedule.returnDefinition.id," +                 //0
                                "r.schedule.returnDefinition.code," +               //1
                                "r.schedule.returnDefinition.description, " +       //2
                                "r.schedule.returnDefinition.returnType.code, " +   //3
                                "r.schedule.period.id," +                           //4
                                "r.schedule.period.fromDate, " +                    //5
                                "r.schedule.period.toDate," +                       //6
                                "r.schedule.period.periodType.code, " +             //7
                                "r.schedule.period.periodType.description," +       //8
                                "r.returnVersion.code, " +                          //9
                                "r.returnVersion.description, " +                   //10
                                "r.schedule.fi.id," +                               //11
                                "r.schedule.fi.code," +                             //12
                                "r.schedule.fi.description," +                      //13
                                "rs.status " +                                      //14
                                "from IN_RETURNS r,IN_RETURN_STATUSES rs where  " +
                                "rs.id=(select MAX(irs.id) from IN_RETURN_STATUSES as irs where irs.returns.id=r.id) and r.id=:returnId and r.returnVersion.id=:versionId", Object[].class)
                .setParameter("returnId", returnId)
                .setParameter("versionId", versionId)
                .getResultList();

        assert returnHeaderData.isEmpty() : String.format("Return not found. Return id:%s, Version id:%s", returnId, versionId);

        assert returnHeaderData.size() == 1 : String.format("Too many result selected. Return id:%s, Version id:%s. Result size:%s", returnId, versionId, returnHeaderData.size());

        RDataMetaModel rdmm = new RDataMetaModel();
        rdmm.setReturnId(returnId);
        rdmm.setVersionId(versionId);

        Object[] data = returnHeaderData.stream().findFirst().get();

        rdmm.setDefinitionId((long) data[0]);
        rdmm.setReturnCode(getStringFromResult(data, 1));
        rdmm.setReturnDescription(getDescriptionStringFromResult(data, 2, langId));
        rdmm.setReturnTypeCode(getStringFromResult(data, 3));

        rdmm.setPeriodId((Long) data[4]);
        rdmm.setFromDate((Date) data[5]);
        rdmm.setToDate((Date) data[6]);
        rdmm.setPeriodTypeCode(getStringFromResult(data, 7));
        rdmm.setPeriodTypeDescription(getDescriptionStringFromResult(data, 8, langId));

        rdmm.setVersionCode(getStringFromResult(data, 9));
        rdmm.setVersionDescription(getDescriptionStringFromResult(data, 10, langId));

        rdmm.setFiId((Long) data[11]);
        rdmm.setFiCode(getStringFromResult(data, 12));
        rdmm.setFiDescription(getDescriptionStringFromResult(data, 13, langId));

        Language language = em.find(Language.class, langId);

        Map<String, String> messages = MessagesUtil.loadMessageBundleMap(language.getCode().trim());

        rdmm.setStatus((ProcessStatus) data[14]);

        if (messages != null) {
            rdmm.setStatusName(messages.get(rdmm.getStatus().getCode()));
        }

        User currentUser = userLocal.getCurrentUser();
        rdmm.setUserLogin(currentUser.getLogin());
        rdmm.setUserName(currentUser.getDescription().getDescription(langId));

        return rdmm;
    }

    private String getStringFromResult(Object[] data, int index) {
        return data[index] != null ? data[index].toString() : "";
    }

    private String getDescriptionStringFromResult(Object[] data, int index, long langId) {
        return data[index] != null ? ((Description) data[index]).getDescription(langId) : "";
    }

    private List<DefinitionTable> getDefinitionTables(long definitionId) {
        return em.createQuery("select dt from IN_RETURN_DEFINITIONS rd, IN(rd.definitionTables) dt where rd.id=:definitionId order by dt.sequence", DefinitionTable.class)
                .setParameter("definitionId", definitionId)
                .getResultList();
    }

    @LogDescription(logMethodParameters = false)
    private List<MDependentMetaModel> getMdtNodeDependencies(List<MDTDependentNode> allMdtDependentNodes, Set<Long> nodeIds) {
        List<MDependentMetaModel> result = new ArrayList<>();
        for (MDTDependentNode mdtDependentNode : allMdtDependentNodes) {
            if (nodeIds.contains(mdtDependentNode.getDepNode().getNodeId()) || nodeIds.contains(mdtDependentNode.getDepNode().getDependentNodeId()))
                result.add(new MDependentMetaModel().setMDTDependentNode(mdtDependentNode));
        }
        return result;
    }

    @LogDescription(logMethodParameters = false)
    private List<MComparisonMetaModel> getComparisons(Collection<Long> nodeIds) {
        List<MComparisonMetaModel> result = new ArrayList<>();

        Map<Long, List<ComparisonItem>> comparisons = processingStoreLocal.loadComparisons();

        for (long nodeId : nodeIds) {
            List<ComparisonItem> comparisonItems = comparisons.get(nodeId);
            if (comparisonItems != null) {
                for (ComparisonItem c : comparisonItems) {
                    result.add(new MComparisonMetaModel().setComparisonItem(c));
                }
            }
        }
        return result;
    }

    @LogDescription(logMethodParameters = false)
    private List<RItemMetaModel> getDependencies(StatisticsLogger statLog, RDataMetaModel rdm, Collection<Long> definitionDependencies, List<MDTDependentNode> mdtDependentNodes, long langId, Map<Long, List<MDTNode>> allMdtNodesByParentId, ReturnTableProcessorBase tableProcessor) {
        List<RItemMetaModel> result = new ArrayList<>();

        if (!definitionDependencies.isEmpty()) {
            statLog.logStage("Load Depended returns");
            List<Long> dependedReturnIds = processingStoreLocal.loadDependentReturns(rdm.getFiId(), rdm.getPeriodId(), definitionDependencies, rdm.getVersionId());
            if (!dependedReturnIds.isEmpty()) {
                statLog.logStage("Load depended Returns");
                Map<Long, ProcessItem> dependencies = processingStoreLocal.loadReturnNodesValue(dependedReturnIds, rdm.getVersionId(), mdtDependentNodes, langId, allMdtNodesByParentId);

                for (ProcessItem pItem : dependencies.values()) {
                    result.addAll(tableProcessor.processItemToRItemMetaModel(pItem));

                }
            }
        }
        return result;
    }

    private List<RTableMetaModel> getTables(ReturnTableProcessorBase tableProcessor, List<DefinitionTable> definitionTables, Map<Long, ProcessItem> returnItems, long langId, RDataMetaModel dataMetaModel) {
        List<RTableMetaModel> result = new ArrayList<>();
        for (DefinitionTable definitionTable : definitionTables) {
            result.add(tableProcessor.process(definitionTable, returnItems, langId, dataMetaModel));
        }

        //Sort tables
        Collections.sort(result);

        return result;
    }


    @Override
    public RDataMetaModel loadReturnDataFromXml(ImportedReturn importedReturn, ValidationEventHandler handler, long langId) {
        RDataMetaModel rdm = new RDataMetaModel();
        try (StatisticsLogger statLog = new StatisticsLogger(String.format("Get Return Data from XML. xml id:%s, return code:%s, fi code:%s, period:%s - %s", importedReturn.getId(), importedReturn.getReturnCode(), importedReturn.getBankCode(), importedReturn.getPeriodEnd().toString(), importedReturn.getPeriodEnd().toString()))) {

            statLog.logMessage("Start");


            statLog.logStage("Load Fi by code");
            Fi fi = findFiByCode(importedReturn.getBankCode());
            if (fi != null) {
                assert userLocal.getCallerPrincipal().getFis().contains(fi.getId()) : "User doesn't have permission. xml id:" + importedReturn.getId();

                statLog.logStage("Init return table data model");
                rdm.setFiCode(importedReturn.getBankCode());
                rdm.setFiDescription(fi.getDescription().getDescription(langId));
                rdm.setReturnCode(importedReturn.getReturnCode());
                rdm.setFromDate(importedReturn.getPeriodStart());
                rdm.setToDate(importedReturn.getPeriodEnd());

                statLog.logStage("Get all mdt dependent nodes");
                List<MDTDependentNode> mdtDependentNodes = processingStoreLocal.loadAllMdtDependentNodes();

                statLog.logStage("Get mdt nodes by parent id");
                final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();
                ReturnTableProcessorBase tableProcessor = new ReturnTableProcessor(allMdtNodesByParentId);

                statLog.logStage("Get definition tables");
                List<DefinitionTable> definitionTables = getDefinitionTablesByReturnCode(importedReturn.getReturnCode());

                statLog.logStage("Get return template");
                Return ret = new Return();
                ret.setId(-1);
                ReturnVersion rv = new ReturnVersion();
                rv.setId(-1);
                ret.setReturnVersion(rv);
                List<ProcessItem> processItems = fsopTemplateLocal.getReturnTemplate(ret, allMdtNodesByParentId, langId, mdtDependentNodes, definitionTables);

                statLog.logStage("Process template");
                net.fina.server.returns.xml.Return xmlReturn = FsopXmlParser.getInstance().convert(fileSession.loadImportedReturnContent(importedReturn), handler);
                FsopReturnTemplateProcessor processor = new FsopReturnTemplateProcessor(new FsopImportedReturnMetaModel().setImportedReturn(importedReturn), xmlReturn, processItems);
                Map<Long, ProcessItem> returnItems = processor.process();

                statLog.logStage("Process Tables");
                rdm.setTables(getTables(tableProcessor, definitionTables, returnItems, langId, rdm));
            }
        }

        return rdm;
    }

    private List<DefinitionTable> getDefinitionTablesByReturnCode(String returnCode) {
        assert returnCode != null : "Return code is null";

        return em.createQuery("select dt from IN_RETURN_DEFINITIONS rd, IN(rd.definitionTables) dt where trim(rd.code)=:returnCode", DefinitionTable.class)
                .setParameter("returnCode", returnCode.trim())
                .getResultList();
    }

    private Fi findFiByCode(String fiCode) {
        assert fiCode != null : "FI code is null";
        List<Fi> queryResult = em.createQuery("select new " + Fi.class.getName() + "( b.id,b.code, b.description ) from IN_BANKS b where trim(b.code)=:fiCode", Fi.class)
                .setParameter("fiCode", fiCode.trim())
                .getResultList();
        Iterator<Fi> iter = queryResult.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public byte[] loadReturnDataView(int importedReturnId, long langId) {
        try {
            Language language = em.find(Language.class, langId);
            ImportedReturn importedReturn = findImportedReturnById(importedReturnId);
            ImportedReturnValidationEventHandler handler = new ImportedReturnValidationEventHandler();
            RDataMetaModel rdm = loadReturnDataFromXml(importedReturn, handler, langId);

            if (handler.getMessages().isEmpty()) {
                Template template = ReturnFreeMarkerTemplate.getInstance().getTemplate();
                template.setNumberFormat(language.getNumberFormat());

                // Create the root hash
                Map<String, Object> root = new HashMap<>();
                root.put("return", rdm);
                root.put("title", "FSOP " + importedReturn.getReturnCode() + " view");

                StringWriter stringWriter = new StringWriter();
                template.process(root, stringWriter);
                return stringWriter.toString().getBytes("UTF-8");
            } else {
                return handler.getMessages().toString().getBytes("UTF-8");
            }
        } catch (FsopTemplateException e) {
            return e.getMessage().getBytes();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return new byte[0];
    }

    private ImportedReturn findImportedReturnById(int importedReturnId) {
        List<String> fiCodes = fiLocal.loadFiCodes();

        List<ImportedReturn> importedReturns = em.createQuery("select ir from IN_IMPORTED_RETURNS ir where ir.bankCode in(:bankCodes) and ir.id=:id", ImportedReturn.class)
                .setParameter("bankCodes", fiCodes)
                .setParameter("id", importedReturnId)
                .getResultList();

        if (importedReturns.size() == 1) {
            return importedReturns.get(0);
        }

        return null;
    }

    @Override
    public void checkReturnPermission(long returnId) {
        CustomPrincipal principal = userLocal.getCallerPrincipal();
        List<Long> result = em.createQuery("select r.schedule.fi.id from IN_RETURNS r where r.id=:returnId ", Long.class)
                .setParameter("returnId", returnId)
                .getResultList();
        if (result.isEmpty() || !principal.getFis().contains(result.get(0))) {
            throw new FinAAccessDeniedException();
        }
    }
}
