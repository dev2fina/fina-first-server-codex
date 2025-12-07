package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;


@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMemberRepresentation extends AbstractRepresentation {
    private String id = null;
    private String displayName = null;

    /**
     * Gets or Sets memberType
     */
    public enum MemberTypeEnum {
        PERSON("PERSON"),

        GROUP("GROUP");

        private String value;

        MemberTypeEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    private MemberTypeEnum memberType = null;

    /**
     * Get id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get displayName
     *
     * @return displayName
     **/
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get memberType
     *
     * @return memberType
     **/
    public MemberTypeEnum getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberTypeEnum memberType) {
        this.memberType = memberType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupMemberRepresentation groupMember = (GroupMemberRepresentation) o;
        return Objects.equals(this.id, groupMember.id) && Objects.equals(this.displayName, groupMember.displayName)
                && Objects.equals(this.memberType, groupMember.memberType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName, memberType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GroupMember , ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    displayName: ").append(toIndentedString(displayName)).append(", ");
        sb.append("    memberType: ").append(toIndentedString(memberType)).append(" ");
        sb.append("}");
        return sb.toString();
    }
}
