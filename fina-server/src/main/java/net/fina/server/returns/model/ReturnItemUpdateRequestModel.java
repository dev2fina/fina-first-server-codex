package net.fina.server.returns.model;

import java.io.Serializable;
import java.util.List;

public class ReturnItemUpdateRequestModel implements Serializable {
    private List<RItemMetaModel> inputItems;
    private long returnId;
    private long returnVersionId;
    private String note;
    private boolean process;

    public ReturnItemUpdateRequestModel() {
    }

    public ReturnItemUpdateRequestModel(List<RItemMetaModel> inputItems, long returnId, long returnVersionId, String note) {
        this.inputItems = inputItems;
        this.returnId = returnId;
        this.returnVersionId = returnVersionId;
        this.note = note;
    }

    public List<RItemMetaModel> getInputItems() {
        return inputItems;
    }

    public void setInputItems(List<RItemMetaModel> inputItems) {
        this.inputItems = inputItems;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public long getReturnVersionId() {
        return returnVersionId;
    }

    public void setReturnVersionId(long returnVersionId) {
        this.returnVersionId = returnVersionId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isProcess() {
        return process;
    }

    public void setProcess(boolean process) {
        this.process = process;
    }
}
