package net.fina.server.processing;

import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;

public interface ErrorHandler {

    enum MessageId {

        GENERAL_ERROR("net.fina.processing.generalError"),
        INVALID_XML_STRUCTURE("net.fina.processing.invalidXmlStructure"),
        NODE_IS_REQUIRED("net.fina.processing.requiredNode"),
        INVALID_NUMBER_FORMAT("net.fina.processing.invalidNumberFormat"),
        INVALID_DATE_FORMAT("net.fina.processing.invalidDateFormat"),
        INVALID_TEXT_LENGTH("net.fina.processing.invalidTextLength"),
        UNKNOWN_NODE_DATA_TYPE("net.fina.processing.unknownNodeDataType"),
        UNKNOWN_COMPARISON("net.fina.processing.unknownComparison"),
        COMPARISON_EQUALS("net.fina.processing.comparisonEquals"),
        COMPARISON_NOT_EQUALS("net.fina.processing.comparisonNotEquals"),
        COMPARISON_GREATER("net.fina.processing.comparisonGreater"),
        COMPARISON_GREATER_EQUALS("net.fina.processing.comparisonGreaterEquals"),
        COMPARISON_LESS("net.fina.processing.comparisonLess"),
        COMPARISON_LESS_EQUALS("net.fina.processing.comparisonLessEquals"),
        COMPARISON_CONTAINS("net.fina.comparison.rule.contains.message"),
        INVALID_RETURN_CODE("net.fina.processing.invalidReturnCode"),
        INVALID_RETURN_FROM_DATEFORMAT("net.fina.processing.invalidFromDateFormat"),
        INVALID_RETURN_TO_DATEFORMAT("net.fina.processing.invalidToDateFormt"),
        DIFERENT_PERIOD_RETURNS("net.fina.processing.differentPeriodReturns"),
        INVALID_PERIOD_TYPE("net.fina.processing.invalidPeriodType"),
        INVALID_FI_CODE("net.fina.processing.invalidFiCode"),
        INVALID_PERIOD("net.fina.processing.invalidPeriod"),
        RETURN_REQUIERS_DEPENDENT("net.fina.processing.returnRequiresDependent"),
        RETURN_REQUIERS_DEPENDENT_ON("net.fina.processing.returnRequiresDependentOn"),
        RETURN_HAS_WRONG_ITEMS("net.fina.processing.wrongItems"),
        INVALID_LIST_ELEMENT_VALUE("net.fina.processing.invalidListElementValue");

        private String code;

        private MessageId(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    void onError(Throwable ex, ProcessItem item, MessageId messageId, Object... params);

    void onComparisonError(ComparisonItem comparisonItem, ProcessItem item, MessageId messageId, Object... params);

    boolean hasErrors(long returnId);

    String getErrorMessage(long returnId);
}
