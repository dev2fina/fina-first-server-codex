package net.fina.server.dcs.mail.model;

import java.io.Serializable;

public class MailResponseImportedXmlMetaModel implements Serializable {
    private String returnCode;
    private String message;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
