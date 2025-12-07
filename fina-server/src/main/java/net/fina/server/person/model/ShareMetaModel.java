package net.fina.server.person.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.model.LegalPersonMetaModel;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareMetaModel {
    private long id;
    private double sharePercentage;
    private Date shareDate;

    private LegalPersonMetaModel company;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ShareMetaModel() {
    }

    public ShareMetaModel(long id, double sharePercentage, Date shareDate) {
        this.id = id;
        this.sharePercentage = sharePercentage;
        this.shareDate = shareDate;
    }

    public ShareMetaModel(long id, double sharePercentage, Date shareDate, LegalPersonMetaModel company) {
        this.id = id;
        this.sharePercentage = sharePercentage;
        this.shareDate = shareDate;
        this.company = company;
    }

    public double getSharePercentage() {
        return sharePercentage;
    }

    public void setSharePercentage(double sharePercentage) {
        this.sharePercentage = sharePercentage;
    }

    public Date getShareDate() {
        return shareDate;
    }

    public void setShareDate(Date shareDate) {
        this.shareDate = shareDate;
    }

    public LegalPersonMetaModel getCompany() {
        return company;
    }

    public void setCompany(LegalPersonMetaModel company) {
        this.company = company;
    }
}
