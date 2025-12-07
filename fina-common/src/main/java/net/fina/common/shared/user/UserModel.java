package net.fina.common.shared.user;

import net.fina.common.shared.AuthorizationType;
import net.fina.common.shared.ReturnVersionModel;
import net.fina.common.shared.UserType;
import net.fina.common.shared.ecm.model.ECMGroupMetaModel;
import net.fina.common.shared.mdt.MDTNodeModel;

import java.util.List;

public class UserModel extends UserModelSimple {

    private String titleDescription;
    private long titleDescriptionStrId;
    private String phone;
    private String email;
    private String contactPerson;
    private long contactPersonStrId;
    private boolean blocked;
    private boolean deleted;
    private boolean disabled;

    private String password;
    private boolean changePassword;

    private UserType userType = UserType.FINA_USER;
    private AuthorizationType authType = AuthorizationType.FINA;

    private List<Long> groupIds;
    private List<Long> fiIds;
    private List<Long> permissionIds;
    private List<Long> returnIds;
    private List<Long> reportIds;
    private List<MDTNodeModel> mdtNodes;
    private List<ReturnVersionModel> returnVersions;
    private List<ECMGroupMetaModel> ecmGroups;

    private boolean mustReLogin;
    private boolean passwordChanged;
    private String contactPersonPosition;
    private long contactPersonPositionStrId;
    private List<Long> permittedMatrixIds;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitleDescription() {
        return titleDescription;
    }

    public void setTitleDescription(String titleDescription) {
        this.titleDescription = titleDescription;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public long getDescriptionStrId() {
        return descriptionStrId;
    }

    public void setDescriptionStrId(long descriptionStrId) {
        this.descriptionStrId = descriptionStrId;
    }

    public long getTitleDescriptionStrId() {
        return titleDescriptionStrId;
    }

    public void setTitleDescriptionStrId(long titleDescriptionStrId) {
        this.titleDescriptionStrId = titleDescriptionStrId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public UserType getUserType() {
        return userType == null ? UserType.FINA_USER : userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public AuthorizationType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthorizationType authType) {
        this.authType = authType;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
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

    public List<Long> getReturnIds() {
        return returnIds;
    }

    public void setReturnIds(List<Long> returnIds) {
        this.returnIds = returnIds;
    }

    public List<Long> getReportIds() {
        return reportIds;
    }

    public void setReportIds(List<Long> reportIds) {
        this.reportIds = reportIds;
    }

    public List<MDTNodeModel> getMdtNodes() {
        return mdtNodes;
    }

    public void setMdtNodes(List<MDTNodeModel> mdtNodes) {
        this.mdtNodes = mdtNodes;
    }

    public List<ReturnVersionModel> getReturnVersions() {
        return returnVersions;
    }

    public void setReturnVersions(List<ReturnVersionModel> returnVersions) {
        this.returnVersions = returnVersions;
    }

    public long getContactPersonStrId() {
        return contactPersonStrId;
    }

    public void setContactPersonStrId(long contactPersonStrId) {
        this.contactPersonStrId = contactPersonStrId;
    }

    public List<ECMGroupMetaModel> getEcmGroups() {
        return ecmGroups;
    }

    public void setEcmGroups(List<ECMGroupMetaModel> ecmGroups) {
        this.ecmGroups = ecmGroups;
    }

    public boolean isMustReLogin() {
        return mustReLogin;
    }

    public void setMustReLogin(boolean mustReLogin) {
        this.mustReLogin = mustReLogin;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public String getContactPersonPosition() {
        return contactPersonPosition;
    }

    public void setContactPersonPosition(String contactPersonPosition) {
        this.contactPersonPosition = contactPersonPosition;
    }

    public long getContactPersonPositionStrId() {
        return contactPersonPositionStrId;
    }

    public void setContactPersonPositionStrId(long contactPersonPositionStrId) {
        this.contactPersonPositionStrId = contactPersonPositionStrId;
    }

    public List<Long> getPermittedMatrixIds() {
        return permittedMatrixIds;
    }

    public void setPermittedMatrixIds(List<Long> permittedMatrixIds) {
        this.permittedMatrixIds = permittedMatrixIds;
    }
}
