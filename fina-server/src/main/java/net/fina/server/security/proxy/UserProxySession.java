package net.fina.server.security.proxy;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jws.WebParam;
import jakarta.transaction.Transactional;
import net.fina.auditlog.api.AuditLogLocal;
import net.fina.auditlog.entity.AuditLog;
import net.fina.common.client.constants.OperationType;
import net.fina.common.client.constants.PasswordChangeStatus;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.AuditLogFilter;
import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ReturnDefinitionModel;
import net.fina.common.client.util.PasswordValidationUtil;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.server.util.ECMUtil;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.*;
import net.fina.common.shared.auditlog.AuditLogModel;
import net.fina.common.shared.ecm.model.ECMGroupMetaModel;
import net.fina.common.shared.mdt.MDTNodeModel;
import net.fina.common.shared.notification.SysNotificationMetaModel;
import net.fina.common.shared.user.*;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import net.fina.first.ecm.group.proxy.GroupProxySession;
import net.fina.first.ecm.people.proxy.PeopleProxySession;
import net.fina.messages.MessagesUtil;
import net.fina.security.api.AuthorizationLocal;
import net.fina.security.util.SecurityUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.communicator.entity.CommunicatorMessage;
import net.fina.server.communicator.entity.UserMessageStatus;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;
import net.fina.server.fi.model.FiSimpleModel;
import net.fina.server.fi.model.SimpleFiPairModel;
import net.fina.server.fi.model.UserRoleFiModel;
import net.fina.server.fi.model.UserRoleFiModelHelper;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.matrix.api.MatrixLocal;
import net.fina.server.matrix.model.MatrixModel;
import net.fina.server.matrix.model.helper.MatrixModelHelper;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.RoleMdt;
import net.fina.server.mdt.entity.RoleMdtId;
import net.fina.server.mdt.entity.UserMdt;
import net.fina.server.mdt.entity.UserMdtId;
import net.fina.server.notification.proxy.SysNotificationProxySession;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.entity.Report;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.entity.*;
import net.fina.server.returns.model.helper.ReturnDefinitionModelHelper;
import net.fina.server.returns.model.helper.ReturnVersionModelHelper;
import net.fina.server.security.api.LdapLocal;
import net.fina.server.security.api.PermissionLocal;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.Permission;
import net.fina.server.security.entity.Role;
import net.fina.server.security.entity.User;
import net.fina.server.security.model.PasswordChangeModel;
import net.fina.server.security.model.UserFilterType;
import net.fina.server.security.model.UserInfoModel;
import net.fina.server.security.model.helper.PermissionModelHelper;
import net.fina.server.security.model.helper.RoleModelHelper;
import net.fina.server.security.model.helper.UserModelHelper;
import net.fina.server.util.UserExportUtil;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
@PermitAll
public class UserProxySession {
    @Inject
    private UserLocal userLocal;
    @Inject
    private LanguageLocal languageLocal;
    @Inject
    private AuthorizationLocal authorizationLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private AuditLogLocal auditLogLocal;
    @Inject
    private PropertyLocal propertyLocal;
    @Inject
    private ReportLocal reportLocal;
    @Inject
    private GroupProxySession ecmGroupProxySession;
    @Inject
    private PeopleProxySession ecmPeopleProxySession;
    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @Inject
    private ReturnDefinitionLocal returnDefinitionLocal;

    @Inject
    private PermissionLocal permissionLocal;
    @Inject
    private SysNotificationProxySession sysNotificationProxySession;

    @Inject
    private MatrixLocal matrixLocal;

    @Inject
    private LdapLocal ldapLocal;

    public List<String> getUserPermissions() {
        return authorizationLocal.loadUserPermission(userLocal.getCurrentUserLogin());
    }

    public UserInfoModel loadCurrentUserInfo() {
        List<String> permissions = authorizationLocal.loadUserPermission(userLocal.getCurrentUserLogin());
        List<String> fiCodes = fiLocal.loadFiCodes();
        String login = userLocal.getCurrentUserLogin();

        return new UserInfoModel(login, permissions, fiCodes);
    }

    public UserInfoModel loadUserInfo(String login) {
        List<String> permissions = authorizationLocal.loadUserPermission(login);
        List<String> fiCodes = fiLocal.loadFiCodes(login);

        return new UserInfoModel(login, permissions, fiCodes);
    }


    public UserExternalDataModel getCurrentUserExternal(String langCode) {
        long langID = languageLocal.getLanguageByCode(langCode).getId();
        User user = userLocal.getCurrentUser();
        UserExternalDataModel model = new UserExternalDataModel();
        model.setId(user.getId());
        model.setEmail(user.getEmail());
        model.setLogin(user.getLogin());
        model.setPhone(user.getPhone());
        model.setContactPerson(user.getContactPersonDescription().getDescription(langID));
        model.setContactPersonStrId(user.getContactPersonDescription().getNameStrId());
        model.setContactPersonPosition(user.getContactPersonPosition().getDescription(langID));
        model.setContactPersonPositionStrId(user.getContactPersonPosition().getNameStrId());

        return model;
    }

