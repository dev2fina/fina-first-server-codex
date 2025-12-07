package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class TagBody implements BaseRepresentation {
    public final String tag;

    public TagBody(String tag) {
        this.tag = tag;
    }
}
