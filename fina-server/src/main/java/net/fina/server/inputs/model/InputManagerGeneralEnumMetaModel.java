package net.fina.server.inputs.model;

public class InputManagerGeneralEnumMetaModel {

    private int ordinal;
    private String code;
    private String name;

    public InputManagerGeneralEnumMetaModel() {}

    public InputManagerGeneralEnumMetaModel(int ordinal, String code, String name) {
        this.ordinal = ordinal;
        this.code = code;
        this.name = name;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
