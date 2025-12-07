package net.fina.common.client.fis;

import java.io.Serializable;

public class FiModelSimple implements Serializable {
    protected long id;
    // descriptions
    protected String code;
    protected long nameStrId;
    protected String name;

    public FiModelSimple() {
    }

    public FiModelSimple(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
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

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