    @Transactional(rollbackOn = FinATypeException.class)
    public UserExternalDataModel updateUserExternal(UserExternalDataModel userInfoModel, String langCode) throws FinATypeException {
        if (!userLocal.getCallerPrincipal().getName().equalsIgnoreCase(userInfoModel.getLogin())) {
            throw new FinATypeException(FinATypeException.Type.INVALID_PERMISSIONS);
        }

        long langID = languageLocal.getLanguageByCode(langCode).getId();
        Language defaultLang = languageLocal.getDefaultLanguage();

        User currentUser = userLocal.findUserbyLogin(userInfoModel.getLogin());
        String userInfoChangeText = getUserInfoChangeText(currentUser, userInfoModel, defaultLang.getCode(), langID);

        User user = new User(currentUser.getId());

        user.setLogin(userInfoModel.getLogin());
        user.setEmail(userInfoModel.getEmail());
        user.setPhone(userInfoModel.getPhone());
        Description description = currentUser.getContactPersonDescription();
        if (userInfoModel.getContactPerson() != null) {
            description.addDescription(langID, userInfoModel.getContactPerson());
        }
        user.setContactPersonDescription(description);
        if (userInfoModel.getContactPersonPosition() != null) {
            user.setContactPersonPosition(new Description(langID, userInfoModel.getContactPersonPositionStrId(), userInfoModel.getContactPersonPosition()));
        }
        user = userLocal.updateUserExternalData(user);

        sendExternalUserInfoUpdateNotification(currentUser, userInfoChangeText);

        UserExternalDataModel responseModel = new UserExternalDataModel();
        responseModel.setId(user.getId());
        responseModel.setEmail(user.getEmail());
        responseModel.setLogin(user.getLogin());
        responseModel.setPhone(user.getPhone());
        if (user.getContactPersonDescription() != null) {
            responseModel.setContactPerson(user.getContactPersonDescription().getDescription(langID));
            responseModel.setContactPersonStrId(user.getContactPersonDescription().getNameStrId());
        }
        if (user.getContactPersonPosition() != null) {
            responseModel.setContactPersonPosition(user.getContactPersonPosition().getDescription(langID));
            responseModel.setContactPersonPositionStrId(user.getContactPersonPosition().getNameStrId());
        }
        return responseModel;
    }

    public List<PasswordChangeStatus> changeCurrUserPassword(PasswordChangeModel passwordChangeModel) throws FinATypeException {
        if (!passwordChangeModel.getNewPassword().equals(passwordChangeModel.getRepeatPassword())) {
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }

        String login = userLocal.getCurrentUserLogin();


        String passMinLength = propertyLocal.getSystemProperty(PropertyKeys.MINIMAL_PASSWORD_LENGTH);
        String passContainLetters = propertyLocal.getSystemProperty(PropertyKeys.PASSWORD_WITH_LETTERS);
        String passContainNums = propertyLocal.getSystemProperty(PropertyKeys.PASSWORD_WITH_NUMS);
        String passContainUpperLetters = propertyLocal.getSystemProperty(PropertyKeys.PASSWORD_WITH_LETTERS_UPPERCASE);
        String passContainSpecialChars = propertyLocal.getSystemProperty(PropertyKeys.PASSWORD_WITH_SPECIAL_CHARACTERS);

        FinaPasswordPolicy passwordPolicy = new FinaPasswordPolicy(passMinLength, passContainLetters, passContainNums, passContainUpperLetters, passContainSpecialChars);

        List<PasswordChangeStatus> statuses = PasswordValidationUtil.isValidPassword(passwordPolicy, passwordChangeModel.getNewPassword().trim());

        if (!statuses.isEmpty()) {
            return statuses;
        }

        PasswordChangeStatus passwordChangeStatus = userLocal.changePassword(login,
                SecurityUtil.encodePassword(passwordChangeModel.getCurrPassword()),
                SecurityUtil.encodePassword(passwordChangeModel.getNewPassword()));

        statuses.add(passwordChangeStatus);

        if (passwordChangeStatus.equals(PasswordChangeStatus.SUCCESS)) {
            SecurityUtil.flushAuthCache(login);
        }

        return statuses;
    }

    @RolesAllowed({PermissionIdNames.FINA_USER_REVIEW, PermissionIdNames.FINA_WEB_INTERNAL_USER})
    public List<RoleModel> loadAllUsersAndGroups(String locale) {
        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
        long langId = lang.getId();

        List<RoleModel> roles = new ArrayList<>(loadRolesWithUsers(langId));
        List<RoleModel> orphanUsers = loadOrphanUsersAsRoles(langId);
        roles.addAll(orphanUsers);
        return roles;
    }

    private List<UserModel> getUserModels(Collection<User> users, long langId) {
        return users.stream().map(user -> {
            UserModel model = new UserModel();
            model.setId(user.getId());
            model.setLogin(user.getLogin());
            model.setDescription(user.getDescription().getDescription(langId));
            model.setTitleDescription(user.getTitledescription().getDescription(langId));
            model.setContactPerson(user.getContactPersonDescription().getDescription(langId));
            model.setEmail(user.getEmail());
            model.setPhone(user.getPhone());
            model.setBlocked(user.getBlocked());
            model.setDisabled(user.isDisabled());
            model.setDeleted(user.isDeleted());
            return model;
        }).collect(Collectors.toList());
    }

    public long getCurrentUserId() {
        return userLocal.getCurrentUserId();
    }

    public List<String> findUsersByEmail(@WebParam(name = "email") String email) {
        return userLocal.findUsersByEmail(email);
    }

    public void disconnect() {
        SecurityUtil.flushAuthCache(userLocal.getCurrentUserLogin());
    }

