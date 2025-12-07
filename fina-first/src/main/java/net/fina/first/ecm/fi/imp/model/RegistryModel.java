package net.fina.first.ecm.fi.imp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistryModel {

    private String registryType;

    private Map<String, Map<String, RegistryValueModel>> folderProps = new HashMap<>();

    private Map<String, RegistryValueModel> properties = new HashMap<>();

    private Map<String, RegistryModel> detailsMapping = new HashMap<>();

    private Map<String, List<RegistryModel>> detailsValue = new HashMap<>();

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public Map<String, Map<String, RegistryValueModel>> getFolderProps() {
        return folderProps;
    }

    public void setFolderProps(Map<String, Map<String, RegistryValueModel>> folderProps) {
        this.folderProps = folderProps;
    }

    public Map<String, RegistryValueModel> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, RegistryValueModel> properties) {
        this.properties = properties;
    }

    public Map<String, RegistryModel> getDetailsMapping() {
        return detailsMapping;
    }

    public void setDetailsMapping(Map<String, RegistryModel> detailsMapping) {
        this.detailsMapping = detailsMapping;
    }

    public Map<String, List<RegistryModel>> getDetailsValue() {
        return detailsValue;
    }

    public void setDetailsValue(Map<String, List<RegistryModel>> detailsValue) {
        this.detailsValue = detailsValue;
    }
}
