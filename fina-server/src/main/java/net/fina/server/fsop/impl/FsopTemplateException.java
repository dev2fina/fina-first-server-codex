package net.fina.server.fsop.impl;


import net.fina.server.fsop.model.FsopImportedReturnMetaModel;

public class FsopTemplateException extends RuntimeException {
    private final FsopImportedReturnMetaModel importedReturn;

    public FsopTemplateException(FsopImportedReturnMetaModel importedReturn, String message) {
        super(message);
        this.importedReturn = importedReturn;
    }

    public FsopImportedReturnMetaModel getImportedReturn() {
        return importedReturn;
    }
}
