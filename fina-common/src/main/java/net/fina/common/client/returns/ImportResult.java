package net.fina.common.client.returns;

import net.fina.common.client.constants.ImportStatus;

import java.io.Serializable;

/**
 * nikoloz on 1/30/14.
 */
public class ImportResult implements Serializable {
    private long returnId;
    private ImportStatus importStatus;
    private StringBuffer message;

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public ImportStatus getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }

    public StringBuffer getMessage() {
        return message;
    }

    public void setMessage(StringBuffer message) {
        this.message = message;
    }
}
