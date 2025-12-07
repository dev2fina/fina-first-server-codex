package net.fina.server.security.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OSTLicense implements Serializable {
    private Date notAfter;
    private OSTLicenseKind licenseKind;
    private List<String> fiTypes;

    public OSTLicense() {
    }

    public OSTLicense(OSTLicenseKind licenseKind) {
        this.licenseKind = licenseKind;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public OSTLicenseKind getLicenseKind() {
        return licenseKind;
    }

    public void setLicenseKind(OSTLicenseKind licenseKind) {
        this.licenseKind = licenseKind;
    }

    public List<String> getFiTypes() {
        return fiTypes;
    }

    public void setFiTypes(List<String> fiTypes) {
        this.fiTypes = fiTypes;
    }

    @Override
    public String toString() {
        return "OstLicense{" +
                "notAfter=" + notAfter +
                ", licenseKind='" + licenseKind + '\'' +
                ", fiTypes=" + fiTypes +
                '}';
    }
}
