package net.fina.first.ecm.search.api;

public enum SearchPropertyWildcardCondition {
    STARTS_WITH("{0}*"),
    ENDS_WITH("*{0}"),
    ANY_MATCH("*{0}*"),
    EXACT_MATCH("{0}");

    private final String condition;

    SearchPropertyWildcardCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
