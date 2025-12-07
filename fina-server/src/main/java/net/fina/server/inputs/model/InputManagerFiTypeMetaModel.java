package net.fina.server.inputs.model;

public class InputManagerFiTypeMetaModel {
    private long id;
    private String code;
    private String name;
    private boolean checked;

    public InputManagerFiTypeMetaModel() {}

    public InputManagerFiTypeMetaModel(long id, String code, String name, boolean checked) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.checked = checked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
