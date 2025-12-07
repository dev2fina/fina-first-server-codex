package net.fina.server.dcs.uploadfile.model;

import java.util.List;

public class UploadFileScheduleRequestMetaModel {
    private UploadFileMetaModel model;
    private String fiCode;
    private String periodFrom;
    private String periodTo;
    private List<String> returnCodes;

    public UploadFileMetaModel getModel() {
        return model;
    }

    public void setModel(UploadFileMetaModel model) {
        this.model = model;
    }

    public String getFiCode() {
        return fiCode;
    }

    public void setFiCode(String fiCode) {
        this.fiCode = fiCode;
    }

    public String getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(String periodFrom) {
        this.periodFrom = periodFrom;
    }

    public String getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(String periodTo) {
        this.periodTo = periodTo;
    }

    public List<String> getReturnCodes() {
        return returnCodes;
    }

    public void setReturnCodes(List<String> returnCodes) {
        this.returnCodes = returnCodes;
    }
}
