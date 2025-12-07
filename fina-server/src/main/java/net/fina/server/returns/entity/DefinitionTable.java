package net.fina.server.returns.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.mdt.entity.MDTNode;

import java.io.Serializable;

@Entity(name = "IN_DEFINITION_TABLES")
@Table(name = "IN_DEFINITION_TABLES")
@IdClass(DefinitionTableId.class)
@NamedQueries({
        @NamedQuery(name = "DT.findAll", query = "select dt from IN_DEFINITION_TABLES dt "),
        @NamedQuery(name = "DT.findAllNodeIds", query = "select dt.node.id from IN_DEFINITION_TABLES dt ")
})
public class DefinitionTable implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_definition_tables_sequence", sequenceName = "in_definition_tables_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_definition_tables_sequence")
    @Column(name = "ID")
    private long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "DEFINITIONID")
    private ReturnDefinition returnDefinition;

    @Column(name = "CODE")
    private String code;

    @OneToOne()
    @JoinColumn(name = "NODEID")
    private MDTNode node;

    @Column(name = "NODEVISIBLE")
    private boolean nodeVisible;

    @Column(name = "VISIBLELEVEL")
    private long visibleLevel;

    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ReturnTableType type;

    @Column(name = "EVALTYPE")
    @Enumerated(EnumType.ORDINAL)
    private MDTNodeEvalMethods evalType;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "SEQUENCE")
    private long sequence;

    @Transient
    private String nodeCode;

    public DefinitionTable() {
    }

    public DefinitionTable(long id, MDTNode node, ReturnTableType type) {
        this.id = id;
        this.node = node;
        this.type = type;
    }

    public DefinitionTable(long id, MDTNode node, ReturnTableType type, MDTNodeEvalMethods evalType) {
        this.id = id;
        this.node = node;
        this.type = type;
        this.evalType = evalType;
    }

    public DefinitionTable(long id, String code, MDTNode node, boolean nodeVisible, long visibleLevel, ReturnTableType type, MDTNodeEvalMethods evalType, Integer version) {
        this.id = id;
        this.code = code;
        this.node = node;
        this.nodeVisible = nodeVisible;
        this.visibleLevel = visibleLevel;
        this.type = type;
        this.evalType = evalType;
        this.version = version;
    }

    public DefinitionTable(String code, MDTNode node, long visibleLevel, ReturnTableType type, MDTNodeEvalMethods evalType, Integer version, long returnDefinitionId) {
        this.code = code;
        this.node = node;
        this.visibleLevel = visibleLevel;
        this.type = type;
        this.evalType = evalType;
        this.version = version;

        ReturnDefinition rd = new ReturnDefinition();
        rd.setId(returnDefinitionId);

        this.returnDefinition = rd;
    }

    public DefinitionTable(long id, String code, MDTNode node, boolean nodeVisible, long visibleLevel, ReturnTableType type, MDTNodeEvalMethods evalType, Integer version, long sequence) {
        this.id = id;
        this.code = code;
        this.node = node;
        this.nodeVisible = nodeVisible;
        this.visibleLevel = visibleLevel;
        this.type = type;
        this.evalType = evalType;
        this.version = version;
        this.sequence = sequence;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MDTNode getNode() {
        return node;
    }

    public void setNode(MDTNode node) {
        this.node = node;
    }

    public boolean isNodeVisible() {
        return nodeVisible;
    }

    public void setNodeVisible(boolean nodeVisible) {
        this.nodeVisible = nodeVisible;
    }

    public long getVisibleLevel() {
        return visibleLevel;
    }

    public void setVisibleLevel(long visibleLevel) {
        this.visibleLevel = visibleLevel;
    }

    public ReturnTableType getType() {
        return type;
    }

    public void setType(ReturnTableType type) {
        this.type = type;
    }

    public MDTNodeEvalMethods getEvalType() {
        return evalType;
    }

    public void setEvalType(MDTNodeEvalMethods evalType) {
        this.evalType = evalType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReturnDefinition getReturnDefinition() {
        return returnDefinition;
    }

    public void setReturnDefinition(ReturnDefinition returnDefinition) {
        this.returnDefinition = returnDefinition;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((returnDefinition == null) ? 0 : returnDefinition.hashCode());
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
        DefinitionTable other = (DefinitionTable) obj;
        if (id != other.id)
            return false;
        if (returnDefinition == null) {
            if (other.returnDefinition != null)
                return false;
        } else if (!returnDefinition.equals(other.returnDefinition))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DefinitionTable{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nodeVisible=" + nodeVisible +
                ", visibleLevel=" + visibleLevel +
                ", type=" + type +
                ", version=" + version +
                ", nodeCode='" + nodeCode + '\'' +
                ", evalType=" + evalType +
                ", node=" + node +
                '}';
    }
}
