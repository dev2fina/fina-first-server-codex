package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.GroupMemberRepresentation;

public class GroupMembershipBodyCreate implements BaseRepresentation {
    public String id;
    public GroupMemberRepresentation.MemberTypeEnum memberType;

    public GroupMembershipBodyCreate(String id, GroupMemberRepresentation.MemberTypeEnum memberType) {
        this.id = id;
        this.memberType = memberType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GroupMemberRepresentation.MemberTypeEnum getMemberType() {
        return memberType;
    }

    public void setMemberType(GroupMemberRepresentation.MemberTypeEnum memberType) {
        this.memberType = memberType;
    }
}
