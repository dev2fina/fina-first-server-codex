package net.fina.server.processing;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RunAs;
import jakarta.ejb.*;
import jakarta.ejb.Timer;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.filter.PeriodFilter;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ReturnModel;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelMappingReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelMatrixReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixMappingSource;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.MatrixMappingUtil;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.Type;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.matrix.api.MatrixLocal;
import net.fina.server.matrix.api.SubMatrixLocal;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.matrix.entity.SubMatrix;
import net.fina.server.matrix.entity.SubMatrixTable;
import net.fina.server.matrix.entity.SubMatrixTableMapping;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.processing.helper.ProcessingGlobalHelper;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.PeriodType;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.security.api.PropertyLocal;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;
import org.joda.time.LocalDate;

import java.util.*;

@Startup
@Singleton
@DependsOn({"SysStringCacheManager", "MDTCacheManager"})
@RunAs(PermissionIdNames.MENU_MATRIX)
public class ProcessingConfigSingleton {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private LanguageLocal languageLocal;
    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @Inject
    private PropertyLocal propertyLocal;
    @Inject
    private PeriodLocal periodLocal;
    @Inject
    private ScheduleLocal scheduleLocal;
    @Inject
    private MDTCacheManager mdtCacheManager;
    @Inject
    private ReturnLocal returnLocal;
    @Inject
    private MatrixLocal matrixLocal;
    @Inject
    private SubMatrixLocal subMatrixLocal;
    @Resource
    private TimerService timerService;
    private ProcessingGlobalHelper config;

    @PostConstruct
    public void initConfig() {
        ScheduleExpression schedule = new ScheduleExpression();
        // every day at 23:45
        schedule.hour(23);
        schedule.minute(45);

        Timer timer = timerService.createCalendarTimer(schedule, new TimerConfig(null, false));
        initProcessingData();
    }

    @Timeout
    public void automaticTimeout() {
        initProcessingData();
    }

