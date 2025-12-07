package net.fina.server.processing.impl;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.shared.mdt.ProcessStage;
import net.fina.server.i18n.entity.Language;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.processing.ErrorHandler;
import net.fina.server.processing.ListElementUtil;
import net.fina.server.processing.ProcessingConfigSingleton;
import net.fina.server.processing.ProcessingUtil;
import net.fina.server.processing.api.FinaCrossFileValidator;
import net.fina.server.processing.helper.ProcessingGlobalHelper;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;
import net.fina.server.processing.script.ScriptEngineBase;
import net.fina.server.processing.script.ScriptEngineFactory;
import net.fina.server.processing.script.js.JSTreeGlobal;
import net.fina.server.returns.entity.Return;
import net.fina.server.util.FinaDS;
import net.fina.server.util.RegDS;
import org.jboss.logging.Logger;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;

@Stateless
@TransactionAttribute(jakarta.ejb.TransactionAttributeType.NOT_SUPPORTED)
public class FinaCrossFileValidatorSession implements FinaCrossFileValidator {
    private final Logger log = Logger.getLogger(getClass().getName());
    @Inject
    private ProcessingConfigSingleton processingConfigSingleton;
    @Inject
    @FinaDS
    private EntityManager finaEm;
    @Inject
    @RegDS
    private EntityManager regEm;
    @Inject
    private MDTCacheManager mdtCacheManager;

