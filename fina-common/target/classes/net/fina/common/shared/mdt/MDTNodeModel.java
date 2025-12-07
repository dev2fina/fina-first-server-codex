package net.fina.common.shared.mdt;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.mdt.MDTPermissionType;

import java.io.Serializable;
import java.util.Map;

import static net.fina.common.client.util.SafeValueUtil.getTrimmedValueSafe;
import static net.fina.common.client.util.SafeValueUtil.getValueSafe;

public class MDTNodeModel implements Serializable {
    private long id;

    private Integer version;

    private String code;

    // Description nameStrId
    private long nameStrId;
    private String name;
    private Map<Long, String> descriptions;
    private long parentId;

    private MDTNodeTypes type;

    private MDTNodeDataTypes dataType;

    private String equation;

    private long sequence;

    private MDTNodeEvalMethods evalMethod;

    private boolean disabled;

    private boolean required;

    private boolean damaged;

    private boolean canUserReview;

    private boolean canUserAmend;

    private MDTPermissionType permissionType;

    private boolean fromRole;
    private boolean catalog;

    private boolean key;

    public MDTNodeModel() {
        super();
        // TODO Auto-generated constructor stub
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return getValueSafe(version);
    }

    public void setVersion(Integer version) {
        this.version = getValueSafe(version);
    }

    public String getCode() {
        return getTrimmedValueSafe(code);
    }

    public void setCode(String code) {
        this.code = getTrimmedValueSafe(code);
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getName() {
        return getTrimmedValueSafe(name);
    }

    public void setName(String name) {
        this.name = getTrimmedValueSafe(name);
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
        return getTrimmedValueSafe(equation);
    }

    public void setEquation(String equation) {
        this.equation = getTrimmedValueSafe(equation);
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

    public boolean isDamaged() {
        return damaged;
    }

    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }

    public boolean isCanUserReview() {
        return canUserReview;
    }

    public void setCanUserReview(boolean canUserReview) {
        this.canUserReview = canUserReview;
    }

    public boolean isCanUserAmend() {
        return canUserAmend;
    }

    public void setCanUserAmend(boolean canUserAmend) {
        this.canUserAmend = canUserAmend;
    }

    public MDTPermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(MDTPermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public boolean isFromRole() {
        return fromRole;
    }

    public void setFromRole(boolean fromRole) {
        this.fromRole = fromRole;
    }

    public boolean isCatalog() {
        return catalog;
    }

    public void setCatalog(boolean catalog) {
        this.catalog = catalog;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public Map<Long, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<Long, String> descriptions) {
        this.descriptions = descriptions;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MDTNodeModel that = (MDTNodeModel) o;

        if (id != that.id) return false;
        if (!code.equals(that.code)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + code.hashCode();
        return result;
    }
}
