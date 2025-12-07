package net.fina.server.mdt.xml.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COMPARISON", propOrder = { "id", "nodeId", "equation", "condition" })
public class Comparison {

	@XmlElement(name = "ID")
	private long id;

	@XmlElement(name = "NODEID")
	private long nodeId;

	@XmlElement(name = "EQUATION")
	private String equation;

	@XmlElement(name = "CONDITION")
	private int condition;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

}
