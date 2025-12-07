package net.fina.server.dashboard;

public enum SqlInjectionKeyWords {
    ALTER("ALTER"),
    DROP("DROP"),
    TRUNCATE("TRUNCATE"),
    INSERT("INSERT"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    EXEC("EXEC"),
    CREATE("CREATE"),
    COMMENT("--"),
    SEMICOLON(";");

    private final String value;

    SqlInjectionKeyWords(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}
