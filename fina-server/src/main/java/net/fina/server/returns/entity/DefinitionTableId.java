package net.fina.server.returns.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DefinitionTableId implements Serializable {

	private long id;

	private ReturnDefinition returnDefinition;

	public DefinitionTableId() {
	}

	public DefinitionTableId(long id, ReturnDefinition returnDefinition) {
		this.id = id;
		this.returnDefinition = returnDefinition;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ReturnDefinition getReturnDefinition() {
		return returnDefinition;
	}

	public void setReturnDefinition(ReturnDefinition returnDefinition) {
		this.returnDefinition = returnDefinition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((returnDefinition == null) ? 0 : returnDefinition.hashCode());
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
		DefinitionTableId other = (DefinitionTableId) obj;
		if (id != other.id)
			return false;
		if (returnDefinition == null) {
			if (other.returnDefinition != null)
				return false;
		} else if (!returnDefinition.equals(other.returnDefinition))
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "DefinitionTableId{" +
                "id=" + id +
                '}';
    }
}
