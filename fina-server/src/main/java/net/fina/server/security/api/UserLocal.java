package net.fina.server.security.api;

import net.fina.common.client.constants.PasswordChangeStatus;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReportFilter;
import net.fina.common.shared.SortField;
import net.fina.security.auth.CustomPrincipal;
import net.fina.server.fi.entity.UserFi;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.reports.entity.Report;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnVersion;
import net.fina.server.security.entity.Permission;
import net.fina.server.security.entity.Role;
import net.fina.server.security.entity.User;
import net.fina.server.security.model.UserFilterType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserLocal {
    List<User> loadUsers(boolean loadAll);

    List<User> loadUsers(int offset, int limit, Map<UserFilterType, Object> filterMap);

    List<User> filterUsersByPermission(List<Long> userIds, String permission);

    List<User> loadUsersIdCode();

    User findUserbyLogin(String login);

    User findUserbyId(long userId);

    long getUserIdByLogin(String login);

    boolean isUserExist(String login);

    Collection<Role> loadUserRoles(long userId);

    List<Role> loadRoles();

    List<Role> loadRolesSimple();

    List<Role> filterRolesByPermission(List<Long> roleIds, String permission);

    Collection<User> loadRoleUsers(long roleId);

    Collection<User> loadRoleUsers(long roleId, String searchValue, SortField sortField, int offset, int limit);

    long countRoleUsers(long roleId, String searchValue);

    Collection<Permission> loadPermissions();

    Collection<Permission> loadUserPermissions(long userId);

    Collection<Permission> loadUserGroupsPermissions(long userId);

    Collection<Permission> loadRolePermissions(long roleId);

    List<User> loadRoleUsersByPermission(long roleId, long permissionId, String userFilter, boolean excludeDeleted);

    List<Long> getUserFis(long userId);

    List<String> getUserFiCodes(String userLogin);

    void deleteUser(long pk) throws FinATypeException;

    void deleteUsers(List<Long> ids) throws FinATypeException;

    User saveUser(User user) throws FinATypeException;

    Role saveRole(Role role) throws FinATypeException;

    void deleteRole(long roleId) throws FinATypeException;

    Map<Long, String> getUserNameByIds(List<Long> userIds);

    List<ReturnVersion> getUserVersion(long userId);

    List<ReturnVersion> getRoleVersion(long roleId);

    List<ReturnVersion> getUserRoleVersion(long userId);

    Map<Long, Boolean> getCurrentUserAndRoleReturnVersions();

    Map<Long, MDTNode> getUserMdt(long userId);

    Map<Long, MDTNode> getUserRoleMdt(long userId);

    Map<Long, MDTNode> getRoleMdt(List<Long> roleIds);

    PasswordChangeStatus changePassword(String login, String oldPassword, String newPassword);

    List<String> findUsersByEmail(String email);

    Collection<ReturnDefinition> loadUserReturnDefinitions(long userId);

    Collection<ReturnDefinition> loadUserRoleReturnDefinitions(long userId);

    Collection<ReturnDefinition> loadRoleReturnDefinitions(long roleId);

    boolean isChangePassword(String login);

    boolean isLdapUser(String login);

    User loadSimpleUser(long id);

    long getCurrentUserId();

    String getCurrentUserLogin();

    User getCurrentUser();

    List<Report> loadUserReports(Map<ReportFilter, Object> filter);

    List<Report> loadUserRoleReports(Map<ReportFilter, Object> filter);

    List<Report> loadRoleReport(long roleId);

    List<Integer> loadUserReportIds(long userId);

    List<Integer> loadRoleReportIds(long roleId);

    List<Integer> loadUserRoleReportIds(long UserId);

    CustomPrincipal getCallerPrincipal();

    boolean checkLoginUnique(User user);

    boolean checkCodeUnique(Role role);

    String getUsersCount();

    String getRolesCount();

    boolean isCurrentUserInGroup(long roleId);

    void saveUserFiPermissions(long bankId, List<UserFi> userFis);

    List<User> loadFiUsers(long fiId);

    List<User> loadUsersByLogins(List<String> logins);

    void addMtdToUser(MDTNode node, long userId);

    boolean hasGroupPermission(String permission, List<Long> ids);

    List<User> loadUserByPermission(String permission);

    List<String> getAllUsersLogin(boolean activeUsersOnly);

    List<Object[]> loadUserPermissionJoinTable();

    List<Object[]> loadRolePermissionJoinTable();

    List<Object[]> loadRoleUserJoinTable();

    List<User> loadUsersPaged(int start, int limit, String filter);

    void flushAuthCache(Role role);

    long getUsersCount(String filter);

    long getUsersCount(Map<UserFilterType, Object> filterMap);

    List<Long> loadUserIdsNotIn(List<Long> userIds);

    User activateDeletedUser(User user) throws FinATypeException;

    User updateUserExternalData(User user) throws FinATypeException;

    User removeUserRelations(User user) throws FinATypeException;

    List<User> loadOrphanUsers(String filter, long langId, boolean excludeDeleted);

    List<User> loadOrphanUsersByPermission(String filter, long langId, long permissionId, boolean excludeDeleted);

    Collection<User> loadRoleUsers(long roleId, long langId, String userFilter, boolean excludeDeleted);

    Role findRoleById(long id);

    List<Matrix> loadUserPermittedMatrixList(long userId);

    List<Matrix> loadRolePermittedMatrixList(long roleId);

    List<User> loadAllUsersSimple();

    void unblockUser(String login);
}
