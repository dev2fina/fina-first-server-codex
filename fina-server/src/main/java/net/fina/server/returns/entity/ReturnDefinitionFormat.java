package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "IN_RETURN_DEFINITIONS")
public class ReturnDefinitionFormat implements Serializable , Audited {
    @Id
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "FORMAT")
    private byte[] format;

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

    @Override
    public String toString() {
        return "ReturnDefinitionFormat{" +
                "id=" + id +
                ", version=" + version +
                '}';
    }
}
