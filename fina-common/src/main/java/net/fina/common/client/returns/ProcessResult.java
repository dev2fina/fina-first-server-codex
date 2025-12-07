package net.fina.common.client.returns;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProcessResult implements Serializable {
    private ProcessStatus status;
    private String processNote;
    private String returnDefinitionCode;
    private long returnId;
    private boolean validatePostProcessComparisons;

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public String getProcessNote() {
        return processNote;
    }

    public void setProcessNote(String processNote) {
        this.processNote = processNote;
    }

    public String getReturnDefinitionCode() {
        return returnDefinitionCode;
    }

    public void setReturnDefinitionCode(String returnDefinitionCode) {
        this.returnDefinitionCode = returnDefinitionCode;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public boolean validatePostProcessComparisons() {
        return validatePostProcessComparisons;
    }

    public void setValidatePostProcessComparisons(boolean validatePostProcessComparisons) {
        this.validatePostProcessComparisons = validatePostProcessComparisons;
    }
}