    public List<AuditLogModel> loadUserLoginActivity() {
        List<AuditLogModel> result = new ArrayList<>();
        String user = userLocal.getCurrentUserLogin();

        Map<AuditLogFilter, Object> filter = new HashMap<>();
        filter.put(AuditLogFilter.OPERATION_TYPE, OperationType.LOGIN);
        filter.put(AuditLogFilter.ENTITY_NAME, User.class.getName());
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date now = new Date();
        filter.put(AuditLogFilter.TIME_FROM, new Date(now.getTime() - (7 * DAY_IN_MS)));
        filter.put(AuditLogFilter.TIME_TO, now);
        filter.put(AuditLogFilter.SORT_DISABLED, false);
        filter.put(AuditLogFilter.OFFSET, 0);
        filter.put(AuditLogFilter.LIMIT, 20);
        filter.put(AuditLogFilter.ENTITY_ID, "\"" + user.trim() + "\"");

        PaginatedListWrapper<AuditLog> auditLogs = auditLogLocal.findAuditLogs(filter, new SortField("relevanceTime", "desc"));

        auditLogs.getList().forEach(log -> result.add(getAuditLogModel(log)));

        return result;
    }

    public List<AuditLogModel> loadUserActivity() {
        List<AuditLogModel> result = new ArrayList<>();
        String user = userLocal.getCurrentUserLogin();
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date now = new Date();

        List<OperationType> operationTypes = Arrays.asList(OperationType.EDIT, OperationType.CREATE, OperationType.LOGIN);
        List<String> entities = Arrays.asList(User.class.getName(), CommunicatorMessage.class.getName(), UserMessageStatus.class.getName(), UploadFile.class.getName());

        PaginatedListWrapper<AuditLog> auditLogs = auditLogLocal.loadUserActivities(user.trim(), new Date(now.getTime() - (7 * DAY_IN_MS)), operationTypes, entities);

        auditLogs.getList().forEach(log -> result.add(getAuditLogModel(log)));

        return result;
    }

    @RolesAllowed(PermissionIdNames.POWER_BI_USER)
    public boolean hasPowerBiAccess() {
        return hasPowerBiAccess(userLocal.getCurrentUserLogin());
    }

    public boolean hasPowerBiAccess(String userLogin) {
        User user = userLocal.findUserbyLogin(userLogin);
        if (user.isDeleted() || user.isDisabled() || user.getBlocked()) {
            return false;
        }

        for (Permission permission : user.getPermissions()) {
            if (permission.getIdName().equalsIgnoreCase(PermissionIdNames.POWER_BI_USER)) {
                return true;
            }
        }

        for (Role role : user.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                if (permission.getIdName().equalsIgnoreCase(PermissionIdNames.POWER_BI_USER)) {
                    return true;
                }
            }
        }