    @Override
    public Map<Long, ProcessResult> validatePostProcessComparisons(Collection<Return> processedReturns, Map<Long, List<ComparisonItem>> comparisons, Collection<ProcessItem> processItems, Language language) {
        Map<Long, ProcessResult> crossFileValidationResult = new HashMap<>();
        ProcessingGlobalHelper processingGlobalHelper = processingConfigSingleton.getConfig();
        Function<String, MDTNode> getMdtNodeByCode = nodeCode -> mdtCacheManager.getNode(nodeCode);

        Map<Long, List<ProcessItem>> returnProcessItemsMap = new HashMap<>();

        for (ProcessItem processItem : processItems) {
            if (returnProcessItemsMap.get(processItem.returnId) == null) {
                returnProcessItemsMap.put(processItem.returnId, new ArrayList<>());
            }
            returnProcessItemsMap.get(processItem.returnId).add(processItem);
        }

        for (Return r : processedReturns) {
            ProcessingErrorHandler processingErrorHandler = new ProcessingErrorHandler(new ProcessReturnModel(r.getId()), language.getCode());
            ProcessingUtil processingUtil = new ProcessingUtil(processingErrorHandler, language.getNumberFormat().trim(), language.getDateFormat().trim(), language.getDateTimeFormat().trim(), language.getId());


            for (ProcessItem item : returnProcessItemsMap.getOrDefault(r.getId(), new ArrayList<>())) {

                List<ComparisonItem> nodeComparisons = comparisons.get(item.nodeId);
                if (nodeComparisons == null) {
                    continue;
                }
                nodeComparisons = nodeComparisons.stream().filter(c -> ProcessStage.POST_PROCESS.equals(c.processStage)).toList();

                for (ComparisonItem comp : nodeComparisons) {

                    Object compValue = ScriptEngineFactory.get().callObject(new JSTreeGlobal(finaEm, regEm, processingGlobalHelper, getMdtNodeByCode, r.getSchedule().getId(), r.getReturnVersion().getId()), ScriptEngineBase.createFunction(comp.equation));
                    Object leftValue = ScriptEngineFactory.get().callObject(new JSTreeGlobal(finaEm, regEm, processingGlobalHelper, getMdtNodeByCode, r.getSchedule().getId(), r.getReturnVersion().getId()), ScriptEngineBase.createFunction(comp.leftEquation));

                    boolean isNumber = leftValue instanceof Number && compValue instanceof Number;

                    if (leftValue instanceof Number) {
                        leftValue = Double.toString(convertAndRoundNumber(processingUtil, comp, leftValue.toString()));
                    }
                    if (compValue instanceof Number) {
                        compValue = Double.toString(convertAndRoundNumber(processingUtil, comp, compValue.toString()));
                    }

                    int compResult = 0;
                    String compStr = "";
                    if (isNumber) {
                        compResult = compare(processingUtil, compValue.toString(), leftValue.toString(), item.dataType);
                        compStr = (comp.leftEquation != null && !comp.leftEquation.trim().isEmpty() ? equationToUserFriendly(comp.leftEquation) : "");
                    }

                    switch (comp.condition) {
                        case EQUALS: {
                            if (compResult != 0) {
                                processingUtil.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_EQUALS, item.code, item.description, leftValue, compStr, compValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case NOT_EQUALS: {
                            if (compResult == 0) {
                                processingUtil.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_NOT_EQUALS, item.code, item.description, leftValue, compStr, compValue, equationToUserFriendly(comp.equation));
                            }
                            break;

                        }
                        case GREATER: {
                            if (compResult <= 0) {
                                processingUtil.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_GREATER, item.code, item.description, leftValue, compStr, compValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case GREATER_EQUALS: {
                            if (compResult < 0) {
                                processingUtil.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_GREATER_EQUALS, item.code, item.description, leftValue, compStr, compValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case LESS: {
                            if (compResult >= 0) {
                                processingUtil.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_LESS, item.code, item.description, leftValue, compStr, compValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case LESS_EQUALS: {
                            if (compResult > 0) {
                                processingUtil.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_LESS_EQUALS, item.code, item.description, leftValue, compStr, compValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }

                        case UNKNOWN: {
                            processingUtil.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.UNKNOWN_COMPARISON, item.code, item.description);
                        }
                        case CONTAINS: {
                            if (!(leftValue instanceof Collection)) {
                                leftValue = new HashSet<>(Collections.singletonList(leftValue));
                            }
                            if (!(compValue instanceof Collection)) {
                                compValue = new HashSet<>(Collections.singletonList(compValue));
                            }

                            Set lv = (Set) leftValue;
                            Set rv = (Set) compValue;
                            if (!(lv).containsAll(rv)) {

                                if (lv.size() >= rv.size()) {
                                    lv.removeAll(rv);
                                } else {
                                    rv.removeAll(lv);
                                }
                                String leftValueTruncatedString = ListElementUtil.truncateListElementMessage(lv);
                                String rightValueTruncatedString = ListElementUtil.truncateListElementMessage(rv);

                                processingUtil.getErrorHandler().onComparisonError(comp, item, ErrorHandler.MessageId.COMPARISON_CONTAINS, item.code, leftValueTruncatedString, rightValueTruncatedString);

                            }
                        }

                    }
                }

            }

            ProcessResult pr = new ProcessResult();
            pr.setReturnId(r.getId());
            pr.setReturnDefinitionCode(r.getSchedule().getReturnDefinition().getCode());
            crossFileValidationResult.put(r.getId(), pr);

            if (processingUtil.getErrorHandler().hasErrors(r.getId())) {
                pr.setStatus(ProcessStatus.STATUS_ERRORS);
                pr.setProcessNote(processingUtil.getErrorHandler().getErrorMessage(r.getId()));
            } else {
                pr.setStatus(ProcessStatus.STATUS_PROCESSED);
                pr.setProcessNote("Process OK.");
            }
        }

        return crossFileValidationResult;
    }

    private String equationToUserFriendly(String equation) {
        if (equation != null) {
            equation = equation.replace("tree.lookup", "lookup");
        }
        return equation;
    }

    private int compare(ProcessingUtil util, String compValue, String itemValue, MDTNodeDataTypes dataType) {
        switch (dataType) {
            case NUMERIC:
                double cv = Double.parseDouble(compValue);
                double iv = Double.parseDouble(itemValue);
                return Double.compare(iv, cv);
            case TEXT:
                if (itemValue != null && compValue != null) {
                    return itemValue.compareTo(compValue);
                } else {
                    return Objects.equals(compValue, itemValue) ? 0 : 1;
                }
            case DATE:
                try {
                    Date id = util.getDateFormat().parse(itemValue);
                    Date cd = util.getDateFormat().parse(compValue);
                    if (id != null && cd != null) {
                        return id.compareTo(cd);
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
                break;
            case DATE_TIME:
                try {
                    Date id = util.getDateTimeFormat().parse(itemValue);
                    Date cd = util.getDateTimeFormat().parse(compValue);
                    if (id != null && cd != null) {
                        return id.compareTo(cd);
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
                break;
        }
        return 0;
    }

    private double convertAndRoundNumber(ProcessingUtil util, ComparisonItem comp, String value) {
        double result = .0;
        if ((value != null) && (value.trim().length() != 0) && (!value.equals("undefined"))) {
            try {
                result = Double.parseDouble(value);
                if (util.getPrecisionFormat() != null) {
                    String number2 = util.getPrecisionFormat().format(result);
                    result = util.getPrecisionFormat().parse(number2).doubleValue();
                }

                //Find number pattern
                DecimalFormat decimalFormat = util.getDecimalFormat();
                if (comp.numberPattern != null && !comp.numberPattern.trim().isEmpty()) {
                    decimalFormat = new DecimalFormat(comp.numberPattern);
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                }

                result = decimalFormat.parse(decimalFormat.format(result)).doubleValue();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        } else {
            result = Double.NaN;
        }

        return result;
    }

}
