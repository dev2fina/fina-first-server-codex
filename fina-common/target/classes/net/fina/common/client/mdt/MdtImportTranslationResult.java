package net.fina.common.client.mdt;

import net.fina.common.client.exception.FinATypeException;

import java.util.HashSet;
import java.util.Set;

public class MdtImportTranslationResult {
    private final Set<String> nonExistingNodes;
    private final Set<String> nonExistingLanguageCodes;
    private FinATypeException exception;

    private String exceptionMessage;


    public MdtImportTranslationResult() {
        nonExistingNodes = new HashSet<>();
        nonExistingLanguageCodes = new HashSet<>();
    }

    public Set<String> getNonExistingNodes() {
        return nonExistingNodes;
    }

    public Set<String> getNonExistingLanguageCodes() {
        return nonExistingLanguageCodes;
    }

    public FinATypeException getException() {
        return exception;
    }

    public void setException(FinATypeException exception) {
        this.exception = exception;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

}
