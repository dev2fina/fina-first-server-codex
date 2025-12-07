package net.fina.common.client.mdt;

import net.fina.common.client.exception.FinATypeException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: Alexander
 * Date: 11/27/13
 * Time: 9:32 AM
 */
public class MDTImportResult implements Serializable {
    private Set<String> codesNotFound;
    private Map<String, String> renamedNodes;
    private Set<String> importedCodesToFix;
    private FinATypeException exception;
    private Set<String> importedNodeCodes;
    private String message;

    public MDTImportResult() {
        codesNotFound = new HashSet<String>();
        renamedNodes = new HashMap<String, String>();
        importedCodesToFix = new HashSet<String>();
        importedNodeCodes=new HashSet<>();
    }

    public int getTotalSize() {
        return codesNotFound.size() + importedCodesToFix.size();
    }

    public Set<String> getCodesNotFound() {
        return codesNotFound;
    }

    public Map<String, String> getRenamedNodes() {
        return renamedNodes;
    }

    public void setRenamedNodes(Map<String, String> renamedNodes) {
        this.renamedNodes = renamedNodes;
    }

    public Set<String> getImportedCodesToFix() {
        return importedCodesToFix;
    }

    public FinATypeException getException() {
        return exception;
    }

    public void setException(FinATypeException exception) {
        this.exception = exception;
    }

    public Set<String> getImportedNodeCodes() {
        return importedNodeCodes;
    }

    public void setImportedNodeCodes(Set<String> importedNodeCodes) {
        this.importedNodeCodes = importedNodeCodes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
