package net.fina.common.client.mdt;

import java.io.Serializable;

public enum MDTComparisonConditions implements Serializable {
    /**
     * Defined unknown condition
     */
    UNKNOWN("mdt.comparison.condition.unknown"),
    /**
     * Defines equals condition (=).
     */
    EQUALS("mdt.comparison.condition.equals"),
    /**
     * Defines not equals condition (!=).
     */
    NOT_EQUALS("mdt.comparison.condition.not.equals"),
    /**
     * Defines greater condition (>).
     */
    GREATER("mdt.comparison.condition.greater"),
    /**
     * Defines greater equals condition (>=).
     */
    GREATER_EQUALS("mdt.comparison.condition.greater.equals"),
    /**
     * Defines less condition (<).
     */
    LESS("mdt.comparison.condition.less"),
    /**
     * Defines less equals condition (<=).
     */
    LESS_EQUALS("mdt.comparison.condition.less.equals"),
    CONTAINS("mdt.comparison.condition.contains");

    private String messageCode;

    private MDTComparisonConditions(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public static MDTComparisonConditions getComparisonConditions(String comparison) {
        switch (comparison) {
            case "=":
                return MDTComparisonConditions.EQUALS;
            case "!=":
                return MDTComparisonConditions.NOT_EQUALS;
            case ">":
                return MDTComparisonConditions.GREATER;
            case ">=":
                return MDTComparisonConditions.GREATER_EQUALS;
            case "<":
                return MDTComparisonConditions.LESS;
            case "<=":
                return MDTComparisonConditions.LESS_EQUALS;
        }
        if (comparison.equalsIgnoreCase("CONTAINS"))
            return MDTComparisonConditions.CONTAINS;
        return MDTComparisonConditions.UNKNOWN;
    }

    public static String getComparisonString(MDTComparisonConditions conditions) {
        if (conditions.ordinal() == MDTComparisonConditions.EQUALS.ordinal())
            return "=";
        else if (conditions.ordinal() == MDTComparisonConditions.NOT_EQUALS.ordinal())
            return "!=";
        else if (conditions.ordinal() == MDTComparisonConditions.GREATER.ordinal())
            return ">";
        else if (conditions.ordinal() == MDTComparisonConditions.GREATER_EQUALS.ordinal())
            return ">=";
        else if (conditions.ordinal() == MDTComparisonConditions.LESS.ordinal())
            return "<";
        else if (conditions.ordinal() == MDTComparisonConditions.LESS_EQUALS.ordinal())
            return "<=";
        else if (conditions.ordinal() == MDTComparisonConditions.CONTAINS.ordinal())
            return "Contains";
        return "";
    }

}
