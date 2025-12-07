package net.fina.server.dashboard.dynamic.entity;

import jakarta.validation.constraints.Size;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

@Entity(name = "IN_DASHLETS")
@Table(name = "IN_DASHLETS")
public class Dashlet {
    @Id
    @SequenceGenerator(name = "dashlets_sequence", sequenceName = "dashlets_sequence", allocationSize = 1)
    @GeneratedValue(generator = "dashlets_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description name;

    @Column(name = "DATA_QUERY")
    private String dataQuery;

    @Column(name = "META_INFO")
    private String metaInfoJson;

    @Column(name = "CODE",unique = true)
    @Size(max = 40, message = "Code must not exceed 40 characters")
    private String code;


    public Dashlet() {
    }

    public Dashlet(String dataQuery) {
        this.dataQuery = dataQuery;
    }

    public Dashlet(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    public String getDataQuery() {
        return dataQuery;
    }

    public void setDataQuery(String dataQuery) {
        this.dataQuery = dataQuery;
    }

    public String getMetaInfoJson() {
        return metaInfoJson;
    }

    public void setMetaInfoJson(String metaInfoJson) {
        this.metaInfoJson = metaInfoJson;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
