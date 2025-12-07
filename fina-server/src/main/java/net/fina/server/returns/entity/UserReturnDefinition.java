package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "SYS_USER_RETURNS")
@Table(name = "SYS_USER_RETURNS")
public class UserReturnDefinition implements Serializable, Audited {

    @EmbeddedId
    UserReturnDefinitionId userDefinition;

    public UserReturnDefinitionId getDefinitionId() {
        return userDefinition;
    }

    public void setDefinitionId(UserReturnDefinitionId userDefinition) {
        this.userDefinition = userDefinition;
    }

}
