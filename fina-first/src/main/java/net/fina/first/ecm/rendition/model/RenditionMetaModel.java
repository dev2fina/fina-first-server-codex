package net.fina.first.ecm.rendition.model;

import net.fina.ecm.alfresco.api.core.model.representation.ContentInfoRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.RenditionStatusEnum;

public class RenditionMetaModel {
    private String id;
    private ContentInfoRepresentation content;
    private RenditionStatusEnum status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContentInfoRepresentation getContent() {
        return content;
    }

    public void setContent(ContentInfoRepresentation content) {
        this.content = content;
    }

    public RenditionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RenditionStatusEnum status) {
        this.status = status;
    }
}
