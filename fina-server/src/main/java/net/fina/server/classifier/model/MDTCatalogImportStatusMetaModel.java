package net.fina.server.classifier.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MDTCatalogImportStatusMetaModel {
    private List<String> errors;
    private List<String> warnings;

    public MDTCatalogImportStatusMetaModel() {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
