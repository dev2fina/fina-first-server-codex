package net.fina.first.ecm.fi.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FiRegistryFilterModel {
    private String fiRegistryName;
    private String fiRegistryCode;
    private String regionName;
    private String cityName;
    private Date taskReceiptDateFrom;
    private Date taskReceiptDateTo;
    private Date fiRegistryLegalActDateFrom;
    private Date fiRegistryLegalActDateTo;
    private String identity;
    private List<String> types;
    private List<String> status;
    private String author;
    private String fiRegistryLicenseStatus;
    private String fiRegistryLastEditorId;
    private List<String> fiRegistryActionType;
    private String fiRegistryLastInspectorId;
    private List<String> years;
    private Date registrationDateFrom;
    private Date registrationDateTo;

    public String getFiRegistryName() {
        return fiRegistryName;
    }

    public void setFiRegistryName(String fiRegistryName) {
        this.fiRegistryName = fiRegistryName;
    }

    public String getFiRegistryCode() {
        return fiRegistryCode;
    }

    public void setFiRegistryCode(String fiRegistryCode) {
        this.fiRegistryCode = fiRegistryCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Date getTaskReceiptDateFrom() {
        return taskReceiptDateFrom;
    }

    public void setTaskReceiptDateFrom(Date taskReceiptDateFrom) {
        this.taskReceiptDateFrom = taskReceiptDateFrom;
    }

    public Date getTaskReceiptDateTo() {
        return taskReceiptDateTo;
    }

    public void setTaskReceiptDateTo(Date taskReceiptDateTo) {
        this.taskReceiptDateTo = taskReceiptDateTo;
    }

    public Date getFiRegistryLegalActDateFrom() {
        return fiRegistryLegalActDateFrom;
    }

    public void setFiRegistryLegalActDateFrom(Date fiRegistryLegalActDateFrom) {
        this.fiRegistryLegalActDateFrom = fiRegistryLegalActDateFrom;
    }

    public Date getFiRegistryLegalActDateTo() {
        return fiRegistryLegalActDateTo;
    }

    public void setFiRegistryLegalActDateTo(Date fiRegistryLegalActDateTo) {
        this.fiRegistryLegalActDateTo = fiRegistryLegalActDateTo;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFiRegistryLicenseStatus() {
        return fiRegistryLicenseStatus;
    }

    public void setFiRegistryLicenseStatus(String fiRegistryLicenseStatus) {
        this.fiRegistryLicenseStatus = fiRegistryLicenseStatus;
    }

    public String getFiRegistryLastEditorId() {
        return fiRegistryLastEditorId;
    }

    public void setFiRegistryLastEditorId(String fiRegistryLastEditorId) {
        this.fiRegistryLastEditorId = fiRegistryLastEditorId;
    }

    public List<String> getFiRegistryActionType() {
        return fiRegistryActionType;
    }

    public void setFiRegistryActionType(List<String> fiRegistryActionType) {
        this.fiRegistryActionType = fiRegistryActionType;
    }

    public String getFiRegistryLastInspectorId() {
        return fiRegistryLastInspectorId;
    }

    public void setFiRegistryLastInspectorId(String fiRegistryLastInspectorId) {
        this.fiRegistryLastInspectorId = fiRegistryLastInspectorId;
    }

    public List<String> getYears() {
        return years != null ? new ArrayList<>(years) : null;
    }

    public void setYears(List<String> years) {
        this.years = years;
    }

    public Date getRegistrationDateFrom() {
        return registrationDateFrom;
    }

    public void setRegistrationDateFrom(Date registrationDateFrom) {
        this.registrationDateFrom = registrationDateFrom;
    }

    public Date getRegistrationDateTo() {
        return registrationDateTo;
    }

    public void setRegistrationDateTo(Date registrationDateTo) {
        this.registrationDateTo = registrationDateTo;
    }

    public static FiRegistryFilterModel fromString(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        FiRegistryFilterModel o = null;
        try {
            o = mapper.readValue(jsonString, FiRegistryFilterModel.class);
        } catch (IOException e) {
            throw new WebApplicationException();
        }
        return o;
    }

}
