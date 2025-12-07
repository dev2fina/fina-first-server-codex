package net.fina.server.i18n.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SysStringId implements Serializable {

	private long id;

	private long langId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLangId() {
		return langId;
	}

	public void setLangId(long langId) {
		this.langId = langId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (langId ^ (langId >>> 32));
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
		SysStringId other = (SysStringId) obj;
		if (id != other.id)
			return false;
		if (langId != other.langId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SysStringId [id=" + id + ", langId=" + langId + "]";
	}

}
