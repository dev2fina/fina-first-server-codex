package net.fina.server.cems.entity.sanction;


import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

@Entity(name = "CEMS_SANCTION_STATUS_TABLE")
@Table(name = "CEMS_SANCTION_STATUS_TABLE")
public class CEMSSanctionStatus {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "cems_sanction_status_sequence", sequenceName = "cems_sanction_status_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cems_sanction_status_sequence")
    private long id;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @Column(name = "CODE", unique = true)
    private String code;


    public CEMSSanctionStatus(){

    }

    public CEMSSanctionStatus(String code){
        this.code = code;
    }

    public CEMSSanctionStatus(String code, Description description){
        this.code = code;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
