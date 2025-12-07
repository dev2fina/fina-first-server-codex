package net.fina.server.cems.entity.sanction;

import jakarta.persistence.*;

@Entity(name = "CEMS_SANCTION_REGULATIONS")
@Table(name = "CEMS_SANCTION_REGULATIONS")
public class CEMSSanctionRegulation {
    @Id
    @SequenceGenerator(name = "cems_sanction_reg_sequence", sequenceName = "cems_sanction_reg_sequence", allocationSize = 1)
    @GeneratedValue(generator = "cems_sanction_reg_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne()
    @JoinColumn(name = "REGULATION")
    private CEMSSanctionRegulationTable regulationCatalog;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "ACTUAL_VALUE")
    private String actualValue;

    public CEMSSanctionRegulation() {
    }

    public CEMSSanctionRegulation(long id, CEMSSanctionRegulationTable regulationCatalog, String value, String actualValue) {
        this.id = id;
        this.regulationCatalog = regulationCatalog;
        this.value = value;
        this.actualValue = actualValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CEMSSanctionRegulationTable getRegulationCatalog() {
        return regulationCatalog;
    }

    public void setRegulationCatalog(CEMSSanctionRegulationTable regulationCatalog) {
        this.regulationCatalog = regulationCatalog;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }
}
