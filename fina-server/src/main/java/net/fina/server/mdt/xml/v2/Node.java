package net.fina.server.mdt.xml.v2;

import jakarta.xml.bind.annotation.*;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTNode;

import java.util.*;

@XmlRootElement(name = "NODE")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NODE")
public class Node {

    public static long nullCounter = 0;
    @XmlAttribute(name = "CODE")
    private String code;
    //Bind custom type
    @XmlAttribute(name = "TYPE")
    private MDTNodeTypes type;
    //Bind custom type
    @XmlAttribute(name = "DATATYPE")
    private MDTNodeDataTypes dataType;
    @XmlAttribute(name = "EQUATION")
    private String equation;
    @XmlAttribute(name = "SEQUENCE")
    private Long sequence;
    @XmlAttribute(name = "EVALMETHOD")
    private MDTNodeEvalMethods evalMethod;
    @XmlAttribute(name = "DISABLED")
    private Boolean disabled;
    @XmlAttribute(name = "REQUIRED")
    private Boolean required;
    @XmlElementWrapper(name = "CHILDREN")
    @XmlElement(name = "NODE")
    private List<Node> children;
    @XmlElementWrapper(name = "DEPENDENT_NODES")
    @XmlElement(name = "CODE")
    private Collection<String> dependentNodeCodes;
    @XmlElementWrapper(name = "DESCRIPTIONS")
    @XmlElement(name = "DESCRIPTION")
    private Collection<Description> descriptions;
    @XmlElementWrapper(name = "COMPARISONS")
    @XmlElement(name = "COMPARISON")
    private Collection<Comparison> comparisons;
    @XmlElement(name = "OPTIONAL", required = false)
    private Optional optional;
    @XmlAttribute(name = "PARENT_ID")
    private long parentId;

    public Node() {
    }

    public Node(MDTNode node, Map<Long, String> languages, Map<Long, List<MDTComparison>> comparisonsMap) {
        setCode(node.getCode().trim());
        for (Map.Entry<Long, String> entry : node.getDescription().getDescriptions().entrySet()) {
            String langCode = languages.get(entry.getKey());
            if (langCode != null) {
                getDescriptions().add(new Description(langCode, entry.getValue()));
            }
        }

        setType(node.getType());
        setDataType(node.getDataType());

        setEquation(node.getEquation());
        setSequence(node.getSequence());
        setEvalMethod(node.getEvalMethod());

        setDisabled(node.isDisabled());
        setRequired(node.isRequired());

        Collection<Node> children = getChildren();
        for (MDTNode child : node.getChildren()) {
            children.add(new Node(child, languages, comparisonsMap));
        }

        List<MDTComparison> comparisons = comparisonsMap.get(node.getId());
        if (comparisons != null) {
            for (MDTComparison comparison : comparisons) {
                getComparisons().add(new Comparison(comparison.getLeftEquation(), comparison.getRightEquation(), comparison.getCondition(), comparison.getTemplate(), comparison.getNumberPattern()));
            }
        }
    }

    public MDTNode asMDTNode(Map<String, Long> languageCodeIdMap, long defaultLanguageId, Map<Long, List<MDTComparison>> comparisonsMap) {
        MDTNode mdtNode = new MDTNode();
        mdtNode.setCode(getCode().trim());

        boolean saved = false;
        net.fina.server.i18n.helper.Description d = new net.fina.server.i18n.helper.Description();
        for (Description description : getDescriptions()) {
            Long langId = languageCodeIdMap.get(description.getLangCode().trim());
            if (langId != null) {
                d.addDescription(langId, description.getValue());
                saved = true;
            }
        }
        if (!saved) {
            d.addDescription(defaultLanguageId, "UNKNOWN");
        }
        mdtNode.setDescription(d);

        mdtNode.setType(getType());
        mdtNode.setDataType(getDataType());

        mdtNode.setEquation(getEquation());
        mdtNode.setSequence(getSequence());
        mdtNode.setEvalMethod(getEvalMethod());

        mdtNode.setDisabled(isDisabled());
        mdtNode.setRequired(isRequired());
        return mdtNode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MDTNodeTypes getType() {
        return type;
    }

    public void setType(MDTNodeTypes type) {
        this.type = type;
    }

    public MDTNodeDataTypes getDataType() {
        return dataType;
    }

    public void setDataType(MDTNodeDataTypes dataType) {
        this.dataType = dataType;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public MDTNodeEvalMethods getEvalMethod() {
        return evalMethod;
    }

    public void setEvalMethod(MDTNodeEvalMethods evalMethod) {
        this.evalMethod = evalMethod;
    }

    public Boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public List<Node> getChildren() {
        if (children == null) children = new ArrayList<>();
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Collection<String> getDependentNodeCodes() {
        if (dependentNodeCodes == null) dependentNodeCodes = new ArrayList<>();
        return dependentNodeCodes;
    }

    public void setDependentNodeCodes(Collection<String> dependentNodeCodes) {
        this.dependentNodeCodes = dependentNodeCodes;
    }

    public Collection<Description> getDescriptions() {
        if (descriptions == null) descriptions = new ArrayList<>();
        return descriptions;
    }

    public void setDescriptions(Description... descriptions) {
        this.descriptions = Arrays.asList(descriptions);
    }

    public Collection<Comparison> getComparisons() {
        if (comparisons == null) comparisons = new ArrayList<>();
        return comparisons;
    }

    public void setComparisons(Collection<Comparison> comparisons) {
        this.comparisons = comparisons;
    }

    public Optional getOptional() {
        return optional;
    }

    public void setOptional(Optional optional) {
        this.optional = optional;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
}
