package net.fina.server.legalperson.model;

import net.fina.server.legalperson.entity.ConnectedCOmpanyConnectionType;
import net.fina.server.legalperson.model.connection.ConnectedCompanyModel;

import java.util.List;

public class ConnectedCompanyConnectionModel {
    private long id;
    private LegalPersonMetaModel source;
    private LegalPersonMetaModel destination;
    private ConnectedCOmpanyConnectionType connectionType;
    private String businessActivity;
    private String strategicPlan;
    private List<ConnectedCompanyModel> sourceConnectedCompanies;
    private List<ConnectedCompanyModel> destinationsConnectedCompanies;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LegalPersonMetaModel getSource() {
        return source;
    }

    public void setSource(LegalPersonMetaModel source) {
        this.source = source;
    }

    public LegalPersonMetaModel getDestination() {
        return destination;
    }

    public void setDestination(LegalPersonMetaModel destination) {
        this.destination = destination;
    }

    public ConnectedCOmpanyConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectedCOmpanyConnectionType connectionType) {
        this.connectionType = connectionType;
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

    public List<ConnectedCompanyModel> getSourceConnectedCompanies() {
        return sourceConnectedCompanies;
    }

    public void setSourceConnectedCompanies(List<ConnectedCompanyModel> sourceConnectedCompanies) {
        this.sourceConnectedCompanies = sourceConnectedCompanies;
    }

    public List<ConnectedCompanyModel> getDestinationsConnectedCompanies() {
        return destinationsConnectedCompanies;
    }

    public void setDestinationsConnectedCompanies(List<ConnectedCompanyModel> destinationsConnectedCompanies) {
        this.destinationsConnectedCompanies = destinationsConnectedCompanies;
    }
}
