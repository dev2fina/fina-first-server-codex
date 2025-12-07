package net.fina.server.returns.entity;

import java.io.Serializable;

import net.fina.server.mdt.entity.MDTNode;

@SuppressWarnings("serial")
public class ReturnItemId implements Serializable {

	private long id;

	private Return returns;

	private MDTNode mdtNode;

	private ReturnVersion returnVersion;

	private long tableId;

	private long rowNumber;

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

	public MDTNode getMdtNode() {
		return mdtNode;
	}

	public void setMdtNode(MDTNode mdtNode) {
		this.mdtNode = mdtNode;
	}

	public ReturnVersion getReturnVersion() {
		return returnVersion;
	}

	public void setReturnVersion(ReturnVersion returnVersion) {
		this.returnVersion = returnVersion;
	}

	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	public long getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(long rowNumber) {
		this.rowNumber = rowNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((mdtNode == null) ? 0 : mdtNode.hashCode());
		result = prime * result + ((returnVersion == null) ? 0 : returnVersion.hashCode());
		result = prime * result + ((returns == null) ? 0 : returns.hashCode());
		result = prime * result + (int) (rowNumber ^ (rowNumber >>> 32));
		result = prime * result + (int) (tableId ^ (tableId >>> 32));
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
		ReturnItemId other = (ReturnItemId) obj;
		if (id != other.id)
			return false;
		if (mdtNode == null) {
			if (other.mdtNode != null)
				return false;
		} else if (!mdtNode.equals(other.mdtNode))
			return false;
		if (returnVersion == null) {
			if (other.returnVersion != null)
				return false;
		} else if (!returnVersion.equals(other.returnVersion))
			return false;
		if (returns == null) {
			if (other.returns != null)
				return false;
		} else if (!returns.equals(other.returns))
			return false;
		if (rowNumber != other.rowNumber)
			return false;
		if (tableId != other.tableId)
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "ReturnItemId{" +
                "id=" + id +
                ", returns=" + returns +
                ", mdtNode=" + mdtNode +
                ", returnVersion=" + returnVersion +
                ", tableId=" + tableId +
                ", rowNumber=" + rowNumber +
                '}';
    }
}