        return false;
    }

    private AuditLogModel getAuditLogModel(AuditLog auditLog) {
        AuditLogModel auditLogModel = new AuditLogModel();
        auditLogModel.setId(auditLog.getId());
        auditLogModel.setActorId(auditLog.getActorId());
        auditLogModel.setEntityId(auditLog.getEntityId());
        auditLogModel.setEntityProperty(auditLog.getEntityProperty());
        auditLogModel.setEntityName(auditLog.getEntityName());
        auditLogModel.setEntityPropertyNewValue(auditLog.getEntityPropertyNewValue());
        auditLogModel.setOperationType(auditLog.getOperationType());
        auditLogModel.setEntityPropertyOldValue(auditLog.getEntityPropertyOldValue());
        auditLogModel.setEntityPropertyNewValue(auditLog.getEntityPropertyNewValue());
        auditLogModel.setRelevanceTime(auditLog.getRelevanceTime());

        return auditLogModel;
    }

    @RolesAllowed({PermissionIdNames.FINA_USER_REVIEW, PermissionIdNames.FINA_WEB_INTERNAL_USER})
    public List<UserAndGroupMetaModel> loadUsersAndGroups(String userFilter, boolean onlyExternalUsers, boolean excludeDeleted) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        long externalUserPermissionId = permissionLocal.loadPermissionByIdName(PermissionIdNames.FINA_WEB_EXTERNAL_USER).getId();

        List<UserAndGroupMetaModel> roles = userLocal.loadRoles().stream().map(item -> new UserAndGroupMetaModel(item.getId(), item.getCode(), item.getDescription().getDescription(langId), true)).collect(Collectors.toList());
        List<User> users = onlyExternalUsers ? userLocal.loadOrphanUsersByPermission(userFilter, langId, externalUserPermissionId, excludeDeleted) : userLocal.loadOrphanUsers(userFilter, langId, excludeDeleted);

        roles.forEach(r -> r.setUsers(onlyExternalUsers ?
                getUserModels(userLocal.loadRoleUsersByPermission(r.getId(), externalUserPermissionId, userFilter, excludeDeleted), langId) :
                getUserModels(userLocal.loadRoleUsers(r.getId(), langId, userFilter, excludeDeleted), langId)));

        users.forEach(u -> roles.add(new UserAndGroupMetaModel(u.getId(), u.getLogin(), u.getDescription().getDescription(langId), false, u.getBlocked(), u.isDisabled())));

        return roles;
    }

    public List<PermissionModel> loadPermissions() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return userLocal.loadPermissions().stream().map(p -> PermissionModelHelper.toModel(p, langId))
                .collect(Collectors.toList());
    }

    public List<PermissionModel> loadUserPermissions(long userId) {
        List<PermissionModel> models = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        for (Permission permission : userLocal.loadUserGroupsPermissions(userId)) {
            PermissionModel permissionModel = PermissionModelHelper.toModel(permission, langId);
            permissionModel.setUserRolePermission(true);
            models.add(permissionModel);
        }

        for (Permission permission : userLocal.loadUserPermissions(userId)) {
            PermissionModel model = PermissionModelHelper.toModel(permission, langId);
            if (!models.contains(model)) {
                models.add(model);
                model.setPermitted(true);
            }
        }

        return models;
    }

    public List<RoleModel> loadGroups() {
        long langId = ThreadLocalHolder.getLanguage().getId();

        return userLocal.loadRolesSimple().stream().map(r -> new RoleModel(r.getId(), r.getCode(), r.getDescription().getDescription(langId), r.getDescription().getNameStrId(), (int) r.getUserCount())).collect(Collectors.toList());
    }

    public List<RoleModel> loadUserGroups(long userId) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        return userLocal.loadUserRoles(userId).stream().map(r -> new RoleModel(r.getId(), r.getCode(), r.getDescription().getDescription(langId), r.getDescription().getNameStrId())).collect(Collectors.toList());
    }

    public List<ReturnDefinitionModel> loadUserReturnDefinitions(long userId) {
        List<ReturnDefinitionModel> models = new ArrayList<>();

        long langId = ThreadLocalHolder.getLanguage().getId();


        for (ReturnDefinition returnDefinition : userLocal.loadUserRoleReturnDefinitions(userId)) {
            ReturnDefinitionModel returnDefinitionModel = ReturnDefinitionModelHelper.toModel(returnDefinition, langId);
            returnDefinitionModel.setUserRoleReturnDefinition(true);
            models.add(returnDefinitionModel);
        }

        for (ReturnDefinition returnDefinition : userLocal.loadUserReturnDefinitions(userId)) {
            ReturnDefinitionModel model = ReturnDefinitionModelHelper.toModel(returnDefinition, langId);
            if (!models.contains(model)) {
                models.add(model);
            }
        }
        return models;
    }

    public List<ReturnVersionModel> loadUserReturnVersions(long userId) {
        List<ReturnVersionModel> models = new ArrayList<>();

        long langId = ThreadLocalHolder.getLanguage().getId();

        for (ReturnVersion returnVersion : userLocal.getUserRoleVersion(userId)) {
            ReturnVersionModel model = ReturnVersionModelHelper.toModel(returnVersion, langId);
            model.setUserRoleReturnVersion(true);
            model.setHasAmendFromRole(returnVersion.isCanAmend());
            models.add(model);
        }

        for (ReturnVersion returnVersion : userLocal.getUserVersion(userId)) {
            ReturnVersionModel model = ReturnVersionModelHelper.toModel(returnVersion, langId);
            if (!models.contains(model)) {
                model.setUserReturnVersion(true);
                models.add(model);
            } else {
                ReturnVersionModel existing = models.get(models.indexOf(model));
                if (!existing.isCanUserAmend() && model.isCanUserAmend()) {
                    existing.setCanUserAmend(true);
                }
            }
        }

        return models;
    }


    public PaginatedListWrapper<UserModel> loadUsers(int page, int limit, Map<UserFilterType, Object> filterMap) {
        int offset = PagingUtil.getOffsetFromPage(page, limit);

        long langId = ThreadLocalHolder.getLanguage().getId();

        List<User> users = userLocal.loadUsers(offset, limit, filterMap);

        long countUsers = userLocal.getUsersCount(filterMap);

        List<UserModel> userModels = UserModelHelper.toModels(users, langId);

        return new PaginatedListWrapper<>(userModels, limit, countUsers);
    }


    public PaginatedListWrapper<UserModel> loadGroupUsers(long roleId, String searchValue, SortField sortField, int page, int limit) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        PaginatedListWrapper<UserModel> result = new PaginatedListWrapper<>();

        result.setList(UserModelHelper.toModels(userLocal.loadRoleUsers(roleId, searchValue, sortField, PagingUtil.getOffsetFromPage(page, limit), limit), langId));
        result.setTotalResults(userLocal.countRoleUsers(roleId, searchValue));
        result.setPageSize(limit);
        result.setCurrentPage(page);

        return result;
    }

    public List<PermissionModel> loadGroupPermissions(long groupId) {
        List<PermissionModel> models = new ArrayList<>();

        long langId = ThreadLocalHolder.getLanguage().getId();

        for (Permission permission : userLocal.loadRolePermissions(groupId)) {
            models.add(PermissionModelHelper.toModel(permission, langId, true));
        }

        return models;
    }

    public List<ReturnDefinitionModel> loadGroupReturns(long groupId) {
        List<ReturnDefinitionModel> models = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        for (ReturnDefinition returnDefinition : userLocal.loadRoleReturnDefinitions(groupId)) {
            models.add(ReturnDefinitionModelHelper.toModel(returnDefinition, langId));
        }

        return models;
    }

    public List<ReturnVersionModel> loadGroupVersions(long groupId) {
        List<ReturnVersionModel> models = new ArrayList<>();

        long langId = ThreadLocalHolder.getLanguage().getId();

        for (ReturnVersion returnVersion : userLocal.getRoleVersion(groupId)) {
            models.add(ReturnVersionModelHelper.toModel(returnVersion, langId));
        }

        return models;
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public RoleModel saveGroup(RoleModel groupModel) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Role role = RoleModelHelper.toEntity(groupModel, langId);


        if (groupModel.getFiIds() != null) {
            role.setFis(groupModel.getFiIds().stream().map(Fi::new).collect(Collectors.toList()));
        }

        if (groupModel.getMdtNodes() != null) {
            Collection<RoleMdt> mdts = new ArrayList<>();
            for (MDTNodeModel nodeModel : groupModel.getMdtNodes()) {
                RoleMdtId roleMdtId = new RoleMdtId();
                roleMdtId.setNode(mdtNodeLocal.findNodeById(nodeModel.getId()));
                roleMdtId.setRole(role);

                RoleMdt roleMdt = new RoleMdt();
                roleMdt.setRoleMdtId(roleMdtId);
                roleMdt.setCanAmend(nodeModel.isCanUserAmend());
                mdts.add(roleMdt);
            }
            role.setRoleMdts(mdts);
        }

        if (groupModel.getReturnDefintionIds() != null) {
            role.setReturnDefinitions(returnDefinitionLocal.loadReturnDefinitionsById(groupModel.getReturnDefintionIds()));
        }

        if (groupModel.getReportIds() != null) {
            List<Report> reports = new ArrayList<>();
            for (long id : groupModel.getReportIds()) {
                reports.add(reportLocal.findById((int) id));
            }
            role.setReports(reports);
        }

        if (groupModel.getReturnVersions() != null) {
            List<RoleReturnVersion> roleReturnVersions = new ArrayList<>();
            for (ReturnVersionModel versionModel : groupModel.getReturnVersions()) {
                RoleReturnVersion roleReturnVersion = new RoleReturnVersion();

                RoleReturnVersionId versionId = new RoleReturnVersionId();
                versionId.setReturnVersion(ReturnVersionModelHelper.toEntity(versionModel, langId));
                versionId.setRole(role);

                roleReturnVersion.setCanAmend(versionModel.isCanUserAmend());
                roleReturnVersion.setReturnVersionId(versionId);

                roleReturnVersions.add(roleReturnVersion);
            }
            role.setReturnVersions(roleReturnVersions);
        }

        if (groupModel.getUsers() != null) {
            List<User> groupUsers = new ArrayList<>();
            groupModel.getUsers().forEach(userModel -> {
                groupUsers.add(userLocal.findUserbyId(userModel.getId()));
            });
            role.setUsers(groupUsers);
        }

        if (groupModel.getPermittedMatrixIds() != null) {
            role.setPermittedMatrixList(matrixLocal.loadMatrixByIds(groupModel.getPermittedMatrixIds()));
        }

        if (groupModel.getPermissionIds() != null) {
            List<Permission> permissions = new ArrayList<>();
            for (long id : groupModel.getPermissionIds()) {
                permissions.add(permissionLocal.findById(id));
            }
            role.setPermissions(permissions);
        }


        role = userLocal.saveRole(role);
        RoleModel result = RoleModelHelper.toModel(role, langId);
        userLocal.flushAuthCache(role);

        if (userLocal.isCurrentUserInGroup(result.getId())) {
            result.setMustReLogin(true);
        }
        result.setUserCount(userLocal.countRoleUsers(role.getId(), null));

        return result;
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public RoleModel updateGroup(long groupId, RoleModel group) throws FinATypeException {
        group.setId(groupId);
        return saveGroup(group);
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public UserModel saveUser(UserModel userModel, boolean removeUserRelations) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();

        String plainPassword = userModel.getPassword();

        checkMixedAuthUserPermissions(userModel);

        User user = UserModelHelper.toEntity(userModel, langId);

        if (user.getId() > 0) {
            user.setPassword(plainPassword != null ? SecurityUtil.encodePassword(plainPassword) : plainPassword);
        }

        if (userModel.getFiIds() != null) {
            user.setFis(userModel.getFiIds().stream().map(Fi::new).collect(Collectors.toList()));
        }

        if (userModel.getMdtNodes() != null) {
            List<UserMdt> userMdts = new ArrayList<>();
            for (MDTNodeModel nodeModel : userModel.getMdtNodes()) {
                UserMdtId userMdtId = new UserMdtId();
                userMdtId.setNode(mdtNodeLocal.findNodeById(nodeModel.getId()));
                userMdtId.setUser(user);

                UserMdt userMdt = new UserMdt();
                userMdt.setUserMdtId(userMdtId);
                userMdt.setCanAmend(nodeModel.isCanUserAmend());

                userMdts.add(userMdt);
            }
            user.setUserMdts(userMdts);
        }

        if (userModel.getReturnIds() != null) {
            user.setReturnDefinitions(returnDefinitionLocal.loadReturnDefinitionsById(userModel.getReturnIds()));
        }

        if (userModel.getReportIds() != null) {
            List<Report> reports = new ArrayList<>();
            for (long id : userModel.getReportIds()) {
                reports.add(reportLocal.findById((int) id));
            }
            user.setReports(reports);
        }

        if (userModel.getReturnVersions() != null) {
            Collection<UserReturnVersion> userReturnVersions = new ArrayList<>();
            for (ReturnVersionModel versionModel : userModel.getReturnVersions()) {
                UserReturnVersionId versionId = new UserReturnVersionId();
                versionId.setReturnVersion(ReturnVersionModelHelper.toEntity(versionModel, langId));
                versionId.setUser(user);
                UserReturnVersion userReturnVersion = new UserReturnVersion();
                userReturnVersion.setReturnVersionId(versionId);
                userReturnVersion.setCanAmend(versionId.getReturnVersion().isCanAmend());

                userReturnVersions.add(userReturnVersion);
            }
            user.setReturnVersions(userReturnVersions);
        }

        if (userModel.getPermittedMatrixIds() != null) {
            user.setPermittedMatrixList(matrixLocal.loadMatrixByIds(userModel.getPermittedMatrixIds()));
        }

        if (userModel.getPermissionIds() != null) {
            List<Permission> permissions = new ArrayList<>();
            for (long id : userModel.getPermissionIds()) {
                permissions.add(permissionLocal.findById(id));
            }
            user.setPermissions(permissions);
        }

        if (userModel.getGroupIds() != null) {
            List<Role> roles = new ArrayList<>();
            for (long id : userModel.getGroupIds()) {
                roles.add(userLocal.findRoleById(id));
            }
            user.setRoles(roles);
        }
        user = userLocal.saveUser(user);

        if (removeUserRelations) {
            user = userLocal.removeUserRelations(user);

            if (ECMUtil.isEcmEnable() && userModel.getEcmGroups() != null) {
                userModel.setEcmGroups(Collections.emptyList());
            }
        }

        UserModel model = UserModelHelper.toModel(user, langId);

        if (userLocal.getCurrentUserId() == userModel.getId()) {
            model.setMustReLogin(true);
        }

        if (ECMUtil.isEcmEnable()) {
            saveEcmUser(userModel, plainPassword);
        }

        return model;
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public UserModel updateUser(UserModel userModel, boolean removeUserRelations) throws FinATypeException {
        return saveUser(userModel, removeUserRelations);
    }

    private void saveEcmUser(UserModel userModel, String plainPassword) throws FinATypeException {
        String firstName = userModel.getDescription() != null ? userModel.getDescription().trim().split("\\s")[0] : "NONAME";
        String lastName = "";
        if (userModel.getDescription() != null && userModel.getDescription().split("\\s").length > 1) {
            lastName = userModel.getDescription().trim().substring(userModel.getDescription().indexOf(" "), userModel.getDescription().length()).trim();
        }
        PersonRepresentation person = new PersonRepresentation(userModel.getLogin(), userModel.getEmail(), userModel.getPhone(), true, firstName, lastName);
        plainPassword = userModel.getId() >= 0 && userModel.isPasswordChanged() ? plainPassword : null;

        if (userModel.getEcmGroups() != null) {
            ecmGroupProxySession.saveUserGroups(person, plainPassword, userModel.getEcmGroups());
        } else {
            PersonBodyUpdate personBodyUpdate = person.toPersonBodyUpdate();
            personBodyUpdate.setPassword(plainPassword);
            ecmPeopleProxySession.updateOrCreatePerson(personBodyUpdate);
        }
    }

    private void checkMixedAuthUserPermissions(UserModel model) throws FinATypeException {
        String langCode = ThreadLocalHolder.getLanguage().getCode();
        String message = null;

        if (model.getAuthType() == AuthorizationType.MIXED && model.getGroupIds() != null && !model.getGroupIds().isEmpty()) {
            List<Long> roleIds = model.getGroupIds();
            switch (model.getUserType()) {
                case FINA_USER:
                    if (userLocal.hasGroupPermission(PermissionIdNames.FINA_WEB_INTERNAL_USER, roleIds)) {
                        message = CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.exception.mixedAuth.invalidUserPermissions", langCode), model.getUserType().name(), model.getLogin(), PermissionIdNames.FINA_WEB_INTERNAL_USER);
                    }
                    break;
                case LDAP_USER:
                    if (userLocal.hasGroupPermission(PermissionIdNames.FINA_WEB_EXTERNAL_USER, roleIds)) {
                        message = CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.exception.mixedAuth.invalidUserPermissions", langCode), model.getUserType().name(), model.getLogin(), PermissionIdNames.FINA_WEB_EXTERNAL_USER);
                    }
                    break;
            }
        }
        if (message != null) {
            throw new FinATypeException(message);
        }
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_DELETE)
    public void deleteUser(long userId) throws FinATypeException {
        userLocal.deleteUser(userId);
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_DELETE)
    public void deleteUsers(List<Long> userIds) throws FinATypeException {
        userLocal.deleteUsers(userIds);
    }

    public UserModel activateDeletedUser(long userId) throws FinATypeException {
        User user = userLocal.findUserbyId(userId);

        if (!user.isDeleted()) {
            throw new FinATypeException("User is not deleted");
        }
        return UserModelHelper.toModel(userLocal.activateDeletedUser(user), ThreadLocalHolder.getLanguage().getId());
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_DELETE)
    public void deleteGroup(long groupId) throws FinATypeException {
        userLocal.deleteRole(groupId);
    }

    public UserModel getUser(long userId) {
        return UserModelHelper.toModel(userLocal.findUserbyId(userId), ThreadLocalHolder.getLanguage().getId());
    }

    public List<ECMGroupMetaModel> loadEcmGroups(long userId) {
        List<ECMGroupMetaModel> userGroups = (userId > 0) ? ecmGroupProxySession.loadUserGroups(userLocal.findUserbyId(userId).getLogin()) : new ArrayList<>();
        List<ECMGroupMetaModel> allGroups = ecmGroupProxySession.loadGroups();

        allGroups.forEach(gr -> {
            if (userGroups.contains(gr)) {
                gr.setChecked(true);
            }
        });
        return allGroups;
    }

    public ECMGroupMetaModel createEcmGroup(ECMGroupMetaModel model) throws FinATypeException {
        return ecmGroupProxySession.createGroup(model);
    }

    public RoleModel getGroup(long groupId) {
        return RoleModelHelper.toModel(userLocal.findRoleById(groupId), ThreadLocalHolder.getLanguage().getId());
    }

    public void sendExternalUserInfoUpdateNotification(User user, String userInfoChangeText) {
        if (!userInfoChangeText.isEmpty()) {
            Language defaultLang = languageLocal.getDefaultLanguage();
            user = userLocal.findUserbyId(user.getId());
            String userDescription = user.getDescription() != null ? user.getDescription().getDescription(defaultLang.getId()) : "";
            userDescription = userDescription == null ? "NONAME" : userDescription;

            List<User> internalUsers = userLocal.loadUserByPermission(PermissionIdNames.FINA_WEB_INTERNAL_USER);
            for (User internalUser : internalUsers) {

                SysNotificationMetaModel sysNotificationMetaModel = new SysNotificationMetaModel();
                String notificationContent = CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.dcs.userProfileChanged", defaultLang.getCode()), user.getLogin(), userDescription, userInfoChangeText);
                sysNotificationMetaModel.setNotification(notificationContent);
                sysNotificationMetaModel.setDatetimeAdded(new Date());
                sysNotificationMetaModel.setNotify(internalUser.getId());

                sysNotificationProxySession.save(sysNotificationMetaModel);
            }
        }

    }

    private String getUserInfoChangeText(User user, UserExternalDataModel userInfoModel, String defaultLangCode, long langId) {
        StringBuilder sb = new StringBuilder();

        String emptyPlaceHolder = "---------";

        if (!Objects.equals(user.getEmail(), userInfoModel.getEmail())) {
            sb.append(MessagesUtil.getString("net.fina.ems.fiPrint.header.email", defaultLangCode))
                    .append(": [")
                    .append(user.getEmail() == null ? emptyPlaceHolder : user.getEmail())
                    .append(" -> ")
                    .append(userInfoModel.getEmail())
                    .append("]");
        }

        if (!Objects.equals(user.getPhone(), userInfoModel.getPhone())) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(MessagesUtil.getString("net.fina.ems.fiPrint.header.phone", defaultLangCode))
                    .append(": [")
                    .append(user.getPhone() == null ? emptyPlaceHolder : user.getPhone())
                    .append(" -> ")
                    .append(userInfoModel.getPhone())
                    .append("]");
        }

        String oldContactPersonDescription = user.getContactPersonDescription() != null ? user.getContactPersonDescription().getDescription(langId) : emptyPlaceHolder;
        String newContactPersonDescription = userInfoModel.getContactPerson();

        if (!Objects.equals(oldContactPersonDescription, newContactPersonDescription)) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(MessagesUtil.getString("net.fina.user.contactPerson", defaultLangCode))
                    .append(": [")
                    .append(oldContactPersonDescription == null ? emptyPlaceHolder : oldContactPersonDescription)
                    .append(" -> ")
                    .append(newContactPersonDescription)
                    .append("]");
        }

        String oldContactPersonPosition = user.getContactPersonPosition() != null ? user.getContactPersonPosition().getDescription(langId) : emptyPlaceHolder;
        String newContactPersonPosition = userInfoModel.getContactPersonPosition();

        if (!Objects.equals(oldContactPersonPosition, newContactPersonPosition)) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(MessagesUtil.getString("net.fina.user.contactPerson.position", defaultLangCode))
                    .append(": [")
                    .append(oldContactPersonPosition == null ? emptyPlaceHolder : oldContactPersonPosition)
                    .append(" -> ")
                    .append(newContactPersonPosition)
                    .append("]");
        }

        return sb.toString();
    }


    public byte[] getUserRoleAndPermissionContent(String langCode) throws IOException {
        long langId = languageLocal.getLanguageByCodeOrDefault(langCode).getId();

        Map<Long, User> users = new LinkedHashMap<>();
        Map<Long, Permission> permissions = new HashMap<>();
        Map<Long, Role> roles = new HashMap<>();


        for (User user : userLocal.loadUsers(true)) {
            users.put(user.getId(), user);
        }
        for (Permission permission : userLocal.loadPermissions()) {
            permissions.put(permission.getId(), permission);
        }
        for (Role role : userLocal.loadRoles()) {
            roles.put(role.getId(), role);
        }

        return UserExportUtil.exportToExcel(roles, users, permissions, userLocal.loadRoleUserJoinTable(),
                userLocal.loadUserPermissionJoinTable(), userLocal.loadRolePermissionJoinTable(), langId, langCode);
    }

    public List<LdapUserMetaModel> getLdapUsers() {
        return ldapLocal.getLdapUsers(null);
    }

    public PaginatedListWrapper<LdapUserMetaModel> loadLdapUsers(String filterText, int pageSize, int page) {
        List<LdapUserMetaModel> data = ldapLocal.getLdapUsers(filterText);

        PaginatedListWrapper<LdapUserMetaModel> result = new PaginatedListWrapper<>();
        if (!data.isEmpty()) {
            result.setList(data);
            result.setPageSize(pageSize);
            result.setCurrentPage(page);
        }
        return result;
    }

    public LdapUserMetaModel getLdapUser(String uid) {
        return ldapLocal.getLdapUser(uid);
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public void flashAuthCacheForPrincipal(String principalName) {
        SecurityUtil.flushAuthCache(principalName);
    }

    public List<KeyValuePair<String, String>> loadPermissionTranslations() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<Permission> permissions = permissionLocal.loadPermissions();

        return permissions.stream()
                .map(p -> new KeyValuePair<>(p.getIdName(), p.getDescription().getDescription(langId)))
                .distinct()
                .collect(Collectors.toList());
    }

    public void translatePermissions(List<KeyValuePair<String, String>> translations) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        permissionLocal.updateDescription(translations.stream().collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue)), langId);
    }

    public List<MatrixModel> loadUserPermittedMatrixList(long userId) {
        return MatrixModelHelper.toModels(userLocal.loadUserPermittedMatrixList(userId), ThreadLocalHolder.getLanguage().getId());
    }

    public List<MatrixModel> loadRolePermittedMatrixList(long roleId) {
        return MatrixModelHelper.toModels(userLocal.loadRolePermittedMatrixList(roleId), ThreadLocalHolder.getLanguage().getId());

    }

    public List<UserModelSimple> loadAllUsers() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<UserModelSimple> users = new ArrayList<>();
        for (User user : userLocal.loadUsers(false)) {
            String description = user.getDescription() != null ? user.getDescription().getDescription(langId) : null;
            users.add(new UserModelSimple(user.getId(), user.getLogin(), description));
        }
        return users;
    }

    public void unblockUser(String login) {
        userLocal.unblockUser(login);
    }


    @RolesAllowed({PermissionIdNames.FINA_USER_AMEND, PermissionIdNames.FINA_USER_REVIEW})
    public List<SimpleFiPairModel> loadUserRolePermittedFis(long entityId, boolean isUser, Boolean excludeDisabled) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<Fi> allFis = fiLocal.loadAllFis(excludeDisabled);
        List<UserRoleFiModel> allFiModels = new ArrayList<>(UserRoleFiModelHelper.toModels(allFis, langId, null));

        List<Fi> entityPermittedFis = fiLocal.loadUserRoleFis(entityId, isUser, excludeDisabled);
        List<UserRoleFiModel> entityPermittedFiModels = UserRoleFiModelHelper.toModels(entityPermittedFis, langId, isUser);

        Set<Long> permittedModelIds = entityPermittedFiModels.stream().map(UserRoleFiModel::getId).collect(Collectors.toSet());
        Set<Long> userRoleFiModelIds = new HashSet<>();

        if (isUser) {
            List<Fi> userRoleFis = fiLocal.loadUserRoleFis(entityId);
            List<UserRoleFiModel> models = UserRoleFiModelHelper.toModels(userRoleFis, langId, false);
            userRoleFiModelIds = models.stream().map(UserRoleFiModel::getId).collect(Collectors.toSet());

        }
        // check permitted fis
        setUserRolePermittedFis(allFiModels, userRoleFiModelIds, permittedModelIds, isUser);

        return groupedUserRoleFis(allFiModels, langId);
    }


    private void setUserRolePermittedFis(List<UserRoleFiModel> allFiModels, Set<Long> userRoleFiModelIds, Set<Long> permittedModelIds, boolean isUser) {
        for (UserRoleFiModel model : allFiModels) {
            if (isUser) {
                if (userRoleFiModelIds.contains(model.getId())) {
                    model.setRoleFi(true);
                    continue;
                }
                if (permittedModelIds.contains(model.getId())) {
                    model.setUserFi(true);
                }
            } else {
                if (permittedModelIds.contains(model.getId())) {
                    model.setRoleFi(true);
                }
            }
        }
    }

    private List<SimpleFiPairModel> groupedUserRoleFis(List<UserRoleFiModel> list, long langId) {

        Map<FiTypeSimpleModel, List<UserRoleFiModel>> resultMap = new HashMap<>();

        for (UserRoleFiModel urFiModel : list) {
            FiTypeSimpleModel fiType = urFiModel.getFiType();
            resultMap.computeIfAbsent(fiType, k -> new ArrayList<>()).add(urFiModel);
        }

        List<FiType> fiTypes = fiLocal.loadFiTypes();

        List<SimpleFiPairModel> result = new ArrayList<>();
        if (fiTypes != null) {
            List<FiTypeSimpleModel> models = fiTypes.stream().map(fiType -> new FiTypeSimpleModel(fiType.getId(), fiType.getCode(), fiType.getDescription().getDescription(langId))).toList();
            models.stream().filter(fiType -> !resultMap.containsKey(fiType))
                    .forEach(fiType -> resultMap.put(fiType, new ArrayList<>()));
        }

        for (Map.Entry<FiTypeSimpleModel, List<UserRoleFiModel>> entry : resultMap.entrySet()) {
            FiSimpleModel fiModel = new FiSimpleModel(entry.getKey());
            result.add(new SimpleFiPairModel(fiModel, entry.getValue()));
        }

        return result;
    }
    private List<RoleModel> loadRolesWithUsers(long langId) {
        return userLocal.loadRoles().stream()
                .map(role -> {
                    RoleModel model = new RoleModel(
                            role.getId(),
                            role.getCode(),
                            role.getDescription().getDescription(langId)
                    );
                    model.setUsers(getUserModels(userLocal.loadRoleUsers(role.getId()), langId));
                    return model;
                })
                .toList();
    }

    private List<RoleModel> loadOrphanUsersAsRoles(long langId) {
        return userLocal.loadOrphanUsers("", langId, false).stream()
                .map(user -> new RoleModel(
                        user.getId(),
                        user.getLogin(),
                        user.getDescription().getDescription(langId)
                ))
                .toList();
    }

}
