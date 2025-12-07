package net.fina.server.reg.model;

public enum InputTypeEnum {
    TIMESTAMP("Timestamp"),
    DATE("Date"),
    BOOLEAN("boolean"),
    NUMBER("double"),
    BIGNUMBER("long"),
    INTEGER("int"),
    STRING("String"),
    BINARY("byte[]"),
    DATETIME("Timestamp");

    private final String javaType;

    InputTypeEnum(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaType() {
        return javaType;
    }
}
