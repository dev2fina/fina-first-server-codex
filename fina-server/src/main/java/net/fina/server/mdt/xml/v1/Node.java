package net.fina.server.mdt.xml.v1;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NODE", propOrder = { "id", "code", "parentId", "type", "dataType", "equation", "sequence", "evalMethod", "disabled", "required", "dependentNodes", "descriptions", "comparisons", "optional" })
public class Node {

	@XmlElement(name = "ID")
	private Long id;

	@XmlElement(name = "CODE")
	private String code;

	@XmlElement(name = "PARENTID")
	private Long parentId;

	@XmlElement(name = "TYPE")
	private Integer type;

	@XmlElement(name = "DATATYPE")
	private Integer dataType;

	@XmlElement(name = "EQUATION")
	private String equation;

	@XmlElement(name = "SEQUENCE")
	private Long sequence;

	@XmlElement(name = "EVALMETHOD")
	private Integer evalMethod;

	@XmlElement(name = "DISABLED")
	private Integer disabled;

	@XmlElement(name = "REQUIRED")
	private Integer required;

	@XmlElementWrapper(name = "DEPENDENT_NODES")
	@XmlElement(name = "ID")
	private List<Long> dependentNodes;

	@XmlElementWrapper(name = "DESCRIPTIONS")
	@XmlElement(name = "DESCRIPTION")
	private List<Description> descriptions;

	@XmlElementWrapper(name = "COMPARISONS")
	@XmlElement(name = "COMPARISON")
	private List<Comparison> comparisons;

	@XmlElement(name = "OPTIONAL", required = false)
	private Optional optional;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public int getEvalMethod() {
		return evalMethod;
	}

	public void setEvalMethod(int evalMethod) {
		this.evalMethod = evalMethod;
	}

	public int getDisabled() {
		return disabled;
	}

	public void setDisabled(int disabled) {
		this.disabled = disabled;
	}

	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
		this.required = required;
	}

	public Optional getOptional() {
		return optional;
	}

	public void setOptional(Optional optional) {
		this.optional = optional;
	}

	public List<Long> getDependentNodes() {
		return dependentNodes;
	}

	public void setDependentNodes(List<Long> dependentNodes) {
		this.dependentNodes = dependentNodes;
	}

	public List<Description> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<Description> descriptions) {
		this.descriptions = descriptions;
	}

	public List<Comparison> getComparisons() {
		return comparisons;
	}

	public void setComparisons(List<Comparison> comparisons) {
		this.comparisons = comparisons;
	}

}
