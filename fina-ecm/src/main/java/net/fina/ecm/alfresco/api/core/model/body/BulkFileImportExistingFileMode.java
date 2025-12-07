package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public enum BulkFileImportExistingFileMode implements BaseRepresentation {
    SKIP,
    REPLACE,
    ADD_VERSION
}
