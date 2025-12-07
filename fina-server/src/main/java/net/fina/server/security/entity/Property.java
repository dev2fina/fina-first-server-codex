package net.fina.server.security.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity(name = "SYS_PROPERTIES")
@Table(name = "SYS_PROPERTIES")
public class Property implements Serializable, Audited {

    @Id
    @Column(name = "PROP_KEY", length = 64, nullable = false, unique = true)
    private String propKey;

    @Column(name = "VALUE", length = 256)
    private String value;

    public Property() {
    }

    public Property(String propKey, String value) {
        this.propKey = propKey;
        this.value = value;
    }

    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