    public void initProcessingData() {

        try (StatisticsLogger statLog = new StatisticsLogger("Load and Cache Processing Configuration Data")) {
            boolean isCrossFileValidationFeatureEnabled = isCrossFileValidationEnabled();
            if (isCrossFileValidationFeatureEnabled) {

                Language language = languageLocal.getDefaultLanguage();
                Set<String> regMdtCodes = new HashSet<>();

                Map<String, String> mdtCodeTableNameMap = new HashMap<>();
                Map<String, String> mdtCodeReturnCodeMap = new HashMap<>();

                LocalDate toDate = LocalDate.fromDateFields(new Date());

                toDate = toDate.plusMonths(2);
                LocalDate fromDate = LocalDate.fromDateFields(new Date());
                fromDate = fromDate.withDayOfMonth(1);
                fromDate = fromDate.minusYears(4);
                fromDate = fromDate.minusMonths(4);

                MatrixMappingSource matrixMappingSource = MatrixMappingUtil.getMatrixMappingSource();

                Map<PeriodFilter, Object> filter = new HashMap<>();
                filter.put(PeriodFilter.from, fromDate.toDate());
                filter.put(PeriodFilter.to, toDate.toDate());
                statLog.logStage("Load Schedules and periods");
                List<Period> currentYearPeriods = periodLocal.load(filter, true, null);
                List<PeriodType> allPeriodTypes = periodLocal.loadPeriodTypes();
                List<Schedule> currentYearSchedules = scheduleLocal.loadSchedulesByDate(fromDate.toDate(), toDate.toDate());

                List<ReturnModel> currentPeriodReturns = returnLocal.loadReturnSimple(fromDate.toDate(), toDate.toDate());

                Map<String, Object> properties = new HashMap<>();
                properties.put("dcs.language", language);
                statLog.logStage("load load All Nodes ByParent Id");
                Map<Long, List<MDTNode>> allMdtNodesByParentId = mdtNodeLocal.loadAllNodesByParentId();

                statLog.logStage("Read Matrix Data");

                switch (matrixMappingSource) {
                    case EXCEL,
                         UNKNOWN ->
                            initDataLegacy(properties, allMdtNodesByParentId, mdtCodeTableNameMap, mdtCodeReturnCodeMap, regMdtCodes, statLog);
                    case DATABASE ->
                            initData(allMdtNodesByParentId, mdtCodeTableNameMap, mdtCodeReturnCodeMap, regMdtCodes);
                }

                this.config = new ProcessingGlobalHelper(regMdtCodes, mdtCodeTableNameMap, mdtCodeReturnCodeMap, currentYearPeriods, currentYearSchedules, allPeriodTypes, currentPeriodReturns);
            } else {
                this.config = new ProcessingGlobalHelper(new HashSet<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            }

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private void initData(Map<Long, List<MDTNode>> allMdtNodesByParentId,
                          Map<String, String> mdtCodeTableNameMap,
                          Map<String, String> mdtCodeReturnCodeMap,
                          Set<String> regMdtCodes) {
        List<Matrix> mainMatrixList = matrixLocal.load();

        for (Matrix mainMatrix : mainMatrixList) {

            ProcessEngine processEngine = mainMatrix.getProcessEngine();
            //TODO
            if (processEngine.equals(ProcessEngine.REG_ADVANCED)) {
                //ignore
                continue;
            }

            List<SubMatrix> subMatrixList = subMatrixLocal.loadSubMatrix(mainMatrix.getId());

            for (SubMatrix subMatrix : subMatrixList) {
                for (SubMatrixTable table : subMatrix.getTables()) {
                    List<MDTNode> childNodes = new ArrayList<>();

                    if (table.getTableMappings().isEmpty()) {
                        log.warn(table.getDefinitionTable().getCode() + " has no mappings defined...");
                        continue;
                    }
                    String firstNodeCode = table.getTableMappings().getFirst().getMdtNode().getCode();
                    MDTNode curNode = mdtCacheManager.getNode(firstNodeCode);
                    if (curNode == null) {
                        log.warn("Invalid Node Code : " + firstNodeCode);
                        continue;
                    }
                    MDTNode parentNode = mdtCacheManager.getNode(curNode.getParentId());

                    if (table.getDefinitionTable().getType().equals(ReturnTableType.MCT)) {
                        for (SubMatrixTableMapping mapping : table.getTableMappings()) {
                            long parentId = mapping.getMdtNode().getParentId();
                            childNodes.addAll(allMdtNodesByParentId.get(parentId));
                        }
                    } else {
                        childNodes = allMdtNodesByParentId.get(parentNode.getId());
                    }


                    String returnDefCode = subMatrix.getReturnDefinition().getCode();
                    childNodes.forEach(c -> {
                        mdtCodeTableNameMap.put(c.getCode(), parentNode.getCode().trim());
                        mdtCodeReturnCodeMap.put(c.getCode(), returnDefCode);
                    });

                    if (processEngine.equals(ProcessEngine.REG)) {
                        regMdtCodes.addAll(table.getTableMappings().stream().map(tm -> tm.getMdtNode().getCode()).toList());
                    }

                }
            }

        }

    }

    private void initDataLegacy(Map<String, Object> properties,
                                Map<Long, List<MDTNode>> allMdtNodesByParentId,
                                Map<String, String> mdtCodeTableNameMap,
                                Map<String, String> mdtCodeReturnCodeMap,
                                Set<String> regMdtCodes,
                                StatisticsLogger statLog) {

        String matrixPath = getMatrixPath();
        List<MatrixOptionBase> options = getMatrixOptions(matrixPath);
        boolean isValidSubMatrix = validateSubMatrixOptions(options);
        if (!isValidSubMatrix) {
            statLog.logMessage("Sub Matrix Options is not configured, skipping processing configuration cache build...");
            this.config = new ProcessingGlobalHelper(regMdtCodes, mdtCodeTableNameMap, mdtCodeReturnCodeMap, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            return;
        }


        for (MatrixOptionBase option : options) {
            try {
                if (option.getMatrixForEachType() == null || option.getMatrixForEachType().isBlank()) {
                    log.warn("Sub Matrix Option is not configured in main Matrix");
                    continue;
                }
                properties.put("dcs.primary.matrix", matrixPath + option.getMatrixForEachType());

                String PRIMARY = properties.get("dcs.primary.matrix").toString();
                ExcelMappingReader pmr = new ExcelMappingReader(PRIMARY, properties);
                List<ExcelMappingReader.Option> primaryOptions = pmr.getOptions();
                for (ExcelMappingReader.Option subMatrixOption : primaryOptions) {

                    ProcessEngine processEngine = option.getProcessEngine();
                    //TODO
                    if (processEngine.equals(ProcessEngine.REG_ADVANCED)) {
                        //ignore
                        continue;
                    }

                    List<MDTNode> childNodes = new ArrayList<>();

                    Map<String, String> mdtCodeCellReferenceMap = subMatrixOption.getMdtCodeCellReferenceMap();

                    String firstNodeCode = mdtCodeCellReferenceMap.keySet().stream().findFirst().orElse("").trim();
                    if (firstNodeCode.trim().isEmpty()) {
                        continue;
                    }
                    MDTNode curNode = mdtCacheManager.getNode(firstNodeCode);
                    if (curNode == null) {
                        log.warn("Invalid Node Code : " + firstNodeCode);
                        continue;
                    }
                    MDTNode parentNode = mdtCacheManager.getNode(curNode.getParentId());

                    if (subMatrixOption.getType().equals(Type.MCT)) {
                        for (String mdtCode : mdtCodeCellReferenceMap.keySet()) {
                            long parentId = mdtCacheManager.getNode(mdtCode.trim()).getParentId();
                            childNodes.addAll(allMdtNodesByParentId.get(parentId));
                        }
                    } else {
                        childNodes = allMdtNodesByParentId.get(parentNode.getId());
                    }


                    String returnDefCode = subMatrixOption.getReturnCode();
                    childNodes.forEach(c -> {
                        mdtCodeTableNameMap.put(c.getCode(), parentNode.getCode().trim());
                        mdtCodeReturnCodeMap.put(c.getCode(), returnDefCode);
                    });

                    if (processEngine.equals(ProcessEngine.REG)) {
                        regMdtCodes.addAll(mdtCodeCellReferenceMap.keySet());
                    }
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }

        }
    }


    private boolean validateSubMatrixOptions(List<MatrixOptionBase> options) {
        if (options == null || options.isEmpty()) {
            return false;
        }

        for (MatrixOptionBase option : options) {
            if (option.getMatrixForEachType() != null && !option.getMatrixForEachType().isBlank()) {
                return true;
            }
        }

        return false;
    }

    private String getMatrixPath() throws ConverterDcsTypeException {
        String matrixPath = propertyLocal.getSystemProperty(PropertyKeys.MATRIX_PATH);
        char separator = (matrixPath.contains("/") ? '/' : '\\');
        if (matrixPath.charAt(matrixPath.length() - 1) != separator) {
            matrixPath += separator;
        }
        return matrixPath;
    }

    private List<MatrixOptionBase> getMatrixOptions(String matrixPath) throws ConverterDcsTypeException {
        ExcelMatrixReader excelMatrixReader = new ExcelMatrixReader(matrixPath + "Matrix.xls", null);
        return excelMatrixReader.getOptions();
    }

    private boolean isCrossFileValidationEnabled() {
        try {
            String value = propertyLocal.getSystemProperty(PropertyKeys.PROCESSING_CROSS_FILE_VALIDATION_ENABLED);
            if (value != null && !value.isBlank()) {
                return Boolean.parseBoolean(value);
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return false;
    }


    public ProcessingGlobalHelper getConfig() {
        return config;
    }
}
