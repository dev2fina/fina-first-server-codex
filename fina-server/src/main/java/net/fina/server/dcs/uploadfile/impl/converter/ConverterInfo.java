package net.fina.server.dcs.uploadfile.impl.converter;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.server.returns.xml.Return;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps information about converted files
 *
 * @author dato.java
 */
public class ConverterInfo {
    private List<Return> returns;
    private UploadFileStatus status;
    private List<String> reason = new ArrayList<>();

    /**
     * @return The list of XML returns
     */
    public List<Return> getReturns() {
        return returns;
    }


    /**
     * @param returns The list of XML returns
     */
    public void setReturns(List<Return> returns) {
        this.returns = returns;
    }

    public UploadFileStatus getStatus() {
        return status;
    }

    public List<String> getReasons() {
        if (this.reason == null) {
            return new ArrayList<String>();
        }
        return reason;
    }

    public void setReasons(List<String> reasons) {
        this.reason = reasons;
    }

    public void setStatus(UploadFileStatus status) {
        this.status = status;
    }
}
