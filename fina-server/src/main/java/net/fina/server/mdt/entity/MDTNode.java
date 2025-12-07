package net.fina.server.mdt.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "IN_MDT_NODES")
@Table(name = "IN_MDT_NODES")
@NamedQueries({
        @NamedQuery(name = "MDT.findAll", query = "SELECT m FROM IN_MDT_NODES m "),
        @NamedQuery(name = "MDT.findAllCodes", query = "SELECT trim(n.code) FROM IN_MDT_NODES n"),
        @NamedQuery(name = "MDT.findChildrenIdsByParentId", query = "SELECT n.id FROM IN_MDT_NODES n WHERE n.parentId IN :parentIds"),
        @NamedQuery(name = "MDT.findByParentId", query = "SELECT n FROM IN_MDT_NODES n" /* left join fetch n.comparisons*/ + " where n.parentId IN :parentId " /* ORDER BY n.sequence ASC "*/),
        @NamedQuery(name = "MDT.findAllCodeAndDescription", query = "SELECT n.id,n.code,n.description  FROM IN_MDT_NODES n "),
        @NamedQuery(name = "MDT.findByParentIdEnabled", query = "SELECT n FROM IN_MDT_NODES n where n.parentId=:parentId AND n.disabled=:disabled "),
        @NamedQuery(name = "MDT.childrenCount", query = "SELECT count(n.id) FROM IN_MDT_NODES n where n.parentId=:parentId "),
        @NamedQuery(name = "MDT.findByCode", query = "SELECT n FROM IN_MDT_NODES n WHERE trim(LOWER(n.code))=trim(LOWER(:code))"),
        @NamedQuery(name = "MDT.checkCodeUnique", query = "SELECT n FROM IN_MDT_NODES n WHERE trim(n.code)=:code AND n.id<>:id "),
        @NamedQuery(name = "MDT.findByParentIdAndSequence", query = "SELECT m FROM IN_MDT_NODES m where m.parentId=:parentId AND m.sequence>=:nodeSequence ORDER BY m.sequence asc "),
        @NamedQuery(name = "MDT.checkByParentIdAndSequence", query = "SELECT m FROM IN_MDT_NODES m where m.parentId=:parentId AND m.sequence=:nodeSequence")
})
public class MDTNode implements Serializable, Audited {
    @Id
    @SequenceGenerator(name = "in_mdt_nodes_sequence", sequenceName = "in_mdt_nodes_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_mdt_nodes_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "CODE")
    private String code;

    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    @Column(name = "NAMESTRID")
    private Description description;

    @Column(name = "PARENTID")
    private long parentId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "TYPE")
    private MDTNodeTypes type;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "DATATYPE")
    private MDTNodeDataTypes dataType;

    @Column(name = "EQUATION")
    private String equation;

    @Column(name = "SEQUENCE")
    private long sequence;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "EVALMETHOD")
    private MDTNodeEvalMethods evalMethod;

    @Column(name = "DISABLED")
    private boolean disabled;

    @Column(name = "REQUIRED")
    private boolean required;

    @Column(name = "OPTLOCK")
    @Version
    private Integer version;

    @Column(name = "IS_KEY")
    private boolean key;

    @OneToMany(mappedBy = "node")
    @JsonIgnore
    private Collection<MDTComparison> comparisons = new ArrayList<MDTComparison>();

    @OneToMany
    @JoinTable(name = "IN_MDT_DEPENDENT_NODES", joinColumns = @JoinColumn(name = "DEPENDENTNODEID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "NODEID", referencedColumnName = "ID"))
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@dependentNodes", scope = MDTNode.class)
    private List<MDTNode> dependentNodes;

    @Column(name = "CATALOG")
    private boolean catalog;

    @Transient
    private List<MDTNode> children = new ArrayList<MDTNode>();

    @Transient
    private List<MDTNode> listElementNodes = new ArrayList<MDTNode>();

    @Transient
    private boolean damagedEquation;

    @Transient
    private Boolean canAmend;

    @Transient
    private long level1;

    public MDTNode() {
    }

    public MDTNode(long id) {
        this.id = id;
    }

    public MDTNode(long id, String code) {
        this.id = id;
        this.code = code;
    }

    public MDTNode(long id, MDTNodeTypes type) {
        this.id = id;
        this.type = type;
    }

    public MDTNode(long id, boolean canAmend) {
        this.id = id;
        this.canAmend = canAmend;
    }

    public MDTNode(long id, Integer version, long parentId, String code, Description description, MDTNodeTypes type, MDTNodeDataTypes dataType, String equation) {
        this(id, version, parentId, code, description, type);
        this.dataType = dataType;
        this.equation = equation;
    }

    public MDTNode(long id, Integer version, long parentId, String code, Description description, MDTNodeTypes type, MDTNodeDataTypes dataType, String equation, boolean key) {
        this(id, version, parentId, code, description, type, dataType, equation);
        this.key = key;
    }

    public MDTNode(long id, Integer version, long parentId, String code, Description description, MDTNodeTypes type) {
        this(id, version);
        this.parentId = parentId;
        this.code = code;
        this.description = description;
        this.dependentNodes = new ArrayList<>();
        this.type = type;
    }

    public MDTNode(long id, Integer version) {
        this.id = id;
        this.version = version;
    }

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

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
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

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public MDTNodeEvalMethods getEvalMethod() {
        return evalMethod;
    }

    public void setEvalMethod(MDTNodeEvalMethods evalMethod) {
        this.evalMethod = evalMethod;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public Collection<MDTComparison> getComparisons() {
        return comparisons;
    }

    public void setComparisons(List<MDTComparison> comparisons) {
        this.comparisons = comparisons;
    }

    public void setComparisons(Collection<MDTComparison> comparisons) {
        this.comparisons = comparisons;
    }

    public List<MDTNode> getChildren() {
        return children;
    }

    public void setChildren(List<MDTNode> children) {
        this.children = children;
    }

    public List<MDTNode> getDependentNodes() {
        return dependentNodes == null ? new ArrayList<>() : dependentNodes;
    }

    public void setDependentNodes(List<MDTNode> dependentNodes) {
        this.dependentNodes = dependentNodes;
    }

    public List<MDTNode> getListElementNodes() {
        return listElementNodes;
    }

    public void setListElementNodes(List<MDTNode> listElementNodes) {
        this.listElementNodes = listElementNodes;
    }

    public boolean isDamagedEquation() {
        return damagedEquation;
    }

    public void setDamagedEquation(boolean damagedEquation) {
        this.damagedEquation = damagedEquation;
    }

    public long getLevel1() {
        return level1;
    }

    public void setLevel1(long level1) {
        this.level1 = level1;
    }

    public boolean isCatalog() {
        return catalog;
    }

    public void setCatalog(boolean classifier) {
        this.catalog = classifier;
    }

    @Override
    public String toString() {
        return "MDTNode [id=" + id + ", code=" + code + ", parentId=" + parentId + ", type=" + type + ", dataType=" + dataType + ", equation=" + equation + ", sequence=" + sequence + ", evalMethod=" + evalMethod + ", disabled=" + disabled + ", required=" + required + ", classifier=" + catalog + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
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
        MDTNode other = (MDTNode) obj;
        return id == other.id;
    }

    public Boolean getCanAmend() {
        return canAmend;
    }

    public void setCanAmend(Boolean canAmend) {
        this.canAmend = canAmend;
    }
}
