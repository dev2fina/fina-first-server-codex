package net.fina.server.fi.model;

import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.person.model.ShareMetaModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FiShareMetaModel {
    private long id;
    private LegalPersonMetaModel company;
    private double share;
    private Date shareDate;

    private List<ShareMetaModel> shares;

    public FiShareMetaModel() {
    }

    public FiShareMetaModel(LegalPersonMetaModel company, double share, Date shareDate) {
        this.company = company;
        this.share = share;
        this.shareDate = shareDate;
    }

    public LegalPersonMetaModel getCompany() {
        return company;
    }

    public void setCompany(LegalPersonMetaModel company) {
        this.company = company;
    }

    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }

    public Date getShareDate() {
        return shareDate;
    }

    public void setShareDate(Date shareDate) {
        this.shareDate = shareDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ShareMetaModel> getShares() {
        return shares == null ? new ArrayList<>() : shares;
    }

    public void setShares(List<ShareMetaModel> shares) {
        this.shares = shares;
    }
}
