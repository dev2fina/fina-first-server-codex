package net.fina.server.returns.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReturnDefinitionFilter;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.DefinitionTableModel;
import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.common.client.returns.ReturnDefinitionModel;
import net.fina.common.shared.ContentModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.ReturnReviewEngineType;
import net.fina.common.shared.reg.GenerateSourceResult;
import net.fina.common.shared.reg.GenerateSourceType;
import net.fina.odstoolkit.AbstractFactory;
import net.fina.odstoolkit.FactoryProducer;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.aoo.AOOServiceManager;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.ProcessingConfigSingleton;
import net.fina.server.reg.api.RegFileLocal;
import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.util.RegUtil;
import net.fina.server.reports.util.ReportPrintUtil;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnDefinitionFormat;
import net.fina.server.returns.entity.ReturnType;
import net.fina.server.returns.impl.ReturnCacheManager;
import net.fina.server.returns.impl.ReturnSheetManager;
import net.fina.server.returns.model.helper.DefinitionTableModelHelper;
import net.fina.server.returns.model.helper.ReturnDefinitionModelHelper;
import net.fina.server.returns.util.ReturnFormatTemplatePOIHelper;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.odftoolkit.simple.table.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class ReturnDefinitionProxySession {

    @Inject
    private ReturnDefinitionLocal definitionLocal;
    @Inject
    private RegFileLocal regFileLocal;
    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @EJB
    private ReturnCacheManager returnCacheManager;
    @Inject
    private LanguageLocal languageLocal;

    @EJB
    private AOOServiceManager aooServiceManager;
    @Inject
    private ProcessingConfigSingleton processingConfigSingleton;


    public Object loadReturnDefinitions(Map<ReturnDefinitionFilter, Object> filterObjectMap) {
        List<ReturnDefinitionModel> models = new ArrayList<>();

        long langId = ThreadLocalHolder.getLanguage().getId();

        models = ReturnDefinitionModelHelper.toModels(definitionLocal.load(filterObjectMap), langId);

        if (filterObjectMap.get(ReturnDefinitionFilter.PAGE_OFFSET) != null && filterObjectMap.get(ReturnDefinitionFilter.PAGE_LIMIT) != null) {
            int offset = (int) filterObjectMap.get(ReturnDefinitionFilter.PAGE_OFFSET);
            int count = definitionLocal.count(filterObjectMap);
            return new PaginatedListWrapper<>(models, offset, count);
        }

        return models;
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_DEFINITION_AMEND)
    public ReturnDefinitionModel save(ReturnDefinitionModel model) throws FinATypeException {
        if (model.getTables().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }

        long langId = ThreadLocalHolder.getLanguage().getId();

        ReturnDefinition returnDefinition = ReturnDefinitionModelHelper.toEntity(model, langId);

        // Definition Table
        List<DefinitionTable> tableList = null;

        tableList = new ArrayList<>();

        for (DefinitionTableModel tableModel : model.getTables()) {
            DefinitionTable table = DefinitionTableModelHelper.toEntity(tableModel, langId);
            table.setReturnDefinition(new ReturnDefinition(model.getId()));
            tableList.add(table);
        }

        returnDefinition.setDefinitionTables(tableList);

        returnDefinition = definitionLocal.save(returnDefinition);

        return ReturnDefinitionModelHelper.toModel(returnDefinition, langId);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_DEFINITION_DELETE)
    public void delete(List<ReturnDefinitionModel> models) throws FinATypeException {
        if (models == null) throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        definitionLocal.delete(models.stream().map(rd -> new ReturnDefinition(rd.getId(), rd.getCode())).toList());
    }

    public List<DefinitionTableModel> loadDefinitionTables(long definitionId) {
        return DefinitionTableModelHelper.toModels(definitionLocal.loadDefinitontables(definitionId), ThreadLocalHolder.getLanguage().getId());
    }

    public GenerateSourceResult generateTableScript(List<Long> definitionIds) {
        List<DefinitionTable> definitionTables = new ArrayList<>();
        for (Long id : definitionIds) {
            List<DefinitionTable> tables = definitionLocal.loadDefinitontables(id);
            if (!tables.isEmpty()) {
                definitionTables.add(tables.get(0));
            }
        }
        List<InputsMetaModel> inputsMetaModels = new ArrayList<>();
        for (DefinitionTable table : definitionTables) {
            inputsMetaModels.add(getInputs(table.getNode(), table.getCode()));
        }

        return regFileLocal.generateTable(GenerateSourceType.TABLE, inputsMetaModels);
    }

    private InputsMetaModel getInputs(MDTNode mdtNode, String tableCode) {
        InputsMetaModel inputsMetaModel = new InputsMetaModel();
        inputsMetaModel.setTableName(mdtNode.getCode());
        inputsMetaModel.setSheetName(tableCode);
        inputsMetaModel.setReturnCode(tableCode);

        List<InputMetaModel> inputMetaModels = new ArrayList<>();

        for (MDTNode n : mdtNodeLocal.loadNodesSimple(mdtNode.getId())) {
            InputMetaModel inputMetaModel = new InputMetaModel();
            inputMetaModel.setColumn(n.getCode().trim());
            inputMetaModel.setCode(n.getCode());
            inputMetaModel.setTypeEnum(RegUtil.getTypeEnum(n.getDataType()));
            inputMetaModel.setType(inputMetaModel.getTypeEnum().getJavaType());
            inputMetaModel.setKey(n.isKey());

            inputMetaModels.add(inputMetaModel);
        }

        inputsMetaModel.setInputs(inputMetaModels);

        return inputsMetaModel;
    }

    public void restartDefinitionCache() {
        returnCacheManager.stop();
        returnCacheManager.start();
        processingConfigSingleton.initProcessingData();
    }

    public void reorderDefinitionTables(long definitionId, List<Long> sortedTableIds) {
        definitionLocal.reorderDefinitionTables(definitionId, sortedTableIds);
    }

    public ContentModel getReturnDefinitionTemplate(List<Long> rdIds, String fileType, ReturnReviewEngineType reviewEngineType, String contentPath) throws Exception {
        if (reviewEngineType.equals(ReturnReviewEngineType.AOO)) {
            return loadAooTemplate(rdIds, fileType, contentPath);
        } else {
            return loadPOITemplate(rdIds, fileType, contentPath);
        }

    }

    private ContentModel loadPOITemplate(List<Long> rdIds, String fileType, String contentPath) throws Exception {

        ReturnType type = definitionLocal.getReturnDefinitionById(rdIds.get(0)).getReturnType();
        List<ReturnDefinition> definitions = definitionLocal.loadReturnDefinitionsById(rdIds);

        for (ReturnDefinition rd : definitions) {
            for (DefinitionTable dt : rd.getDefinitionTables()) {
                dt.setNode(loadSubTreeByRootId(dt.getNode().getId()));
            }
        }

        byte[] content = new ReturnFormatTemplatePOIHelper(definitions, type.getFormat(), ThreadLocalHolder.getLanguage().getId()).executeReturnTemplateProcessor();
        String fileName = "MDT_Structure_" + type.getCode();

        if (fileType.equalsIgnoreCase("html")) {
            return ReportPrintUtil.printHtml(aooServiceManager.getOfficeManager(), content, fileName, contentPath);
        } else {
            return ReportPrintUtil.print(aooServiceManager.getOfficeManager(), content, fileType, fileName, contentPath);
        }

    }

    private ContentModel loadAooTemplate(List<Long> rdIds, String fileType, String contentPath) throws Exception {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Language language = languageLocal.getLanguageById(langId);
        byte[] content = null;
        String fileName = "";
        String curDefCode;
        AooWriterBase workBookOdfToolkitReader = null;
        if (rdIds != null && !rdIds.isEmpty()) {
            for (Long id : rdIds) {
                List<DefinitionTable> table = definitionLocal.loadDefinitontables(id);
                List<DefinitionTable> tables = new ArrayList<>();
                for (DefinitionTable t : table) {
                    t.setNode(loadSubTreeByRootId(t.getNode().getId()));
                    tables.add(t);
                }
                List<ReturnDefinition> rds = definitionLocal.loadReturnDefinitionsById(Collections.singletonList(id));
                List<String> header = new ArrayList<>();
                for (ReturnDefinition rd : rds) {
                    header.add(rd.getCode());
                    header.add(rd.getDescription().getDescription(langId));
                }
                curDefCode = (header.isEmpty() ? "" : header.get(0));
                ProcessReturnInfo returnInfo = new ProcessReturnInfo();
                returnInfo.setLangId(langId);
                returnInfo.setNumberFormat(language.getNumberFormat().trim());
                returnInfo.setDateFormat(language.getDateFormat().trim());
                returnInfo.setDateTimeFormat(language.getDateTimeFormat().trim());
                ReturnDefinitionFormat format = definitionLocal.getReturnDefinitionFormat(id);
                AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");
                AooWriterBase aooReader = factory.getAooWriter(format.getFormat());
                ReturnSheetManager manager = new ReturnSheetManager(aooReader, tables, header, returnInfo, langId);
                manager.executeForTemplate();
                if (rdIds.size() > 1) {
                    if (workBookOdfToolkitReader == null) {
                        workBookOdfToolkitReader = factory.getAooWriter();
                        workBookOdfToolkitReader.init(workBookOdfToolkitReader.createEmptySpreadsheetDocument());
                        workBookOdfToolkitReader.removeSheetByIndex(0);
                        fileName = "MDT_Structure_" + rds.get(0).getReturnType().getCode();
                    }
                    workBookOdfToolkitReader.appendSheet(aooReader.getCurrentSheet(), header.get(0));
                } else {
                    fileName = "MDT_Structure_" + rds.get(0).getCode();
                    ((Table) aooReader.getCurrentSheet()).setTableName(curDefCode);
                    aooReader.removeExtraSheets(Collections.singletonList(curDefCode));
                    content = aooReader.getSpreadsheetDocument();
                }
            }
            if (rdIds.size() > 1 && workBookOdfToolkitReader != null) {
                content = workBookOdfToolkitReader.getSpreadsheetDocument();
            }
            if (fileType.equalsIgnoreCase("html")) {
                return ReportPrintUtil.printHtml(aooServiceManager.getOfficeManager(), content, fileName, contentPath);
            } else {
                return ReportPrintUtil.print(aooServiceManager.getOfficeManager(), content, fileType, fileName, contentPath);
            }
        }
        return null;
    }

    private MDTNode loadSubTreeByRootId(long id) {
        MDTNode node = mdtNodeLocal.loadNodeSimple(id);
        loadMDTTree(node);
        return node;
    }

    private void loadMDTTree(MDTNode node) {
        node.setChildren(mdtNodeLocal.loadNodesSimple(node.getId()));
        for (MDTNode child : node.getChildren()) {
            if (child.getType() == MDTNodeTypes.NODE) {
                loadMDTTree(child);
            }
        }
    }
}
