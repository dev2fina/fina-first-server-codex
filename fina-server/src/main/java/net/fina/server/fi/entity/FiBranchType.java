package net.fina.server.fi.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

@Entity(name = "IN_BANK_BRANCH_TYPES")
@Table(name = "IN_BANK_BRANCH_TYPES")
public class FiBranchType implements Audited {

    @Id
    @SequenceGenerator(name = "fi_branch_types_sequence", sequenceName = "fi_branch_types_sequence", allocationSize = 1)
    @GeneratedValue(generator = "fi_branch_types_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "CODE", unique = true)
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description name;

    @Column(name = "JSON_CONFIG")
    private String jsonConfig;

    @Transient
    private long count;

    public FiBranchType() {
    }

    public FiBranchType(long id) {
        this.id = id;
    }

    public FiBranchType(long id, String code) {
        this.id = id;
        this.code = code;
    }

    public FiBranchType(long id, String code, Description name, long count) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.count = count;
    }

    public FiBranchType(long id, String code, Description name, String jsonConfig, long count) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.jsonConfig = jsonConfig;
        this.count = count;
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

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    public String getJsonConfig() {
        return jsonConfig;
    }

    public void setJsonConfig(String jsonConfig) {
        this.jsonConfig = jsonConfig;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
