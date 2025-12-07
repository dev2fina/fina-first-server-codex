package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class ChildAssociationBody implements BaseRepresentation {
    public final String childId;

    public final String assocType;

    public ChildAssociationBody(String childId)
    {
        this.childId = childId;
        this.assocType = null;
    }

    public ChildAssociationBody(String childId, String assocType)
    {
        this.childId = childId;
        this.assocType = assocType;
    }
}
