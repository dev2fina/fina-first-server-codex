package net.fina.server.returns.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class UserReturnDefinitionId implements Serializable {

	@Column(name = "USER_ID")
	private long userId;

	@Column(name = "DEFINITION_ID")
	private long definitionId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(long definitionId) {
		this.definitionId = definitionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (definitionId ^ (definitionId >>> 32));
		result = prime * result + (int) (userId ^ (userId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserReturnDefinitionId other = (UserReturnDefinitionId) obj;
		if (definitionId != other.definitionId)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "UserReturnDefinitionId{" +
                "userId=" + userId +
                ", definitionId=" + definitionId +
                '}';
    }
}
