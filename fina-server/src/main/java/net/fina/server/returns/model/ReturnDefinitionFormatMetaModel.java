package net.fina.server.returns.model;

import net.fina.server.returns.entity.ReturnDefinitionFormat;

import java.io.Serializable;

public class ReturnDefinitionFormatMetaModel implements Serializable {
    private long id;
    private Integer version;
    private byte[] format;

    public ReturnDefinitionFormatMetaModel setEntity(ReturnDefinitionFormat format) {
        if (format != null) {
            this.id = format.getId();
            this.version = format.getVersion();
            this.format = format.getFormat();
        }
        return this;
    }

    public ReturnDefinitionFormat toEntity() {
        ReturnDefinitionFormat format = new ReturnDefinitionFormat();
        format.setId(this.id);
        format.setVersion(this.version);
        format.setFormat(this.format);
        return format;
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

    public byte[] getFormat() {
        return format;
    }

    public void setFormat(byte[] format) {
        this.format = format;
    }
}
