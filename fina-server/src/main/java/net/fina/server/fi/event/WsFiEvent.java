package net.fina.server.fi.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fina.common.client.fis.FiImportWrapper;

import java.io.Serializable;

public class WsFiEvent implements Serializable {
    private FiImportWrapper fiImportWrapper;

    private WsFiEventType type;

    @JsonIgnore
    private String userLogin;

    public WsFiEvent() {
    }

    public WsFiEvent(FiImportWrapper fiImportWrapper, WsFiEventType type, String userLogin) {
        this.fiImportWrapper = fiImportWrapper;
        this.type = type;
        this.userLogin = userLogin;
    }

    public FiImportWrapper getFiImportWrapper() {
        return fiImportWrapper;
    }

    public void setFiImportWrapper(FiImportWrapper fiImportWrapper) {
        this.fiImportWrapper = fiImportWrapper;
    }

    public WsFiEventType getType() {
        return type;
    }

    public void setType(WsFiEventType type) {
        this.type = type;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }


}
