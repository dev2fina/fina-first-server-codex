package net.fina.server.classifier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.server.classifier.entity.DataType;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MDTCatalogColumnMetaModel {
    private long id;
    private DataType dataType;
    private String name;
    private Map<Long, String> names = new HashMap<>();
    private long nameStrId;
    private boolean key;
    private int sequence;
    private String dataFormat;
    private boolean isRequired;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Map<Long, String> getNames() {
        return names == null ? new HashMap<>() : names;
    }

    public void setNames(Map<Long, String> names) {
        this.names = names;
    }
}
