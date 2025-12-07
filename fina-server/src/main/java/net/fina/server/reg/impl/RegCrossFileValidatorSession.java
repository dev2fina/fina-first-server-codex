package net.fina.server.reg.impl;

import net.fina.common.server.util.CommonUtil;
import net.fina.messages.MessagesUtil;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.processing.ErrorHandler;
import net.fina.server.processing.ListElementUtil;
import net.fina.server.processing.ProcessingConfigSingleton;
import net.fina.server.processing.helper.ProcessingGlobalHelper;
import net.fina.server.processing.impl.ComparisonMessageTemplateUtil;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.script.ScriptEngineBase;
import net.fina.server.processing.script.ScriptEngineFactory;
import net.fina.server.reg.api.RegCrossFileValidator;
import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.util.RegJSTreeGlobal;
import net.fina.server.reg.util.RegUtil;
import net.fina.server.reg.validator.ValidationErrorType;
import net.fina.server.util.FinaDS;
import net.fina.server.util.RegDS;
import org.jboss.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(jakarta.ejb.TransactionAttributeType.NOT_SUPPORTED)
public class RegCrossFileValidatorSession implements RegCrossFileValidator {

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
    public void validatePostProcessComparisons(RegProcessConfig config, Map<String, List<InputMetaModel>> postProcessComparisonInputs) {
        ProcessingGlobalHelper processingGlobalHelper = processingConfigSingleton.getConfig();
        Function<String, MDTNode> getMdtNodeByCode = nodeCode -> mdtCacheManager.getNode(nodeCode);

        for (Map.Entry<String, List<InputMetaModel>> entry : postProcessComparisonInputs.entrySet()) {
            long scheduleId = config.getShceduleMap().get(entry.getKey());
            for (InputMetaModel input : entry.getValue()) {

                for (ComparisonItem comp : input.getComparisons()) {

                    Object rightValue = ScriptEngineFactory.get().callObject(new RegJSTreeGlobal(scheduleId, config.getReturnVersionId(), getMdtNodeByCode, processingGlobalHelper, regEm, finaEm), ScriptEngineBase.createFunction(comp.equation));
                    Object leftValue = ScriptEngineFactory.get().callObject(new RegJSTreeGlobal(scheduleId, config.getReturnVersionId(), getMdtNodeByCode, processingGlobalHelper, regEm, finaEm), ScriptEngineBase.createFunction(comp.leftEquation));

                    boolean isNumber = leftValue instanceof Number && rightValue instanceof Number;

                    if (leftValue instanceof Number) {
                        leftValue = Double.toString(RegUtil.convertAndRoundNumber(comp, leftValue.toString(), config));
                    }
                    if (rightValue instanceof Number) {
                        rightValue = Double.toString(RegUtil.convertAndRoundNumber(comp, rightValue.toString(), config));
                    }

                    int compResult = 0;
                    String compStr = "";
                    if (isNumber) {
                        compResult = RegUtil.compare(rightValue.toString(), leftValue.toString(), input.getTypeEnum(), config);
                        compStr = (comp.leftEquation != null && !comp.leftEquation.trim().isEmpty() ? equationToUserFriendly(comp.leftEquation) : "");
                    }

                    switch (comp.condition) {
                        case EQUALS: {
                            if (compResult != 0) {
                                this.onComparisonError(config, input, entry.getKey(), ErrorHandler.MessageId.COMPARISON_EQUALS, comp, input.getCode(), input.getDescription(), leftValue, compStr, rightValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case NOT_EQUALS: {
                            if (compResult == 0) {
                                this.onComparisonError(config, input, entry.getKey(), ErrorHandler.MessageId.COMPARISON_NOT_EQUALS, comp, input.getCode(), input.getDescription(), leftValue, compStr, rightValue, equationToUserFriendly(comp.equation));
                            }
                            break;

                        }
                        case GREATER: {
                            if (compResult <= 0) {
                                this.onComparisonError(config, input, entry.getKey(), ErrorHandler.MessageId.COMPARISON_GREATER, comp, input.getCode(), input.getDescription(), leftValue, compStr, rightValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case GREATER_EQUALS: {
                            if (compResult < 0) {
                                this.onComparisonError(config, input, entry.getKey(), ErrorHandler.MessageId.COMPARISON_GREATER_EQUALS, comp, input.getCode(), input.getDescription(), leftValue, compStr, rightValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case LESS: {
                            if (compResult >= 0) {
                                this.onComparisonError(config, input, entry.getKey(), ErrorHandler.MessageId.COMPARISON_LESS, comp, input.getCode(), input.getDescription(), leftValue, compStr, rightValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case LESS_EQUALS: {
                            if (compResult > 0) {
                                this.onComparisonError(config, input, entry.getKey(), ErrorHandler.MessageId.COMPARISON_LESS_EQUALS, comp, input.getCode(), input.getDescription(), leftValue, compStr, rightValue, equationToUserFriendly(comp.equation));
                            }
                            break;
                        }
                        case CONTAINS:
                            if (!(leftValue instanceof Collection)) {
                                leftValue = new HashSet<>(Collections.singletonList(leftValue));
                            }
                            if (!(rightValue instanceof Collection)) {
                                rightValue = new HashSet<>(Collections.singletonList(rightValue));
                            }

                            Set lv = (Set) leftValue;
                            Set rv = (Set) rightValue;
                            if (!(lv).containsAll(rv)) {

                                String errorMessage = null;

                                if (lv.size() >= rv.size()) {
                                    lv.removeAll(rv);
                                } else {
                                    rv.removeAll(lv);
                                }

                                String leftValueTruncatedString = ListElementUtil.truncateListElementMessage(lv);
                                String rightValueTruncatedString = ListElementUtil.truncateListElementMessage(rv);

                                if (comp.messageTemplate != null && !Objects.equals(comp.messageTemplate.trim(), "")) {
                                    try {
                                        errorMessage = new ComparisonMessageTemplateUtil().process(comp, input, input.getCode(), input.getDescription(), leftValueTruncatedString, comp.leftEquation, rightValueTruncatedString, comp.equation, 0);
                                    } catch (Throwable t) {
                                        log.error(t.getMessage(), t);
                                    }
                                }

                                if (errorMessage == null) {
                                    errorMessage = CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.comparison.rule.contains.message"), input.getColumn(), lv.size() >= rv.size() ? leftValueTruncatedString : rightValueTruncatedString, lv.size() >= rv.size() ? rightValueTruncatedString : leftValueTruncatedString);
                                }
                                config.getInputValidatorMap().get(entry.getKey()).addError(ValidationErrorType.COMPARISON, input.getColumn(), errorMessage);
                            }
                    }
                }

            }
        }
    }

    @Override
    public void validateUniqueColumnData(RegProcessConfig config) {
        ProcessingGlobalHelper processingGlobalHelper = processingConfigSingleton.getConfig();

        Map<String, List<InputMetaModel>> returnCodeUniqueInputsMap = new HashMap<>();
        for (InputsMetaModel inm : config.getInputs()) {
            returnCodeUniqueInputsMap.put(inm.getReturnCode(), inm.getInputs().stream().filter(InputMetaModel::isKey).collect(Collectors.toList()));
        }

        String query = "select %s from %s where file_id=%s group by %s having count(%s)>1";

        for (Map.Entry<String, List<InputMetaModel>> entry : returnCodeUniqueInputsMap.entrySet()) {
            String returnCode = entry.getKey();
            for (InputMetaModel inm : entry.getValue()) {
                String tableName = processingGlobalHelper.getRegMdtCodeTableNameMap().get(inm.getCode());
                List queryResult = regEm.createNativeQuery(String.format(query, inm.getCode(), tableName, config.getUploadFile().getId(), inm.getCode(), inm.getCode())).getResultList();
                if (!queryResult.isEmpty()) {
                    config.getInputValidatorMap().get(returnCode).addError(ValidationErrorType.OTHER, inm.getColumn(), CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.comparison.rule.notUnique.message"), queryResult.toString()));
                }
            }
        }

    }

    private void onComparisonError(RegProcessConfig config, InputMetaModel item, String key, ErrorHandler.MessageId messageId, ComparisonItem comparisonItem, Object... params) {
        String errorMessage;
        if (comparisonItem.messageTemplate != null && !Objects.equals(comparisonItem.messageTemplate.trim(), "")) {
            try {
                errorMessage = new ComparisonMessageTemplateUtil().process(comparisonItem, item, params);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                errorMessage = onError(messageId, params);
            }
        } else {
            errorMessage = onError(messageId, params);
        }
        config.getInputValidatorMap().get(key).addError(ValidationErrorType.COMPARISON, item.getColumn(), errorMessage);

    }

    private String onError(ErrorHandler.MessageId messageId, Object... params) {
        return compileMessage(messageId, params);

    }

    private String compileMessage(ErrorHandler.MessageId messageId, Object... params) {
        String message = MessagesUtil.getString(messageId.getCode());
        if (params != null && params.length > 0) {
            message = String.format(message, params);
        }
        return message;
    }

    private String equationToUserFriendly(String equation) {
        if (equation != null) {
            equation = equation.replace("tree.lookup", "lookup");
        }
        return equation;
    }
}
