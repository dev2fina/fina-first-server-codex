package net.fina.server.fi.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.*;

import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.entity.MDTNode;
import org.hibernate.annotations.Type;

@Entity(name = "IN_COUNTRY_DATA")
@Table(name = "IN_COUNTRY_DATA")
@NamedQueries({
        @NamedQuery(name = "checkRegionCodeUnique", query = "select c from IN_COUNTRY_DATA as c WHERE trim(c.code)=:code and c.id<>:id ")
})
public class Region implements Serializable, net.fina.auditlog.api.Audited {

    @Id
    @SequenceGenerator(name = "in_country_data_sequence", sequenceName = "in_country_data_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_country_data_sequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description description;

    @Column(name = "PARENTID")
    private long parentId;

    @Column(name = "SEQUENCE")
    private long sequence;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE_1")
    private String code1;

    @Column(name = "DELETED")
    private boolean deleted;

    @OneToOne
    @JoinColumn(name = "MDT_DATA_NODE_ID")
    private MDTNode mdtNode;


    public Region(long id, long parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    public Region() {

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

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public MDTNode getMdtNode() {
        return mdtNode;
    }

    public void setMdtNode(MDTNode mdtNode) {
        this.mdtNode = mdtNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return getId() == region.getId() && Objects.equals(getCode(), region.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCode());
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description=" + description +
                ", parentId=" + parentId +
                ", sequence=" + sequence +
                ", version=" + version +
                ", deleted=" + deleted +
                '}';
    }
}
