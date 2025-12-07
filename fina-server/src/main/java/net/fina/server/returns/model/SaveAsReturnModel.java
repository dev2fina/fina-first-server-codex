package net.fina.server.returns.model;

import net.fina.common.client.returns.ReturnModel;

import java.util.List;

public class SaveAsReturnModel {
    private List<ReturnModel> returnModels;
    private long versionId;
    private String note;

    public SaveAsReturnModel() {
    }

    public SaveAsReturnModel(List<ReturnModel> returnModels, long versionId, String note) {
        this.returnModels = returnModels;
        this.versionId = versionId;
        this.note = note;
    }

    public List<ReturnModel> getReturnModels() {
        return returnModels;
    }

    public void setReturnModels(List<ReturnModel> returnModels) {
        this.returnModels = returnModels;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
