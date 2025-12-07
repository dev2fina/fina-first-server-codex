package net.fina.server.legislative.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "IN_LAW_DOC_CATEGORY")
@Table(name = "IN_LAW_DOC_CATEGORY")
public class LegislativeDocumentCategory implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "law_doc_cat_seq", sequenceName = "law_doc_cat_seq", allocationSize = 1)
    @GeneratedValue(generator = "law_doc_cat_seq", strategy = GenerationType.SEQUENCE)
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description name;

    public LegislativeDocumentCategory(long id) {
        this.id = id;
    }

    public LegislativeDocumentCategory() {}

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

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegislativeDocumentCategory that = (LegislativeDocumentCategory) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
