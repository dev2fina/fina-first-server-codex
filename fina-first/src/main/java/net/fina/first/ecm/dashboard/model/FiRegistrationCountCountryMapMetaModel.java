package net.fina.first.ecm.dashboard.model;

import java.util.Map;

public class FiRegistrationCountCountryMapMetaModel {
    private String countryMapLayerNodeId;
    private Map<String, Long> countByRegion;

    public String getCountryMapLayerNodeId() {
        return countryMapLayerNodeId;
    }

    public void setCountryMapLayerNodeId(String countryMapLayerNodeId) {
        this.countryMapLayerNodeId = countryMapLayerNodeId;
    }

    public Map<String, Long> getCountByRegion() {
        return countByRegion;
    }

    public void setCountByRegion(Map<String, Long> countByRegion) {
        this.countByRegion = countByRegion;
    }
}
