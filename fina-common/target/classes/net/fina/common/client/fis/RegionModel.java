package net.fina.common.client.fis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class RegionModel implements Serializable {

    private long id;
    private String code;
    private String code1;
    private Integer version;
    private long sequence;
    private long nameStrId;
    private String name;
    private long parentId;
    private boolean deleted;

    private int level;

    public RegionModel() {
    }

    public RegionModel(long id, String code, long nameStrId, String name, long parentId) {
        this.id = id;
        this.code = code;
        this.nameStrId = nameStrId;
        this.name = name;
        this.parentId = parentId;
    }

    public boolean hasChildren(List<RegionModel> nodes) {
        for (RegionModel model : nodes) {
            if (this.id == model.parentId)
                return true;
        }
        return false;
    }

    public List<RegionModel> getChildren(List<RegionModel> nodes) {
        List<RegionModel> children = new ArrayList<RegionModel>();
        for (RegionModel model : nodes) {
            if (this.id == model.parentId)
                children.add(model);
        }
        return children;
    }

    public String getFullPathString(List<RegionModel> store) {
        StringBuilder sb = new StringBuilder();

        if (store != null) {
            RegionModel current = this;
            while (current.getParentId() != 0) {
                sb.insert(0, current.getName());
                sb.insert(0, " / ");
                current = getParent(store, current.parentId);
            }
        }
        return sb.toString();
    }

    private RegionModel getParent(List<RegionModel> store, long Id) {

        for (RegionModel regionModel : store) {
            if (regionModel.getId() == id) {
                return regionModel;
            }
        }
        return new RegionModel();
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

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
        RegionModel other = (RegionModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
