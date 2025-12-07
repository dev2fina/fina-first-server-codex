package net.fina.common.shared.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ConfigMetaModel implements Serializable {

    private String dateFormat;

    private List<String> permissions;

    private Serializable data;

    private Map<String, Serializable> properties;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Serializable getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

    public Map<String, Serializable> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Serializable> properties) {
        this.properties = properties;
    }
}
