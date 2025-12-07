package net.fina.common.shared;

import jakarta.xml.bind.annotation.XmlElement;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SimpleMenuModel implements Serializable {
    private String code;
    private Map<String, String> i18n;
    private List<String> permissions;
    @XmlElement
    private String iframeSrc;

    public SimpleMenuModel() {
    }

    public SimpleMenuModel(String code, List<String> permission, Map<String, String> i18n, String iframeSrc) {
        this.code = code;
        this.permissions = permission;
        this.i18n = i18n;
        this.iframeSrc = iframeSrc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, String> i18n) {
        this.i18n = i18n;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getIframeSrc() {
        return iframeSrc;
    }

    public void setIframeSrc(String iframeSrc) {
        this.iframeSrc = iframeSrc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleMenuModel that = (SimpleMenuModel) o;
        return that.getCode().equals(getCode());
    }

    @Override
    public int hashCode() {
        return getCode().hashCode();
    }

    @Override
    public String toString() {
        return "MenuModel{" +
                ", code='" + code + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
