package net.fina.server.returns.entity;

import java.io.Serializable;
import java.util.Collection;

import jakarta.persistence.*;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;

import org.hibernate.annotations.Type;

@Entity(name = "IN_RETURN_DEFINITIONS")
@Table(name = "IN_RETURN_DEFINITIONS")
@NamedQueries({
        @NamedQuery(name = "ReturnDefinition.findAll", query = "select d from IN_RETURN_DEFINITIONS as d "),
        @NamedQuery(name = "ReturnDefinition.findByCode", query = "select rd from IN_RETURN_DEFINITIONS as rd where trim(rd.code)=:returnDefinitionCode ")})
public class ReturnDefinition implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_return_definitions_sequence", sequenceName = "in_return_definitions_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_return_definitions_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE", length = 12, nullable = false, unique = true)
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @OneToOne()
    @JoinColumn(name = "TYPEID")
    private ReturnType returnType;

    @OneToMany()
    @JoinColumn(name = "DEFINITIONID", referencedColumnName = "ID")
    private Collection<DefinitionTable> definitionTables;

    @JoinColumn(name = "DISABLE")
    private Boolean disable;

    @Column(name = "MANUALINPUT")
    private boolean manualInput;

    @Column(name = "GENERAL_INFO")
    private String generalInfo;

    @Transient
    private long nodeId;

    public ReturnDefinition() {
    }

    public ReturnDefinition(long id) {
        this.id = id;
    }

    public ReturnDefinition(long id, Integer version) {
        this.id = id;
        this.version = version;
    }

    public ReturnDefinition(long id, String code) {
        this.id = id;
        this.code = code;
    }

    public ReturnDefinition(long id, Integer version, String code, Description description) {
        this(id, version);
        this.code = code;
        this.description = description;
    }

    public ReturnDefinition(long id, String code, Description description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public ReturnDefinition(long id, String code, Collection<DefinitionTable> definitionTables) {
        this.id = id;
        this.code = code;
        this.definitionTables = definitionTables;
    }

    public ReturnDefinition(long id, Integer version, String code, Description description, long typeId, String typeCode, Description typeDescription, long nodeId) {
        this(id, version, code, description);
        ReturnType type = new ReturnType();
        type.setId(typeId);
        type.setCode(typeCode);
        type.setDescription(typeDescription);
        this.setReturnType(type);
        this.nodeId = nodeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public Collection<DefinitionTable> getDefinitionTables() {
        return definitionTables;
    }

    public void setDefinitionTables(Collection<DefinitionTable> definitionTables) {
        this.definitionTables = definitionTables;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public boolean isManualInput() {
        return manualInput;
    }

    public void setManualInput(boolean manualInput) {
        this.manualInput = manualInput;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public String getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(String generalInfo) {
        this.generalInfo = generalInfo;
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
        ReturnDefinition other = (ReturnDefinition) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return code;
    }
}
