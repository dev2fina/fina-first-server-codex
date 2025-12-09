package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class NodeBodyCreateAssociation implements BaseRepresentation {
    public final String assocType;

    public NodeBodyCreateAssociation(String assocType)
    {
        this.assocType = assocType;
    }
}
