package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;


public class DownloadRepresentation extends AbstractRepresentation {
    private DownloadEntry entry;

    public DownloadRepresentation() {
    }

    public DownloadRepresentation(DownloadEntry entry) {
        this.entry = entry;
    }

    public DownloadEntry getEntry() {
        return entry;
    }

    public void setEntry(DownloadEntry entry) {
        this.entry = entry;
    }
}
