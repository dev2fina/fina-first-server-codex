package net.fina.first.ecm.organization.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrganizationIndividualLicenseCertificateTypeMetaModel implements Serializable {
    private String id;
    private String type;
    private String name;
    private String identifier;
    private String documentNumber;
    private List<String> allowedOperations;
    private Date registrationDate;

    public OrganizationIndividualLicenseCertificateTypeMetaModel() {
    }

    public OrganizationIndividualLicenseCertificateTypeMetaModel(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public List<String> getAllowedOperations() {
        return allowedOperations;
    }

    public void setAllowedOperations(List<String> allowedOperations) {
        this.allowedOperations = allowedOperations;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
