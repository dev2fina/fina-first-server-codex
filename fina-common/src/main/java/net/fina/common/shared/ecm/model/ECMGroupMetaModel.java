package net.fina.common.shared.ecm.model;

import java.io.Serializable;
import java.util.List;

public class ECMGroupMetaModel implements Serializable, Comparable<ECMGroupMetaModel> {
    private String id;
    private String displayName;
    private Boolean isRoot;
    private List<String> parentIds;

    private boolean checked;

    public ECMGroupMetaModel() {
    }

    public ECMGroupMetaModel(String id, String displayName, Boolean isRoot, List<String> parentIds) {
        this.id = id;
        this.displayName = displayName;
        this.isRoot = isRoot;
        this.parentIds = parentIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getRoot() {
        return isRoot;
    }

    public void setRoot(Boolean root) {
        isRoot = root;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ECMGroupMetaModel))
            return false;
        ECMGroupMetaModel other = (ECMGroupMetaModel) o;
        return (this.getId() != null && other.getId() != null) && (this.getId().equals(other.getId()));
    }

    @Override
    public int compareTo(ECMGroupMetaModel ecmGroupMetaModel) {
        return this.id.compareToIgnoreCase(ecmGroupMetaModel.getId());
    }

}
