package net.fina.server.dashboard;

public enum SqlKeywords {
    DISTINCT,
    ORDER_BY("ORDER BY"),
    DELETE,
    UPDATE,
    TRUNCATE,
    INSERT,
    DROP,
    DESC,
    EQUALS("="),
    NOT_EQUALS("!="),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    IN("IN"),
    NOT_IN("NOT IN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    DOES_NOT_BEGIN_WITH("DOES NOT BEGIN WITH"),
    DOES_NOT_END_WITH("DOES NOT END WITH");

    private String value;

    public String value() {
        return this.value != null ? this.value : this.name();
    }

    SqlKeywords(String value) {
        this.value = value;
    }

    SqlKeywords() {
    }

    public static SqlKeywords fromString(String text) {
        for (SqlKeywords keyword : SqlKeywords.values()) {
            if (keyword.value().equalsIgnoreCase(text)) {
                return keyword;
            }
        }
        return null;
    }
}