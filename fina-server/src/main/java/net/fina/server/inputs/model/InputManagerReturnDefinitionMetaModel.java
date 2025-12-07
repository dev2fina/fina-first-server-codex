package net.fina.server.inputs.model;

public class InputManagerReturnDefinitionMetaModel {

    private long id;
    private Integer version;
    private String code;
    private String description;

    public InputManagerReturnDefinitionMetaModel() {}

    public InputManagerReturnDefinitionMetaModel(long id, Integer version, String code, String description) {
        this.id = id;
        this.version = version;
        this.code = code;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
