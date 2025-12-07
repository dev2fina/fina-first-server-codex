package net.fina.common.client.mdt;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MDTDependency implements Serializable {

	private Long id;
	private String code;
	private String Description;
	private String usedBy;

	private MDTNodeTypes type;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getUsedBy() {
		return usedBy;
	}

	public void setUsedBy(String usedBy) {
		this.usedBy = usedBy;
	}

	public MDTNodeTypes getType() {
		return type;
	}

	public void setType(MDTNodeTypes type) {
		this.type = type;
	}
}
