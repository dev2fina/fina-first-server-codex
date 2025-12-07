package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.List;

public class GroupBodyCreate implements BaseRepresentation {
    public final String id;
    public final String displayName;
    public final List<String> parentIds;

    public GroupBodyCreate(String id, String displayName, List<String> parentIds)
    {
        this.id = id;
        this.displayName = displayName;
        this.parentIds = parentIds;
    }


    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getParentIds() {
        return parentIds;
    }
}
