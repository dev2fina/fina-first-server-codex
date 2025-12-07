package net.fina.server.processing;

import jakarta.ejb.ApplicationException;
import java.util.List;

@ApplicationException(rollback = true)
public class ProcessException extends Exception {

    private List<Integer> errorImportedReturnIds;

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(String message, List<Integer> errorImportedReturnIds) {
        this(message);
        this.errorImportedReturnIds = errorImportedReturnIds;
    }

    public List<Integer> getErrorImportedReturnIds() {
        return errorImportedReturnIds;
    }

    public void setErrorImportedReturnIds(List<Integer> errorImportedReturnIds) {
        this.errorImportedReturnIds = errorImportedReturnIds;
    }
}
