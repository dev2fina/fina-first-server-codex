package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Date;

public class ReturnStatusDashletModel implements Serializable {
    private String id;
    private int uploaded;
    private int error;
    private int notUploaded;
    private Date period;

    public ReturnStatusDashletModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getNotUploaded() {
        return notUploaded;
    }

    public void setNotUploaded(int notUploaded) {
        this.notUploaded = notUploaded;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public enum ReturnStatusType {
        UPLOADED("net.fina.web.dashlet.returnStatusStatistics.uploaded"),
        ERROR("net.fina.web.dashlet.returnStatusStatistics.error"),
        NOT_UPLOADED("net.fina.web.dashlet.returnStatusStatistics.notUploaded");


        private String code;

        private ReturnStatusType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum ReturnFileType {
        FR("net.fina.web.dashlet.returnStatusStatistics.FR"),
        LFP("net.fina.web.dashlet.returnStatusStatistics.LFP");

        private String code;

        private ReturnFileType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
