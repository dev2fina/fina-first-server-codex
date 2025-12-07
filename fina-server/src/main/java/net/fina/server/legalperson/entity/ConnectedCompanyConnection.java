package net.fina.server.legalperson.entity;

import jakarta.persistence.*;

@Entity(name = "IN_CONNECTED_COMPANIES_INFO")
@Table(name = "IN_CONNECTED_COMPANIES_INFO")
public class ConnectedCompanyConnection {
    @Id
    @SequenceGenerator(name = "connected_company_sequence", sequenceName = "connected_company_sequence", allocationSize = 1)
    @GeneratedValue(generator = "connected_company_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_ID")
    private LegalPerson source;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DESTINATION_ID")
    private LegalPerson destination;
    @Column(name = "BUSINESS_ACTIVITY")
    private String businessActivity;
    @Column(name = "STRATEGIC_PLAN")
    private String strategicPlan;
    @Column(name = "CONNECTION_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ConnectedCOmpanyConnectionType connectionType;

    public ConnectedCompanyConnection() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LegalPerson getSource() {
        return source;
    }

    public void setSource(LegalPerson source) {
        this.source = source;
    }

    public LegalPerson getDestination() {
        return destination;
    }

    public void setDestination(LegalPerson destination) {
        this.destination = destination;
    }

    public String getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(String businessActivity) {
        this.businessActivity = businessActivity;
    }

    public String getStrategicPlan() {
        return strategicPlan;
    }

    public void setStrategicPlan(String strategicPlan) {
        this.strategicPlan = strategicPlan;
    }

    public ConnectedCOmpanyConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectedCOmpanyConnectionType connectionType) {
        this.connectionType = connectionType;
    }
}
