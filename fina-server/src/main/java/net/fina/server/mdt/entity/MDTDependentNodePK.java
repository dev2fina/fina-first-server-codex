package net.fina.server.mdt.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class MDTDependentNodePK implements Serializable {
	private long nodeId;
	private long dependentNodeId;

	public MDTDependentNodePK() {
	}

	public MDTDependentNodePK(long nodeId, long dependentNodeId) {
		this.nodeId = nodeId;
		this.dependentNodeId = dependentNodeId;
	}

	@Column(name = "NODEID")
	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	@Column(name = "DEPENDENTNODEID")
	public long getDependentNodeId() {
		return dependentNodeId;
	}

	public void setDependentNodeId(long dependentNodeId) {
		this.dependentNodeId = dependentNodeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dependentNodeId ^ (dependentNodeId >>> 32));
		result = prime * result + (int) (nodeId ^ (nodeId >>> 32));
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
		MDTDependentNodePK other = (MDTDependentNodePK) obj;
		if (dependentNodeId != other.dependentNodeId)
			return false;
		if (nodeId != other.nodeId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MDTDependentNodePK [nodeId=" + nodeId + ", dependentNodeId=" + dependentNodeId + "]";
	}

}