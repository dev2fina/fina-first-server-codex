package net.fina.server.dashboard.legacy.model;

import java.io.Serializable;

public class FilterModel implements Serializable {
    private String property;
    private String value;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
