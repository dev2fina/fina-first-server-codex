package net.fina.server.dcs.uploadfile.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SortInfo implements Serializable {

	private String sortField;
	private String sortDir;

	public SortInfo() {
	}

	public SortInfo(String sortField, String sortDir) {
		this.sortField = sortField;
		this.sortDir = sortDir;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}

}
