package net.fina.common.shared.user;

import net.fina.common.shared.ReturnVersionModel;
import net.fina.common.shared.mdt.MDTNodeModel;

import java.util.List;

public class RoleModel {
    private long id;
    private String code;
    private String description;
    private long descriptionStrId;
    private List<UserModel> users;

    private long userCount;

    private List<Long> fiIds;
    private List<Long> permissionIds;
    private List<Long> returnDefintionIds;
    private List<Long> reportIds;
    private List<ReturnVersionModel> returnVersions;
    private List<MDTNodeModel> mdtNodes;
    private List<Long> permittedMatrixIds;

    private boolean mustReLogin;

    public RoleModel() {
    }

    public RoleModel(long id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public RoleModel(long id, String code, String description, long descriptionStrId) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.descriptionStrId = descriptionStrId;
    }

    public RoleModel(long id, String code, String description, long descriptionStrId, int userCount) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.descriptionStrId = descriptionStrId;
        this.userCount = userCount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public long getDescriptionStrId() {
        return descriptionStrId;
    }

    public void setDescriptionStrId(long descriptionStrId) {
        this.descriptionStrId = descriptionStrId;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public List<Long> getFiIds() {
        return fiIds;
    }

    public void setFiIds(List<Long> fiIds) {
        this.fiIds = fiIds;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public List<Long> getReturnDefintionIds() {
        return returnDefintionIds;
    }

    public void setReturnDefintionIds(List<Long> returnDefintionIds) {
        this.returnDefintionIds = returnDefintionIds;
    }

    public List<Long> getReportIds() {
        return reportIds;
    }

    public void setReportIds(List<Long> reportIds) {
        this.reportIds = reportIds;
    }

    public List<ReturnVersionModel> getReturnVersions() {
        return returnVersions;
    }

    public void setReturnVersions(List<ReturnVersionModel> returnVersions) {
        this.returnVersions = returnVersions;
    }

    public List<MDTNodeModel> getMdtNodes() {
        return mdtNodes;
    }

    public void setMdtNodes(List<MDTNodeModel> mdtNodes) {
        this.mdtNodes = mdtNodes;
    }

    public boolean isMustReLogin() {
        return mustReLogin;
    }

    public void setMustReLogin(boolean mustReLogin) {
        this.mustReLogin = mustReLogin;
    }

    public List<Long> getPermittedMatrixIds() {
        return permittedMatrixIds;
    }

    public void setPermittedMatrixIds(List<Long> permittedMatrixIds) {
        this.permittedMatrixIds = permittedMatrixIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleModel roleModel = (RoleModel) o;
        return getId() == roleModel.getId();
    }

    @Override
    public int hashCode() {
        return (int) getId();
    }
}
