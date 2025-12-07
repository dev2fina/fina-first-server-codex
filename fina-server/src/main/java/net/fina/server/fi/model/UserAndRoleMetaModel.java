package net.fina.server.fi.model;

import net.fina.common.client.returns.ReturnDefinitionModel;
import net.fina.common.shared.AuthorizationType;
import net.fina.common.shared.UserType;
import net.fina.common.shared.ecm.model.ECMGroupMetaModel;
import net.fina.common.shared.mdt.MDTNodeModel;

import java.util.Date;
import java.util.List;

public class UserAndRoleMetaModel {
    private static int COUNTER;
    private long id;
    private Integer version;
    private String login;
    private String password;
    private boolean changePassword;
    private String name;
    private long nameStrId;
    private String title;
    private long titleStrId;
    private String contactPerson;
    private long contactPersonStrId;
    private String phone;
    private String email;
    private boolean blocked;
    private Date lastLoginDate;
    private Date lastPasswordChangeDate;

    private Type type;

    private List<UserAndRoleMetaModel> groupModels;
    private List<Long> fiIdModels;
    private List<PermissionMetaModel> permissionsModels;
    private List<ReturnDefinitionModel> returnModels;
    private List<ReportMetaModel> reportModels;
    private List<ReturnVersionMetaModel> versionModels;
    private List<MDTNodeModel> mdtModels;
    private List<Long> groupUserIds;


    private boolean halfChecked;
    private boolean permitted;
    private boolean inGroup;
    private boolean passwordChanged;
    private boolean mustReLogin;
    private int innerId;
    private UserType userType = UserType.FINA_USER;
    private AuthorizationType authType = AuthorizationType.FINA;
    private List<ECMGroupMetaModel> ecmGroups;
    private boolean deleted;
    private boolean disabled;
    public UserAndRoleMetaModel() {
        super();
        innerId = COUNTER++;
    }

    public UserAndRoleMetaModel(String login, Type type) {
        this.login = login;
        this.type = type;
        innerId = COUNTER++;
    }

    public int getInnerId() {
        return innerId;
    }

    public void setInnerId(int innerId) {
        this.innerId = innerId;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTitleStrId() {
        return titleStrId;
    }

    public void setTitleStrId(long titleStrId) {
        this.titleStrId = titleStrId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public long getContactPersonStrId() {
        return contactPersonStrId;
    }

    public void setContactPersonStrId(long contactPersonStrId) {
        this.contactPersonStrId = contactPersonStrId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastPasswordChangeDate() {
        return lastPasswordChangeDate;
    }

    public void setLastPasswordChangeDate(Date lastPasswordChangeDate) {
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isInGroup() {
        return inGroup;
    }

    public void setInGroup(boolean inGroup) {
        this.inGroup = inGroup;
    }

    public List<UserAndRoleMetaModel> getGroupModels() {
        return groupModels;
    }

    public void setGroupModels(List<UserAndRoleMetaModel> groupModels) {
        this.groupModels = groupModels;
    }

    public List<Long> getFiIdModels() {
        return fiIdModels;
    }

    public void setFiIdModels(List<Long> fiIdModels) {
        this.fiIdModels = fiIdModels;
    }

    public List<PermissionMetaModel> getPermissionsModels() {
        return permissionsModels;
    }

    public void setPermissionsModels(List<PermissionMetaModel> permissionsModels) {
        this.permissionsModels = permissionsModels;
    }

    public List<ReturnDefinitionModel> getReturnModels() {
        return returnModels;
    }

    public void setReturnModels(List<ReturnDefinitionModel> returnModels) {
        this.returnModels = returnModels;
    }

    public List<ReportMetaModel> getReportModels() {
        return reportModels;
    }

    public void setReportModels(List<ReportMetaModel> reportModels) {
        this.reportModels = reportModels;
    }

    public List<ReturnVersionMetaModel> getVersionModels() {
        return versionModels;
    }

    public void setVersionModels(List<ReturnVersionMetaModel> versionModels) {
        this.versionModels = versionModels;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public boolean isMustReLogin() {
        return mustReLogin;
    }

    public void setMustReLogin(boolean mustReLogin) {
        this.mustReLogin = mustReLogin;
    }

    public boolean isHalfChecked() {
        return halfChecked;
    }

    public void setHalfChecked(boolean halfChecked) {
        this.halfChecked = halfChecked;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }

    public List<MDTNodeModel> getMdtModels() {
        return mdtModels;
    }

    public void setMdtModels(List<MDTNodeModel> mdtModels) {
        this.mdtModels = mdtModels;
    }

    public UserType getUserType() {
        return userType;
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

    public List<ECMGroupMetaModel> getEcmGroups() {
        return ecmGroups;
    }

    public void setEcmGroups(List<ECMGroupMetaModel> ecmGroups) {
        this.ecmGroups = ecmGroups;
    }

    public List<Long> getGroupUserIds() {
        return groupUserIds;
    }

    public void setGroupUserIds(List<Long> groupUserIds) {
        this.groupUserIds = groupUserIds;
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

    public enum Type {
        ROOT_USER,
        ROOT_ROLE,
        USER,
        ROLE,
        USER_ROLE,
        ROLE_USER,
    }
}
