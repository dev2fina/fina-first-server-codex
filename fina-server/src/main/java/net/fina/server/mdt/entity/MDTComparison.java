package net.fina.server.mdt.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.mdt.MDTComparisonConditions;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.shared.mdt.ProcessStage;
import net.fina.server.i18n.helper.Description;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "IN_MDT_COMPARISON")
@Table(name = "IN_MDT_COMPARISON")
@IdClass(MDTComparisonId.class)
@SuppressWarnings("serial")
@NamedQueries({
        @NamedQuery(name = "MDTComparison.findAllByNodeIds", query = "SELECT c FROM IN_MDT_COMPARISON c WHERE c.node.id in :nodeIds "),
        @NamedQuery(name = "MDTComparison.findByNodeAndId", query = "SELECT c FROM IN_MDT_COMPARISON c WHERE c.id = :id AND c.node.id = :nodeId ")
})
public class MDTComparison implements Serializable, Audited {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "in_mdt_comparison_sequence", sequenceName = "in_mdt_comparison_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_mdt_comparison_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "NODEID")
    private MDTNode node;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "CONDITION")
    private MDTComparisonConditions condition;

    @Column(name = "LEFT_EQUATION")
    private String leftEquation;

    @Column(name = "RIGHT_EQUATION")
    private String rightEquation;

    @Column(name = "TEMPLATE")
    private String template;

    @Column(name = "NUMBER_PATTERN")
    private String numberPattern;

    @Column(name = "OPTLOCK")
    @Version
    private Integer version;

    @Column(name = "PROCESS_STAGE")
    @Enumerated(EnumType.ORDINAL)
    private ProcessStage processStage = ProcessStage.DEFAULT;
    @Transient
    private long rowNumber;

    @Transient
    private String value;

    public MDTComparison() {
    }

    public MDTComparison(long id, Integer version, MDTComparisonConditions condition, String rightEquation, String template, String numberPattern, long nodeId, Integer nodeVersion, String nodeCode, Description nodeDescription, MDTNodeTypes nodeType, boolean disabled, ProcessStage processStage) {
        this(id, version, condition, "", rightEquation, template, numberPattern, nodeId, nodeVersion, nodeCode, nodeDescription, nodeType, disabled, processStage);
    }

    public MDTComparison(long id, Integer version, MDTComparisonConditions condition, String leftEquation, String rightEquation, String template, String numberPattern, long nodeId, Integer nodeVersion, String nodeCode, Description nodeDescription, MDTNodeTypes nodeType, boolean disabled, ProcessStage processStage) {
        this.id = id;
        this.version = version;
        this.condition = condition;
        this.leftEquation = leftEquation;
        this.rightEquation = rightEquation;
        this.template = template;
        this.numberPattern = numberPattern;
        this.processStage = processStage;

        MDTNode node = new MDTNode();
        node.setId(nodeId);
        node.setVersion(nodeVersion);
        node.setCode(nodeCode);
        node.setDescription(nodeDescription);
        node.setType(nodeType);
        node.setDisabled(disabled);

        this.node = node;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MDTComparisonConditions getCondition() {
        return condition;
    }

    public void setCondition(MDTComparisonConditions condition) {
        this.condition = condition;
    }

    public String getLeftEquation() {
        return leftEquation;
    }

    public void setLeftEquation(String firstEquation) {
        this.leftEquation = firstEquation;
    }

    public String getRightEquation() {
        return rightEquation;
    }

    public void setRightEquation(String equation) {
        this.rightEquation = equation;
    }

    public MDTNode getNode() {
        return node;
    }

    public void setNode(MDTNode node) {
        this.node = node;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(long rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getNumberPattern() {
        return numberPattern;
    }

    public void setNumberPattern(String numberPattern) {
        this.numberPattern = numberPattern;
    }

    public ProcessStage getProcessStage() {
        return processStage;
    }

    public void setProcessStage(ProcessStage processStage) {
        this.processStage = processStage;
    }

    @Override
    public String toString() {
        return "MDTComparison [id=" + id + ", condition=" + condition + ", equation=" + rightEquation + ", node=" + node + ", version=" + version + "]";
    }

}
