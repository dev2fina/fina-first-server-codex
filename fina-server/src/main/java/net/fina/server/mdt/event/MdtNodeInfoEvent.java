package net.fina.server.mdt.event;

import net.fina.common.client.mdt.MDTImportResult;

import java.io.Serializable;
import java.util.List;

public class MdtNodeInfoEvent implements Serializable {
    private final String userLogin;
    private final List<MDTImportResult> results;

    public MdtNodeInfoEvent(String userLogin, List<MDTImportResult> results) {
        this.userLogin = userLogin;
        this.results = results;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public List<MDTImportResult> getResults() {
        return results;
    }
}
