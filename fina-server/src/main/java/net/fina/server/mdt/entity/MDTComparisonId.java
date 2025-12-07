package net.fina.server.mdt.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MDTComparisonId implements Serializable {
	private long id;
	private MDTNode node;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MDTNode getNode() {
		return node;
	}

	public void setNode(MDTNode node) {
		this.node = node;
	}

	public MDTComparisonId() {
	}

	public MDTComparisonId(long id, MDTNode node) {
		this.id = id;
		this.node = node;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		MDTComparisonId other = (MDTComparisonId) obj;
		if (id != other.id)
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "MDTComparisonId{" +
                "id=" + id +
                '}';
    }
}
