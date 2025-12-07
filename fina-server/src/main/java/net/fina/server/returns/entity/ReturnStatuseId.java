package net.fina.server.returns.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ReturnStatuseId implements Serializable {

	private long id;

	private Return returns;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Return getReturns() {
		return returns;
	}

	public void setReturns(Return returns) {
		this.returns = returns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((returns == null) ? 0 : returns.hashCode());
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
		ReturnStatuseId other = (ReturnStatuseId) obj;
		if (id != other.id)
			return false;
		if (returns == null) {
			if (other.returns != null)
				return false;
		} else if (!returns.equals(other.returns))
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "ReturnStatuseId{" +
                "id=" + id +
                ", returns=" + returns +
                '}';
    }
}
