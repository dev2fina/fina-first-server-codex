package net.fina.server.fi.model;

import net.fina.common.client.fis.FiModel;
import net.fina.server.person.model.CriminalRecordMetaModel;
import net.fina.server.person.model.ShareMetaModel;

import java.util.ArrayList;
import java.util.List;

public class FiModelFull extends FiModel {
    private List<ShareMetaModel> shares;
    private List<CriminalRecordMetaModel> criminalRecords;

    public List<ShareMetaModel> getShares() {
        return shares == null ? new ArrayList<>() : shares;
    }

    public void setShares(List<ShareMetaModel> shares) {
        this.shares = shares;
    }

    public List<CriminalRecordMetaModel> getCriminalRecords() {
        return criminalRecords;
    }

    public void setCriminalRecords(List<CriminalRecordMetaModel> criminalRecords) {
        this.criminalRecords = criminalRecords;
    }
}
