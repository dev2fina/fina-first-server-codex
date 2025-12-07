package net.fina.server.returns.model;

import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.client.returns.ReturnModel;

import java.util.List;

public class ReturnChangeStatusModel {
    private List<ReturnModel> returnModels;
    private ProcessStatus status;
    private String note;

    public ReturnChangeStatusModel() {
    }

    public ReturnChangeStatusModel(List<ReturnModel> returnModels, ProcessStatus status, String note) {
        this.returnModels = returnModels;
        this.status = status;
        this.note = note;
    }

    public List<ReturnModel> getReturnModels() {
        return returnModels;
    }

    public void setReturnModels(List<ReturnModel> returnModels) {
        this.returnModels = returnModels;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
