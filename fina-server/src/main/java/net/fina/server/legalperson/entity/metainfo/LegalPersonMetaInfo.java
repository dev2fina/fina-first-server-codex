package net.fina.server.legalperson.entity.metainfo;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;

@Entity(name = "IN_LEGAL_PERSON_META_INFO")
@Table(name = "IN_LEGAL_PERSON_META_INFO")
@Audited
public class LegalPersonMetaInfo {

    @Id
    @SequenceGenerator(name = "lp_meta_info_sequence", sequenceName = "lp_meta_info_sequence", allocationSize = 1)
    @GeneratedValue(generator = "lp_meta_info_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne
    @JoinColumn(name = "BUSINESS_ENTITY_TYPE_ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private BusinessEntityType businessEntityType;

    @OneToOne
    @JoinColumn(name = "ECONOMIC_ENTITY_TYPE_ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private EconomicEntityType economicEntityType;

    @OneToOne
    @JoinColumn(name = "EQUITY_FORM_TYPE_ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private EquityFormType equityFormType;
    @OneToOne
    @JoinColumn(name = "MANAGEMENT_FORM_TYPE_ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private ManagementFormType managementFormType;

    public LegalPersonMetaInfo() {
    }

    public LegalPersonMetaInfo(long id, BusinessEntityType businessEntityType, EconomicEntityType economicEntityType, EquityFormType equityFormType, ManagementFormType managementFormType) {
        this.id = id;
        this.businessEntityType = businessEntityType;
        this.economicEntityType = economicEntityType;
        this.equityFormType = equityFormType;
        this.managementFormType = managementFormType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BusinessEntityType getBusinessEntityType() {
        return businessEntityType;
    }

    public void setBusinessEntityType(BusinessEntityType businessEntityType) {
        this.businessEntityType = businessEntityType;
    }

    public EconomicEntityType getEconomicEntityType() {
        return economicEntityType;
    }

    public void setEconomicEntityType(EconomicEntityType economicEntityType) {
        this.economicEntityType = economicEntityType;
    }

    public EquityFormType getEquityFormType() {
        return equityFormType;
    }

    public void setEquityFormType(EquityFormType equityFormType) {
        this.equityFormType = equityFormType;
    }

    public ManagementFormType getManagementFormType() {
        return managementFormType;
    }

    public void setManagementFormType(ManagementFormType managementFormType) {
        this.managementFormType = managementFormType;
    }
}
