package net.fina.server.security.impl;

import global.namespace.truelicense.api.LicenseManagementException;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import net.fina.common.client.constants.PasswordChangeStatus;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReportFilter;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.SortField;
import net.fina.common.shared.UserType;
import net.fina.common.shared.jms.model.UserInfoMessage;
import net.fina.elytron.AuthenticationConstants;
import net.fina.messages.MessagesUtil;
import net.fina.security.api.AuthorizationLocal;
import net.fina.security.auth.CustomPrincipal;
import net.fina.security.impl.LoginAttemptPolicyManager;
import net.fina.security.util.SecurityUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dcs.jms.DcsJMSClient;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.Fi_;
import net.fina.server.fi.entity.UserFi;
import net.fina.server.fi.entity.UserFi_;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.entity.RoleMdt;
import net.fina.server.mdt.entity.UserMdt;
import net.fina.server.mdt.entity.UserMdtId;
import net.fina.server.reports.entity.Report;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnVersion;
import net.fina.server.returns.entity.RoleReturnVersion;
import net.fina.server.returns.entity.UserReturnVersion;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.product.Product;
import net.fina.server.security.entity.*;
import net.fina.server.security.model.UserFilterType;
import net.fina.server.security.util.UserFieldValidator;
import net.fina.server.util.DBUtil;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
@Local(UserLocal.class)
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
public class UserSession implements UserLocal {

    @Inject
    private EntityManager em;

    @Resource
    private SessionContext sc;

    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private AuthorizationLocal authorizationLocal;
    @Inject
    private LoginAttemptPolicyManager loginAttemptPolicyManager;
    @Inject
    private DcsJMSClient dcsJMSClient;

    @SuppressWarnings("unchecked")
    @Override
    public List<User> loadUsers(boolean loadAll) {
        try {
            Product.getInstance().check();
        } catch (LicenseManagementException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (loadAll) {
            return em.createQuery("select u from SYS_USERS as u order by u.login asc").getResultList();
        }
        Query query = em.createNamedQuery("loadUsers", User.class).setParameter("deleted", false);
        return (List<User>) query.getResultList();
    }

    @Override
    public List<User> loadUsers(int offset, int limit, Map<UserFilterType, Object> filterMap) {
        try {
            Product.getInstance().check();
        } catch (LicenseManagementException e) {
            throw new RuntimeException(e.getMessage());
        }

        SortField sortField = (SortField) filterMap.get(UserFilterType.SORT);
        if (sortField == null) {
            sortField = new SortField("login", "asc");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);


        Selection[] selections = new Selection[]{root.get(User_.id), root.get(User_.login), root.get(User_.description), root.get(User_.titledescription), root.get(User_.contactPersonDescription), root.get(User_.phone), root.get(User_.email), root.get(User_.userType), root.get(User_.blocked), root.get(User_.disabled), root.get(User_.changePassword), root.get(User_.deleted),};

        query.multiselect(selections);

        List<Predicate> predicates = getUserFilterPredicate(cb, root, filterMap);
        predicates.add(cb.equal(root.get(User_.deleted), false));
        query.distinct(true);
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(sortField.isAsc() ? cb.asc(root.get(sortField.getProperty())) : cb.desc(root.get(sortField.getProperty())));

        TypedQuery<User> loadQuery = em.createQuery(query);
        if (offset > 0) {
            loadQuery.setFirstResult(offset);
        }
        if (limit > 0) {
            loadQuery.setMaxResults(limit);
        }

        return loadQuery.getResultList();
    }

    @Override
    public List<User> filterUsersByPermission(List<Long> userIds, String permission) {
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }

        Query query = em
                .createQuery("select u from SYS_USERS u where u.id in(:ids)", User.class)
                .setParameter("ids", userIds);

        List<User> result = query.getResultList();

        return result.stream()
                .filter((u) -> hasPermission(u.getPermissions(), permission)
                        || u.getRoles().stream()
                        .anyMatch(r -> hasPermission(r.getPermissions(), permission))
                )
                .collect(Collectors.toList());
    }

    @Override

    public User findUserbyLogin(String login) {
        Query query = em.createNamedQuery("loadUserByUserName", User.class).setParameter("login", login.toLowerCase().trim());
        return (User) query.getSingleResult();
    }

    @Override

    public User findUserbyId(long userId) {
        return em.find(User.class, userId);
    }

    @Override

    public long getUserIdByLogin(String login) {
        Query query = em.createQuery("select u.id from SYS_USERS as u where trim(LOWER(u.login))=:login");
        query.setParameter("login", login.trim().toLowerCase());
        Object result = query.getSingleResult();
        return (long) result;
    }

    @Override

    public boolean isUserExist(String login) {
        Query query = em.createNamedQuery("loadUserByUserName", User.class).setParameter("login", login.trim().toLowerCase());
        return !query.getResultList().isEmpty();
    }

    @Override
    public Collection<Role> loadUserRoles(long userId) {
        User user = em.find(User.class, userId);
        return user.getRoles();
    }

    @SuppressWarnings("unchecked")
    public List<Role> loadRoles() {
        Query query = em.createNamedQuery("loadRoles", Role.class);
        return query.getResultList();
    }

    @Override
    public List<Role> loadRolesSimple() {
        return em.createQuery("select new net.fina.server.security.entity.Role(r.id,r.version,r.code,r.description,count(ru.id)) from SYS_ROLES r left join r.users ru group by r.id,r.version,r.code,r.description", Role.class)
                .getResultList();

    }

    @Override
    public List<Role> filterRolesByPermission(List<Long> roleIds, String permission) {
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        Query query = em
                .createQuery("select r from SYS_ROLES as r where r.id in(:ids)", Role.class)
                .setParameter("ids", roleIds);

        List<Role> result = query.getResultList();

        return result.stream()
                .filter((u) -> hasPermission(u.getPermissions(), permission))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> loadRoleUsers(long roleId) {
        Role role = em.find(Role.class, roleId);
        return (role != null && role.getUsers() != null ? role.getUsers() : new ArrayList<>());
    }

    public Collection<User> loadRoleUsers(long roleId, String searchValue, SortField sortField, int offset, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<Role> root = cq.from(Role.class);
        Join<Role, User> userJoin = root.join(Role_.users);


        cq.select(userJoin);

        List<Predicate> predicates = buildPredicates(cb, root, userJoin, roleId, searchValue);
        predicates.add(cb.equal(userJoin.get(User_.deleted), false));
        cq.where(predicates.toArray(new Predicate[0]));

        cq.orderBy(sortField.isAsc() ? cb.asc(userJoin.get(sortField.getProperty())) : cb.desc(userJoin.get(sortField.getProperty())));

        TypedQuery<User> query = em.createQuery(cq);

        if (limit > 0) {
            query.setMaxResults(limit);
        }

        if (offset > 0) {
            query.setFirstResult(offset);
        }

        return query.getResultList();
    }

    public long countRoleUsers(long roleId, String searchValue) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Role> root = cq.from(Role.class);
        Join<Role, User> userJoin = root.join(Role_.users);

        cq.select(cb.count(userJoin.get(User_.id)));

        List<Predicate> predicates = buildPredicates(cb, root, userJoin, roleId, searchValue);
        predicates.add(cb.equal(userJoin.get(User_.deleted), false));
        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> query = em.createQuery(cq);

        return query.getSingleResult();
    }


    @SuppressWarnings("unchecked")
    @Override
    public Collection<Permission> loadPermissions() {
        Query query = em.createNamedQuery("loadPermissions");
        return query.getResultList();
    }

    @Override

    public Collection<Permission> loadUserPermissions(long userId) {
        User user = em.find(User.class, userId);
        if (user == null) return new ArrayList<>();
        return user.getPermissions();
    }

    @SuppressWarnings("unchecked")
    @Override

    public Collection<Permission> loadUserGroupsPermissions(long userId) {
        Query permLoadQuery = em.createQuery("SELECT DISTINCT rp FROM SYS_USERS as u, IN (u.roles) r, IN(r.permissions) rp WHERE u.id=:userId ");
        permLoadQuery.setParameter("userId", userId);
        List<Permission> permissions = permLoadQuery.getResultList();
        return permissions;
    }

    @Override

    public Collection<Permission> loadRolePermissions(long roleId) {
        Role role = em.find(Role.class, roleId);
        return role.getPermissions();
    }

    @Override
    public List<User> loadRoleUsersByPermission(long roleId, long permissionId, String userFilter, boolean excludeDeleted) {
        String deletedCondition = excludeDeleted ? "u.deleted = false" : "1=1";

        String queryString = "select distinct u from SYS_USERS u " +
                "left join u.roles r left join r.permissions rp " +
                "where r.id = :roleId and rp.id = :permissionId and " + deletedCondition;

        if (userFilter != null && !userFilter.trim().isEmpty()) {
            queryString += " and trim(lower(u.login)) like trim(lower(:userFilter))";
        }

        TypedQuery<User> query = em.createQuery(queryString, User.class)
                .setParameter("roleId", roleId)
                .setParameter("permissionId", permissionId);

        if (userFilter != null && !userFilter.trim().isEmpty()) {
            query.setParameter("userFilter", "%" + userFilter + "%");
        }

        return query.getResultList();
    }


    @Override
    public void deleteUsers(List<Long> ids) throws FinATypeException {
        if (ids != null) {
            List<String> dependencyErrors = new ArrayList<>();

            for (long id : ids) {
                User user = em.find(User.class, id);
                if (user != null) {
                    StringBuilder errorMessage = new StringBuilder("User - [")
                            .append(user.getLogin())
                            .append("] has the following dependencies:\n");

                    boolean hasDependencies = false;

                    hasDependencies = appendDependencyMessage(errorMessage, user.getFis().stream().map(Fi::getCode).toList(), "Fis", hasDependencies);
                    hasDependencies = appendDependencyMessage(errorMessage, user.getReturnDefinitions().stream().map(ReturnDefinition::getCode).toList(), "Return Definitions", hasDependencies);
                    hasDependencies = appendDependencyMessage(errorMessage, user.getReports().stream().map(Report::getCode).toList(), "Reports", hasDependencies);
                    hasDependencies = appendDependencyMessage(errorMessage, user.getReturnVersions().stream()
                            .map(urv -> urv.getReturnVersionId().getReturnVersion().getCode())
                            .collect(Collectors.toList()), "Return Versions", hasDependencies);

                    if (hasDependencies) {
                        errorMessage.append("\n");
                        dependencyErrors.add(errorMessage.toString());
                    }
                }
            }

            if (!dependencyErrors.isEmpty()) {
                String finalErrorMessage = "\n" + String.join("\n", dependencyErrors);
                throw new FinATypeException(FinATypeException.Type.USER_DELETE_DEPENDENCIES, finalErrorMessage);
            }

            em.createQuery("update SYS_USERS set deleted = true where id in (:ids)")
                    .setParameter("ids", ids)
                    .executeUpdate();
        }
    }


    @Override
    public void deleteUser(long pk) throws FinATypeException {
        if (pk == getCurrentUserId()) {
            throw new FinATypeException(FinATypeException.Type.USER_DELETE_CURRENT);
        }

        User user = em.find(User.class, pk);

        // User FIs
        Collection<Fi> fis = user.getFis();
        if (fis != null && !fis.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.USER_DELETE_DEPENDENCIES);
        }

        // User Return Definitions
        Collection<ReturnDefinition> returnDefinitions = user.getReturnDefinitions();
        if (returnDefinitions != null && !returnDefinitions.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.USER_DELETE_DEPENDENCIES);
        }

        // User Reports
        Collection<Report> reports = user.getReports();
        if (reports != null && !reports.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.USER_DELETE_DEPENDENCIES);
        }

        // User Return Versions
        Collection<UserReturnVersion> returnVersions = user.getReturnVersions();
        if (returnVersions != null && !returnVersions.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.USER_DELETE_DEPENDENCIES);
        }

        user.setDeleted(true);

    }

    @Override
    @Transactional(rollbackOn = FinATypeException.class)
    public User saveUser(User user) throws FinATypeException {
        checkUserIsProgrammaticallyDeleted(user);
        validateUserRequiredFields(user);
        UserFieldValidator.validateLogin(user.getLogin());
        //trim login
        user.setLogin(user.getLogin().trim());

        boolean passwordChangedBlockedOrDisabled = false;

        if (user.getId() > 0) {

            User changedUser = em.find(User.class, user.getId());
            if (user.getPassword() != null && changedUser.getPassword() != null) {
                changedUser.setLastPasswordChangeDate(new Date());
                passwordChangedBlockedOrDisabled = !user.getPassword().equals(changedUser.getPassword());
            }
            passwordChangedBlockedOrDisabled |= user.getBlocked();
            passwordChangedBlockedOrDisabled |= user.isDisabled();
            changedUser.setLogin(user.getLogin());
            changedUser.setPassword(user.getPassword() != null ? user.getPassword() : changedUser.getPassword());
            changedUser.setChangePassword(user.getChangePassword());
            changedUser.setDescription(user.getDescription());
            changedUser.setTitledescription(user.getTitledescription());
            changedUser.setPhone(user.getPhone());
            changedUser.setEmail(user.getEmail());
            changedUser.setLastLoginDate(user.getLastLoginDate());
            changedUser.setContactPersonPosition(user.getContactPersonPosition());

            // Check User Permissions
            if (user.getPermissions() != null) {
                changedUser.setPermissions(user.getPermissions());
            }

            // Check User Roles
            if (user.getRoles() != null) {
                changedUser.setRoles(user.getRoles());
            }

            isPermissionsCorrect(changedUser);

            // Check User FIs
            if (user.getFis() != null) {
                changedUser.setFis(user.getFis());
            }

            if (user.getPermittedMatrixList() != null) {
                changedUser.setPermittedMatrixList(user.getPermittedMatrixList());
            }

            // Check User Return Definitions
            Collection<ReturnDefinition> returnDefinitions = user.getReturnDefinitions();
            if (returnDefinitions != null) {
                List<ReturnDefinition> newList = new ArrayList<ReturnDefinition>();
                for (ReturnDefinition rdef : returnDefinitions) {
                    ReturnDefinition definition = em.find(ReturnDefinition.class, rdef.getId());
                    newList.add(definition);
                }
                changedUser.setReturnDefinitions(newList);
            }

            // Check User Reports
            Collection<Report> reports = user.getReports();
            if (reports != null) {
                Collection<Report> refreshedReports = new ArrayList<>();
                for (Report report : reports) {
                    refreshedReports.add(em.find(Report.class, report.getId()));
                }
                changedUser.setReports(refreshedReports);
            }

            // Check Return Versions
            Collection<UserReturnVersion> returnVersions = user.getReturnVersions();
            if (returnVersions != null) {
                for (UserReturnVersion urv : changedUser.getReturnVersions()) {
                    if (!returnVersions.contains(urv)) {
                        em.remove(urv);
                    }
                }
                changedUser.setReturnVersions(returnVersions);

                for (UserReturnVersion urv : returnVersions) {
                    urv.getReturnVersionId().setUser(changedUser);
                    em.merge(urv);
                }
            }

            // Check user mdt
            Collection<UserMdt> userMdts = user.getUserMdts();
            if (userMdts != null) {
                for (UserMdt userMdt : changedUser.getUserMdts()) {
                    if (!userMdts.contains(userMdt)) {
                        em.remove(userMdt);
                    }
                }
                changedUser.setUserMdts(userMdts);

                for (UserMdt userMdt : userMdts) {
                    userMdt.getUserMdtId().setUser(changedUser);
                    em.merge(userMdt);
                }
            }

            changedUser.setUserType(user.getUserType());
            changedUser.setContactPersonDescription(user.getContactPersonDescription());

            //clear login error attempt counter
            if (!user.getBlocked()) {
                loginAttemptPolicyManager.remove(user.getLogin());
            }

            changedUser.setBlocked(user.getBlocked());
            changedUser.setDisabled(user.isDisabled());

            user = em.merge(changedUser);

            removeUsersFromFis(user.getId(), user.getFis().stream().map(Fi::getId).collect(Collectors.toList()));

            if (passwordChangedBlockedOrDisabled && authorizationLocal.hasUserPermission(user.getLogin(), PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
                SecurityUtil.flushDcsUserAuthCache(user.getLogin());
                dcsJMSClient.sendUserInfoMessage(new UserInfoMessage(user.getLogin(), true));
            } else if (user.getBlocked() || user.isDisabled()) {
                dcsJMSClient.sendUserInfoMessage(new UserInfoMessage(user.getLogin(), true));
            }

        } else {

            isPermissionsCorrect(user);
            // Encode Password
            user.setPassword(SecurityUtil.encodePassword(user.getPassword()));
            user.setDeleted(false);
            user.setLastPasswordChangeDate(new Date());
            em.persist(user);

            // Add User return versions can amend
            if (user.getReturnVersions() != null) {
                for (UserReturnVersion urv : user.getReturnVersions()) {
                    urv.getReturnVersionId().setUser(user);
                    em.merge(urv);
                }
            }

            if (user.getUserMdts() != null) {
                for (UserMdt userMdt : user.getUserMdts()) {
                    userMdt.getUserMdtId().setUser(user);
                    em.merge(userMdt);
                }
            }
        }

        // Flush auth cache
        SecurityUtil.flushAuthCache(user.getLogin());
        return user;
    }

    @Override
    @Transactional(rollbackOn = FinATypeException.class)
    public Role saveRole(Role role) throws FinATypeException {

        if (role.getCode() == null || role.getCode().isBlank()) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }

        if (!checkCodeUnique(role)) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        checkRolePermissions(role.getPermissions());

        if (role.getId() > 0) {
            Role changedRole = em.find(Role.class, role.getId());

            // Check Role Permissions
            Collection<Permission> permissions = role.getPermissions();
            if (role.getPermissions() != null) {
                changedRole.setPermissions(permissions);
            }

            // Check User FIs
            Collection<Fi> fis = role.getFis();
            validateRoleFis(role, changedRole);
            if (fis != null) {
                changedRole.setFis(fis);
            }

            if (role.getPermittedMatrixList() != null) {
                changedRole.setPermittedMatrixList(role.getPermittedMatrixList());
            }

            // Check Role Users
            Collection<User> users = role.getUsers();
            if (users != null) {
                // Validate user permissions
                for (User u : users) {
                    isPermissionsCorrect(u, changedRole.getPermissions());
                }
                changedRole.setUsers(users);
            }

            // Check Role Return Definitions
            Collection<ReturnDefinition> returnDefinitions = role.getReturnDefinitions();
            if (returnDefinitions != null) {
                List<ReturnDefinition> newList = new ArrayList<>();
                for (ReturnDefinition rdef : returnDefinitions) {
                    ReturnDefinition definition = em.find(ReturnDefinition.class, rdef.getId());
                    newList.add(definition);
                }
                changedRole.setReturnDefinitions(newList);
            }

            // Check Role Reports
            Collection<Report> reports = role.getReports();
            if (reports != null) {
                changedRole.setReports(reports);
            }

            // Check Role Return Versions
            Collection<RoleReturnVersion> returnVersions = role.getReturnVersions();
            if (returnVersions != null) {
                for (RoleReturnVersion urv : changedRole.getReturnVersions()) {
                    if (!role.getReturnVersions().contains(urv)) {
                        em.remove(urv);
                    }
                }
                changedRole.setReturnVersions(returnVersions);
                // Update User return versions can amend
                for (RoleReturnVersion urv : role.getReturnVersions()) {
                    urv.getReturnVersionId().setRole(changedRole);
                    em.merge(urv);
                }
            }

            // Check Role MDT
            Collection<RoleMdt> mdts = role.getRoleMdts();
            if (mdts != null) {
                for (RoleMdt roleMdt : changedRole.getRoleMdts()) {
                    if (!mdts.contains(roleMdt)) {
                        em.remove(roleMdt);
                    }
                }
                changedRole.setRoleMdts(mdts);

                for (RoleMdt roleMdt : mdts) {
                    if (changedRole.getRoleMdts().contains(roleMdt)) {
                        roleMdt.getRoleMdtId().setRole(changedRole);
                        em.merge(roleMdt);
                    }
                }
            }
            changedRole.setCode(role.getCode());
            changedRole.setDescription(role.getDescription());

            role = em.merge(changedRole);

        } else {
            validateRoleFis(role, role);
            em.persist(role);

            // Update User return versions can amend
            if (role.getReturnVersions() != null) {
                for (RoleReturnVersion urv : role.getReturnVersions()) {
                    urv.getReturnVersionId().setRole(role);
                    em.merge(urv);
                }
            }

            if (role.getRoleMdts() != null) {
                for (RoleMdt mdtRole : role.getRoleMdts()) {
                    mdtRole.getRoleMdtId().setRole(role);
                    em.merge(mdtRole);
                }
            }

        }

        return role;
    }

    private void deleteDataFromRoleUsers(Role role, User user) {
        role = em.find(Role.class, role.getId());

        // Remove User permissions that match role's permissions
        Collection<Permission> rolePermissions = role.getPermissions();
        if (rolePermissions != null) {
            user.getPermissions().removeAll(rolePermissions);
        }

        // Remove user return definitions that match role's return definitions
        Collection<ReturnDefinition> roleRDefinitions = role.getReturnDefinitions();
        if (roleRDefinitions != null) {
            user.getReturnDefinitions().removeAll(roleRDefinitions);
        }

        // Remove user reports that match role's reports
        Collection<Report> roleReports = role.getReports();
        if (roleReports != null) {
            user.getReports().removeAll(roleReports);
        }

        // Remove user return versions that match role's return versions
        Collection<ReturnVersion> roleReturnVersions = role.getReturnVersions() != null
                ? role.getReturnVersions().stream().map(rrv -> rrv.getReturnVersionId().getReturnVersion()).toList()
                : new ArrayList<>();

        Collection<UserReturnVersion> userReturnVersions = user.getReturnVersions();
        if (userReturnVersions != null) {
            for (UserReturnVersion urv : userReturnVersions) {
                if (roleReturnVersions.contains(urv.getReturnVersionId().getReturnVersion())) {
                    em.remove(urv);
                }
            }
        }

        // Remove user mdt nodes versions that match role's mdt nodes
        Collection<MDTNode> roleMdtNodes = role.getRoleMdts() != null ? role.getRoleMdts().stream().map(roleMdt -> roleMdt.getRoleMdtId().getNode()).toList() : new ArrayList<>();
        Collection<UserMdt> userMdts = user.getUserMdts();

        if (userMdts != null) {
            for (UserMdt userMdt : userMdts) {
                if (roleMdtNodes.contains(userMdt.getUserMdtId().getNode())) {
                    em.remove(userMdt);
                }
            }
        }

        // Remove user Fis that match role's Fis
        Collection<Fi> roleFis = role.getFis();
        Collection<Fi> userFis = user.getFis();
        if (roleFis != null) {
            userFis.removeAll(roleFis);
        }

    }


    @Override

    public void deleteRole(long roleId) throws FinATypeException {

        Role role = em.find(Role.class, roleId);

        // Role Users
        Collection<User> users = role.getUsers();
        if (users != null && !users.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.ROLE_DELETE_DEPENDENCIES);
        }

        // Role Return Definitions
        Collection<ReturnDefinition> returnDefinitions = role.getReturnDefinitions();
        if (returnDefinitions != null && !returnDefinitions.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.ROLE_DELETE_DEPENDENCIES);
        }

        // Role Reports
        Collection<Report> reports = role.getReports();
        if (reports != null && !reports.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.ROLE_DELETE_DEPENDENCIES);
        }

        // Role Return Versions
        Collection<RoleReturnVersion> returnVersions = role.getReturnVersions();
        if (returnVersions != null && !returnVersions.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.ROLE_DELETE_DEPENDENCIES);
        }

        em.remove(role);

    }

    @SuppressWarnings("unchecked")
    @Override

    public List<Long> getUserFis(long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // User Fis
        CriteriaQuery<Long> userFisQuery = cb.createQuery(Long.class);
        Root<User> userFiRoot = userFisQuery.from(User.class);
        Join<User, Fi> userFiJoin = userFiRoot.join(User_.fis);
        userFisQuery.select(userFiJoin.get(Fi_.id));
        userFisQuery.where(cb.equal(userFiRoot.get(User_.id), userId));
        Set<Long> fis = new HashSet<>(em.createQuery(userFisQuery).getResultList());

        //user Group Fis
        CriteriaQuery<Long> userRolesFisQuery = cb.createQuery(Long.class);
        Root<User> userAndRolesRoot = userRolesFisQuery.from(User.class);
        Join<User, Role> userAndRoleJoin = userAndRolesRoot.join(User_.roles);
        Join<Role, Fi> roleFisJoin = userAndRoleJoin.join(Role_.fis);
        userRolesFisQuery.select(roleFisJoin.get(Fi_.id));
        userRolesFisQuery.where(cb.equal(userAndRolesRoot.get(User_.id), userId));
        fis.addAll(em.createQuery(userRolesFisQuery).getResultList());

        return new ArrayList<>(fis);
    }

    @Override
    public List<String> getUserFiCodes(String userLogin) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // User Fis
        CriteriaQuery<String> userFisQuery = cb.createQuery(String.class);
        Root<User> userFiRoot = userFisQuery.from(User.class);
        Join<User, Fi> userFiJoin = userFiRoot.join(User_.fis);
        userFisQuery.select(userFiJoin.get(Fi_.code));
        userFisQuery.where(cb.equal(cb.trim(cb.lower(userFiRoot.get(User_.login))), userLogin.trim().toLowerCase()));
        Set<String> fis = new HashSet<>(em.createQuery(userFisQuery).getResultList());

        //user Group Fis
        CriteriaQuery<String> userRolesFisQuery = cb.createQuery(String.class);
        Root<User> userAndRolesRoot = userRolesFisQuery.from(User.class);
        Join<User, Role> userAndRoleJoin = userAndRolesRoot.join(User_.roles);
        Join<Role, Fi> roleFisJoin = userAndRoleJoin.join(Role_.fis);
        userRolesFisQuery.select(roleFisJoin.get(Fi_.code));
        userRolesFisQuery.where(cb.equal(cb.trim(cb.lower(userAndRolesRoot.get(User_.login))), userLogin.trim().toLowerCase()));
        fis.addAll(em.createQuery(userRolesFisQuery).getResultList());

        return new ArrayList<>(fis);
    }

    @SuppressWarnings("unchecked")
    @Override

    public Map<Long, String> getUserNameByIds(List<Long> userIds) {
        Map<Long, String> userNames = new HashMap<Long, String>();
        Query query = em.createQuery("select su.id, su.login from SYS_USERS su where su.id in(:id)");
        query.setParameter("id", userIds);
        List<Object[]> login = query.getResultList();
        for (Object[] object : login) {
            userNames.put((Long) object[0], (String) object[1]);
        }
        return userNames;
    }

    @SuppressWarnings("unchecked")
    @Override

    public List<User> loadUsersIdCode() {
        Query loadUserQuery = em.createQuery("select u.id, u.login from SYS_USERS u ");

        List<User> users = new ArrayList<User>();

        List<Object[]> result = loadUserQuery.getResultList();

        for (Object[] objects : result) {

            User user = new User();
            user.setId((Long) objects[0]);
            user.setLogin(objects[1] == null ? "" : objects[1].toString());

            users.add(user);

        }

        return users;
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<ReturnVersion> getUserVersion(long userId) {
        return em.createQuery("select new " + ReturnVersion.class.getName() + "(urv.returnVersionId.returnVersion,urv.canAmend) from SYS_USER_RETURN_VERSIONS urv where urv.returnVersionId.user.id=:userId", ReturnVersion.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<ReturnVersion> getRoleVersion(long roleId) {
        return em.createQuery("select new " + ReturnVersion.class.getName() + "(rrv.returnVersionId.returnVersion,rrv.canAmend) from SYS_ROLE_RETURN_VERSIONS rrv where rrv.returnVersionId.role.id=:roleId", ReturnVersion.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<ReturnVersion> getUserRoleVersion(long userId) {
        return em.createQuery("select new " + ReturnVersion.class.getName() + "(rrv.returnVersionId.returnVersion,rrv.canAmend) from SYS_USERS  u, IN(u.roles) ur, SYS_ROLE_RETURN_VERSIONS rrv where rrv.returnVersionId.role.id=ur.id and u.id=:userId", ReturnVersion.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Map<Long, Boolean> getCurrentUserAndRoleReturnVersions() {
        return getCallerPrincipal().getReturnVersions();
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public Map<Long, MDTNode> getUserMdt(long userId) {
        Map<Long, MDTNode> userMdts = new HashMap<>();

        List<MDTNode> nodes = em.createQuery("SELECT new " + MDTNode.class.getName() + "(umdt.userMdtId.node.id, umdt.canAmend) FROM SYS_USER_MDT umdt WHERE umdt.userMdtId.user.id=:userId", MDTNode.class)
                .setParameter("userId", userId)
                .getResultList();

        for (MDTNode node : nodes) {
            userMdts.put(node.getId(), node);
        }
        return userMdts;
    }

    @Override
    public Map<Long, MDTNode> getUserRoleMdt(long userId) {
        List<Long> roleIds = new ArrayList<>();
        if (userId > 0) {
            roleIds = findUserbyId(userId).getRoles().stream().map(Role::getId).collect(Collectors.toList());
        }
        return getRoleMdt(roleIds);
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public Map<Long, MDTNode> getRoleMdt(List<Long> roleIds) {
        List<MDTNode> nodes = em.createQuery("SELECT new " + MDTNode.class.getName() +
                        "(m.roleMdtId.node.id, m.canAmend)  from SYS_ROLE_MDT m " +
                        "where m.roleMdtId.role.id in :roleIds", MDTNode.class)
                .setParameter("roleIds", roleIds.isEmpty() ? Collections.singletonList(-1L) : roleIds)
                .getResultList();

        return nodes.stream().distinct().collect(Collectors.toMap(MDTNode::getId, node -> node));
    }

    @Override
    public PasswordChangeStatus changePassword(String login, String oldPassword, String newPassword) {
        PasswordChangeStatus status = PasswordChangeStatus.ERROR;

        Query query = em.createQuery("SELECT u from SYS_USERS u WHERE trim(lower(u.login))=:login and trim(u.password)=:oldPassword ");
        query.setParameter("login", login.trim().toLowerCase());
        query.setParameter("oldPassword", oldPassword);

        try {
            User user = (User) query.getSingleResult();

            if (passwordAlreadyUsed(newPassword, user.getId())) {
                status = PasswordChangeStatus.ALREADY_USED;
            } else {

                UserPassword up = new UserPassword();
                up.setPassword(newPassword);
                Date changeDate = new Date();
                up.setStoredate(changeDate);
                up.setUser(user);

                em.persist(up);

                user.setPassword(newPassword);
                user.setLastLoginDate(changeDate);
                user.setLastPasswordChangeDate(changeDate);
                user.setChangePassword(false);

                status = PasswordChangeStatus.SUCCESS;
                SecurityUtil.flushAuthCache(login.toLowerCase());
            }

        } catch (NoResultException noResultException) {
            Logger.getLogger(getClass()).error("Password Change Failed, Invalid login or password : '" + login + "'");
            Logger.getLogger(getClass()).error(noResultException.getMessage(), noResultException);
            status = PasswordChangeStatus.CURRENT_PASS_NO_MATCH;
        } catch (PersistenceException ex) {
            Logger.getLogger(getClass()).error(ex.getMessage(), ex);
        }

        return status;
    }

    private boolean passwordAlreadyUsed(String newPassword, long userId) {
        boolean usedBefore = false;

        @SuppressWarnings("unchecked")
        List<UserPassword> userPasswords = em.createNamedQuery("loadUserPasswordsByUserId").setParameter("userId", userId).getResultList();

        int oldStoredPasswordsNumber = Integer.parseInt(propertyLocal.getSystemProperty(PropertyKeys.OLD_STORED_PASSWORDS_NUMBER));

        for (int i = 0; i < oldStoredPasswordsNumber && i < userPasswords.size(); i++) {
            UserPassword up = userPasswords.get(i);
            if (up.getPassword().trim().equalsIgnoreCase(newPassword.trim())) {
                usedBefore = true;
                break;
            }
        }

        return usedBefore;
    }

    @SuppressWarnings("unchecked")
    @Override

    public List<String> findUsersByEmail(String email) {
        return em.createQuery("SELECT u.login FROM SYS_USERS u WHERE u.email LIKE :email ")
                .setParameter("email", "%" + email + "%").getResultList();
    }

    @Override

    public Collection<ReturnDefinition> loadUserReturnDefinitions(long userId) {
        User user = em.find(User.class, userId);
        if (user == null) return new ArrayList<>();
        return user.getReturnDefinitions();
    }

    @Override

    public Collection<ReturnDefinition> loadUserRoleReturnDefinitions(long userId) {
        User user = em.find(User.class, userId);
        if (user == null) {
            return new ArrayList<>();
        }
        HashSet<ReturnDefinition> definitions = new HashSet<>();
        for (Role role : user.getRoles()) {
            definitions.addAll(role.getReturnDefinitions());
        }
        return definitions;
    }

    @Override

    public Collection<ReturnDefinition> loadRoleReturnDefinitions(long roleId) {
        Role role = em.find(Role.class, roleId);
        return role.getReturnDefinitions();
    }

    @Override

    public boolean isChangePassword(String login) {
        return (Boolean) em.createNamedQuery("isChangePassword").setParameter("login", login.trim().toLowerCase()).getSingleResult();
    }

    @Override
    public boolean isLdapUser(String login) {
        return em.createNamedQuery("getUserTypeByLogin")
                .setParameter("login", login.trim().toLowerCase())
                .getSingleResult() == (UserType.LDAP_USER);
    }

    @Override

    public User loadSimpleUser(long id) {
        StringBuilder qlString = new StringBuilder();
        qlString.append("SELECT ");
        qlString.append(" NEW ");
        qlString.append(User.class.getName());
        qlString.append("(");
        qlString.append("u.id, ");
        qlString.append("u.version ");
        qlString.append(")");
        qlString.append(" from SYS_USERS u WHERE u.id=:id ");
        Query query = em.createQuery(qlString.toString());
        query.setParameter("id", id);
        return (User) query.getSingleResult();
    }

    @Override

    @LogDescription(ignore = true)
    public long getCurrentUserId() {
        String login = getCurrentUserLogin();
        if (!login.equalsIgnoreCase(AuthenticationConstants.SYSTEM_USER_LOGIN) && !login.equalsIgnoreCase("anonymous")) {
            return getCallerPrincipal().getId();
        }
        return -1;
    }

    @Override
    @LogDescription(ignore = true)
    public CustomPrincipal getCallerPrincipal() {
        return authorizationLocal.getCallerPrincipal();
    }

    @Override

    public boolean checkLoginUnique(User user) {
        return em.createNamedQuery("checkLoginUnique").setParameter("login", user.getLogin().trim().toLowerCase()).setParameter("id", user.getId()).getResultList().isEmpty();
    }

    private void validateUserEmail(User user) throws FinATypeException {
        String email = user.getEmail();
        String locale = ThreadLocalHolder.getLanguage().getCode();
        if (email == null || email.trim().isEmpty()) {
            throw new FinATypeException("Email Is Required");
        }

        boolean isUnique = em.createNamedQuery("checkEmailUnique")
                .setParameter("email", email.trim().toLowerCase())
                .setParameter("id", user.getId())
                .getResultList()
                .isEmpty();

        if (!isUnique) {
            throw new FinATypeException(FinATypeException.Type.EMAIL_UNIQUE,
                    CommonUtil.compileMessageWithParams(MessagesUtil.getString(FinATypeException.Type.EMAIL_UNIQUE.getCode(), locale), email, user.getLogin()));
        }

        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        String propertyValue = propertyLocal.getSystemProperty(PropertyKeys.BLOCKED_EMAIL_DOMAINS);
        List<String> blockedEmailDomains = propertyValue == null || propertyValue.trim().isEmpty()
                ? Collections.emptyList()
                : Arrays.stream(propertyValue.split(";"))
                .map(String::toLowerCase)
                .toList();

        String emailLower = email.toLowerCase();

        if (!matcher.matches() || (!blockedEmailDomains.isEmpty() && blockedEmailDomains.stream().anyMatch(emailLower::endsWith))) {
            FinATypeException.Type exType = !matcher.matches()
                    ? FinATypeException.Type.EMAIL_INVALID
                    : FinATypeException.Type.EMAIL_INVALID_DOMAIN;
            throw new FinATypeException(exType,
                    CommonUtil.compileMessageWithParams(MessagesUtil.getString(exType.getCode(), locale), email));
        }
    }


    @Override
    public boolean checkCodeUnique(Role role) {
        return em.createNamedQuery("checkCodeUnique").setParameter("code", role.getCode()).setParameter("id", role.getId()).getResultList().isEmpty();
    }

    private void checkRolePermissions(Collection<Permission> permissions) throws FinATypeException {
        if (permissions != null && !permissions.isEmpty() && permissions.contains(new Permission(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) && permissions.contains(new Permission(PermissionIdNames.FINA_WEB_INTERNAL_USER))) {
            throw new FinATypeException("Internal And External User Permissions cannot be used both at same time!");
        }
    }

    @Override
    public String getUsersCount() {
        Query query = em.createQuery("select count(u.id) from SYS_USERS u");
        return "" + query.getSingleResult();
    }

    @Override

    public String getRolesCount() {
        Query query = em.createQuery("select count(r.id) from SYS_ROLES r");
        return "" + query.getSingleResult();
    }

    @Override
    public boolean isCurrentUserInGroup(long roleId) {
        Role role = em.find(Role.class, roleId);
        return role.getUsers() != null && role.getUsers().contains(getCurrentUser());
    }

    @Override

    public void saveUserFiPermissions(long bankId, List<UserFi> userFis) {
        Query query = em.createQuery("DELETE from SYS_USER_BANKS u WHERE u.bankId=:bankId");
        query.setParameter("bankId", bankId);
        query.executeUpdate();

        for (UserFi userFi : userFis) {
            em.merge(userFi);
        }
    }

    @Override
    public List<User> loadFiUsers(long fiId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);

        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<UserFi> userFiRoot = subQuery.from(UserFi.class);
        subQuery.select(userFiRoot.get(UserFi_.userId));
        subQuery.where(cb.equal(userFiRoot.get(UserFi_.bankId), fiId));

        query.where(userRoot.get(User_.id).in(subQuery));

        return em.createQuery(query).getResultList();
    }

    @Override
    @LogDescription(ignore = true)
    public String getCurrentUserLogin() {
        return sc.getCallerPrincipal().getName().trim();
    }

    @Override
    public User getCurrentUser() {
        String login = sc.getCallerPrincipal().getName();
        return findUserbyLogin(login);
    }

    // Reports
    @Override
    public List<Report> loadUserReports(Map<ReportFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Report> query = cb.createQuery(Report.class);
        Root<User> root = query.from(User.class);
        Join<User, Report> reportJoin = root.join(User_.reports);
        query.select(reportJoin);
        List<Predicate> predicates = getFilterPredicate(cb, root, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        return em.createQuery(query).getResultList();
    }

    @Override
    public List<Report> loadUserRoleReports(Map<ReportFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Report> query = cb.createQuery(Report.class);
        Root<User> user = query.from(User.class);

        user.fetch(User_.roles, JoinType.LEFT).fetch(Role_.reports, JoinType.INNER);

        List<Predicate> predicates = getFilterPredicate(cb, user, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<Report> loadRoleReport(long roleId) {
        Query query = em.createQuery("SELECT r FROM SYS_ROLES AS role, IN (role.reports) AS r  WHERE role.id=:roleId");
        query.setParameter("roleId", roleId);

        return query.getResultList();
    }

    @Override
    public List<Integer> loadUserReportIds(long userId) {
        Query query = em.createQuery("SELECT r.id FROM SYS_USERS AS u, IN (u.reports) AS r  WHERE u.id=:userId");
        query.setParameter("userId", userId);

        return query.getResultList();
    }

    @Override
    public List<Integer> loadUserRoleReportIds(long userId) {
        Query query = em.createQuery("SELECT r.id FROM SYS_USERS AS u, IN(u.roles) AS ur, IN(ur.reports) AS r  WHERE u.id=:userId");
        query.setParameter("userId", userId);

        return query.getResultList();
    }

    @Override
    public List<Integer> loadRoleReportIds(long roleId) {
        Query query = em.createQuery("SELECT r.id FROM SYS_ROLES AS role, IN (role.reports) AS r  WHERE role.id=:roleId");
        query.setParameter("roleId", roleId);

        return query.getResultList();
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<User> user, Map<ReportFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter != null) {
            for (Map.Entry<ReportFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case FOLDER_ID:
                        break;
                    case USER_ID:
                        predicates.add(cb.equal(user.get(User_.id), (long) entry.getValue()));
                        break;
                }
            }
        }

        return predicates;
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<User> loadUsersByLogins(List<String> logins) {
        if (logins == null || logins.isEmpty()) {
            return new ArrayList<>();
        }
        return em.createQuery("SELECT NEW " + User.class.getName() + "(u.id, u.version) FROM SYS_USERS u where TRIM(u.login) in (:logins)", User.class)
                .setParameter("logins", logins)
                .getResultList();
    }

    @Override
    public void addMtdToUser(MDTNode node, long userId) {
        User user = em.find(User.class, userId);

        UserMdtId userMdtId = new UserMdtId();
        userMdtId.setNode(node);
        userMdtId.setUser(user);

        UserMdt userMdt = new UserMdt();
        userMdt.setUserMdtId(userMdtId);
        userMdt.setCanAmend(true);

        userMdt = em.merge(userMdt);
        user.getUserMdts().add(userMdt);
        em.merge(user);

    }

    @Override
    public boolean hasGroupPermission(String permission, List<Long> ids) {
        return !em.createNativeQuery("SELECT sr.ID FROM SYS_ROLES sr LEFT OUTER JOIN SYS_ROLE_PERMISSIONS srp ON " +
                        "sr.ID=srp.ROLEID WHERE sr.ID IN (:roleIds) AND srp.PERMISSIONID=(SELECT id FROM SYS_PERMISSIONS WHERE IDNAME=:idname)")
                .setParameter("roleIds", ids)
                .setParameter("idname", permission).getResultList().isEmpty();
    }

    @Override
    public List<User> loadUserByPermission(String permission) {
        return em.createQuery("select  distinct u from SYS_USERS u left join u.permissions sup left join u.roles sur left join" +
                        " sur.permissions surp where sup.idName=:permissionName or surp.idName=:permissionName ", User.class)
                .setParameter("permissionName", permission)
                .getResultList();
    }


    @Override
    public List<String> getAllUsersLogin(boolean activeUsersOnly) {
        StringBuilder query = new StringBuilder("select u.login from SYS_USERS u");
        if (activeUsersOnly) {
            query.append(" where u.deleted=:deleted");
            return em.createQuery(query.toString(), String.class)
                    .setParameter("deleted", false)
                    .getResultList();
        }
        return em.createQuery(query.toString(), String.class).getResultList();
    }

    @Override
    public List<Object[]> loadUserPermissionJoinTable() {
        List<Object[]> userPermissions = em.createQuery("select u.id, p.id, -1L from SYS_USERS u join u.permissions p order by u.id ", Object[].class).getResultList();
        List<Object[]> userRolePermissions = em.createQuery("select u.id, p.id, r.id from SYS_USERS u join u.roles r join r.permissions p order by u.id ", Object[].class).getResultList();

        userRolePermissions.addAll(userPermissions);
        return userRolePermissions;
    }

    @Override
    public List<Object[]> loadRolePermissionJoinTable() {
        return em.createQuery("select r.id, p.id from SYS_ROLES r join r.permissions p order by r.id", Object[].class)
                .getResultList();
    }

    @Override
    public List<Object[]> loadRoleUserJoinTable() {
        return em.createQuery("select r.id, u.id from SYS_ROLES r join r.users u order by r.id", Object[].class)
                .getResultList();
    }

    @Override
    public List<User> loadUsersPaged(int start, int limit, String filter) {
        TypedQuery<User> query = null;
        if (filter != null && !filter.trim().isEmpty()) {
            query = em.createQuery("select u from SYS_USERS u where u.deleted = false and (u.login like :filter or u.email like :filter)", User.class)
                    .setParameter("filter", "%" + filter + "%");
        } else {
            query = em.createQuery("select u from SYS_USERS u where u.deleted = false ", User.class);
        }
        query.setFirstResult(start).setMaxResults(limit);

        return query.getResultList();
    }


    @Override
    public void flushAuthCache(Role role) {
        for (User user : loadRoleUsers(role.getId())) {
            deleteDataFromRoleUsers(role, user);
            if (!user.getLogin().equalsIgnoreCase(getCurrentUserLogin())) {
                SecurityUtil.flushAuthCache(user.getLogin());
            }
        }
    }

    @Override
    public long getUsersCount(String filter) {
        return filter != null && !filter.trim().isEmpty() ?
                em.createQuery("select count(u.id) from SYS_USERS u where u.deleted=false and(u.login like :filter or u.email like :filter)", Long.class)
                        .setParameter("filter", "%" + filter + "%").getSingleResult() :
                em.createQuery("select count(u.id) from SYS_USERS u where u.deleted=false ", Long.class).getSingleResult();
    }

    @Override
    public long getUsersCount(Map<UserFilterType, Object> filterMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        query.select(cb.countDistinct(root.get(User_.id)));

        List<Predicate> predicates = getUserFilterPredicate(cb, root, filterMap);
        predicates.add(cb.equal(root.get(User_.deleted), false));

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> loadQuery = em.createQuery(query);

        return loadQuery.getSingleResult();
    }

    @Override
    public List<Long> loadUserIdsNotIn(List<Long> userIds) {
        return em.createQuery("select su.id from SYS_USERS su where su.id not in (:userIds)", Long.class)
                .setParameter("userIds", userIds)
                .getResultList();
    }

    @Override
    public User activateDeletedUser(User user) throws FinATypeException {
        User existingUser = em.find(User.class, user.getId());
        existingUser.setDeleted(false);
        return saveUser(user);
    }

    @Override
    public User updateUserExternalData(User user) throws FinATypeException {
        checkUserIsProgrammaticallyDeleted(user);
        validateUserEmail(user);

        User u = em.find(User.class, user.getId());
        u.setEmail(user.getEmail());
        u.setPhone(user.getPhone());
        u.setContactPersonDescription(user.getContactPersonDescription());
        u.setContactPersonPosition(user.getContactPersonPosition());

        em.merge(u);

        return getExternallyEditableUser(u);
    }

    @Override
    public User removeUserRelations(User user) throws FinATypeException {
        if (user.getId() <= 0) {
            throw new FinATypeException("User does not exist!");
        }

        User changedUser = em.find(User.class, user.getId());
        changedUser.setPermissions(null);
        changedUser.setRoles(null);
        changedUser.setFis(null);
        changedUser.setReturnDefinitions(null);
        changedUser.setReports(null);

        for (UserReturnVersion urv : changedUser.getReturnVersions()) {
            em.remove(urv);
        }
        changedUser.setReturnVersions(null);


        for (UserMdt userMdt : changedUser.getUserMdts()) {
            em.remove(userMdt);
        }
        changedUser.setUserMdts(null);

        if (user.getPermittedMatrixList() != null) {
            em.createNativeQuery("delete from SYS_USER_MATRIX_FILE_MAPPING where USER_ID=:userId")
                    .setParameter("userId", user.getId())
                    .executeUpdate();
        }

        changedUser.setPermittedMatrixList(null);
        user = em.merge(changedUser);

        return user;
    }

    @Override
    public List<User> loadOrphanUsers(String filter, long langId, boolean excludeDeleted) {
        String deletedCondition = excludeDeleted ? "u.deleted = false" : "1=1";
        String orderBy = " order by u.login";

        if (filter == null || filter.trim().isEmpty()) {
            return em.createQuery("select u from SYS_USERS u where u.roles is empty and " + deletedCondition + orderBy, User.class)
                    .getResultList();
        }

        return em.createNativeQuery(
                        "select u.* from SYS_USERS u " +
                                "where " + (excludeDeleted ? "u.deleted = 0" : "1=1") + " " +
                                "and u.id not in (select ur.userid from SYS_USERS_ROLES ur) " +
                                "and (u.login like :filter or " +
                                "u.namestrid in (select s.id from SYS_STRINGS s where s.langid = :langId and s.value like :filter))" + orderBy,
                        User.class)
                .setParameter("langId", langId)
                .setParameter("filter", "%" + filter + "%")
                .getResultList();
    }


    @Override
    public List<User> loadOrphanUsersByPermission(String filter, long langId, long permissionId, boolean excludeDeleted) {
        String deletedCondition = excludeDeleted ? "u.deleted = false" : "1=1";

        if (filter == null || filter.trim().isEmpty()) {
            return em.createQuery(
                            "select distinct u from SYS_USERS u " +
                                    "left join u.permissions up " +
                                    "where " + deletedCondition + " and u.roles is empty and up.id = :permissionId order by u.login asc ", User.class)
                    .setParameter("permissionId", permissionId)
                    .getResultList();
        }

        return em.createNativeQuery(
                        "select distinct u.* from SYS_USERS u " +
                                "left join SYS_USER_PERMISSIONS up on u.id = up.userid " +
                                "left join SYS_STRINGS s on u.namestrid = s.id and s.langid = :langId " +
                                "where " + (excludeDeleted ? "u.deleted = 0" : "1=1") + " " +
                                "and (u.login like :filter or s.value like :filter) " +
                                "and up.permissionid = :permissionId order by u.login asc", User.class)
                .setParameter("langId", langId)
                .setParameter("filter", "%" + filter + "%")
                .setParameter("permissionId", permissionId)
                .getResultList();
    }

    @Override
    public Collection<User> loadRoleUsers(long roleId, long langId, String userFilter, boolean excludeDeleted) {
        String deletedCondition = excludeDeleted ? "u.deleted = 0" : "1=1";

        if (userFilter == null || userFilter.trim().isEmpty()) {
            Collection<User> users = loadRoleUsers(roleId);
            if (excludeDeleted) {
                return users.stream().filter(u -> !u.isDeleted()).collect(Collectors.toList());
            } else {
                return users;
            }
        }

        return em.createNativeQuery(
                        "select u.* from SYS_USERS u, SYS_USERS_ROLES ur " +
                                "where ur.USERID = u.ID and ur.ROLEID = :roleId and " + deletedCondition + " and " +
                                "(u.LOGIN like :filter or u.NAMESTRID in " +
                                "(select s.ID from SYS_STRINGS s where s.LANGID = :langId and s.VALUE like :filter))",
                        User.class)
                .setParameter("filter", "%" + userFilter + "%")
                .setParameter("langId", langId)
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    public Role findRoleById(long id) {
        return em.find(Role.class, id);
    }

    @Override
    public List<Matrix> loadUserPermittedMatrixList(long userId) {
        return em.createQuery("select u.permittedMatrixList from SYS_USERS u where u.id=:userId", Matrix.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Matrix> loadRolePermittedMatrixList(long roleId) {
        return em.createQuery("select r.permittedMatrixList from SYS_ROLES r where r.id=:roleId", Matrix.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    public List<User> loadAllUsersSimple() {
        return em.createQuery("select new " + User.class.getName() + "(u.id,u.login,u.description) from SYS_USERS u", User.class)
                .getResultList();
    }

    @Override
    public void unblockUser(String login) {
        em.createQuery("update SYS_USERS  set blocked=false  where login=:login")
                .setParameter("login", login)
                .executeUpdate();
    }

    private User getExternallyEditableUser(User user) {
        User result = new User();
        result.setId(user.getId());
        result.setLogin(user.getLogin());
        result.setContactPersonDescription(user.getContactPersonDescription());
        result.setEmail(user.getEmail());
        result.setPhone(user.getPhone());

        return result;
    }

    private void isPermissionsCorrect(User user) throws FinATypeException {
        checkPermissions(user, null);
    }

    private void isPermissionsCorrect(User user, Collection<Permission> newRolePermissions) throws FinATypeException {
        checkPermissions(user, newRolePermissions);
    }

    private Set<Permission> collectPermissions(User user, Collection<Permission> additionalPermissions) {
        Set<Permission> allPermissions = new HashSet<>();

        if (user.getPermissions() != null) {
            allPermissions.addAll(user.getPermissions());
        }

        if (user.getRoles() != null) {
            user.getRoles().forEach(r -> allPermissions.addAll(r.getPermissions()));
        }

        if (additionalPermissions != null) {
            allPermissions.addAll(additionalPermissions);
        }

        return allPermissions;
    }

    private void checkPermissions(User user, Collection<Permission> additionalPermissions) throws FinATypeException {

        Set<Permission> allPermissions = collectPermissions(user, additionalPermissions);

        boolean invalidPermissions = allPermissions.containsAll(Arrays.asList(
                new Permission(PermissionIdNames.FINA_WEB_EXTERNAL_USER),
                new Permission(PermissionIdNames.FINA_WEB_INTERNAL_USER)
        ));
        if (invalidPermissions) {
            String langCode = ThreadLocalHolder.getLanguage().getCode();
            throw new FinATypeException(CommonUtil.compileMessageWithParams(
                    MessagesUtil.getString(FinATypeException.Type.INVALID_PERMISSIONS.getCode(), langCode),
                    user.getLogin()
            ));
        }
    }

    private void validateRoleFis(Role role, Role changedRole) throws FinATypeException {
        Collection<Fi> fis = role.getFis() == null ? changedRole.getFis() : role.getFis();
        Collection<Permission> permissions = role.getPermissions() == null ? changedRole.getPermissions() : role.getPermissions();
        fis = fis == null ? new ArrayList<>() : fis;
        permissions = permissions == null ? new ArrayList<>() : permissions;
        for (Permission p : permissions) {
            if (p.getIdName().equals(PermissionIdNames.FINA_WEB_EXTERNAL_USER) && !fis.isEmpty()) {
                throw new FinATypeException(MessagesUtil.getString("net.fina.web.externalUsersFisNotAllowed"));
            }
        }
    }

    private void checkUserIsProgrammaticallyDeleted(User user) throws FinATypeException {
        if (user.getId() <= 0) {
            List<Long> userIds = em.createQuery("select u.id from SYS_USERS u where trim(lower(u.login))=:login and u.deleted=true", Long.class).setParameter("login", user.getLogin()).getResultList();
            if (!userIds.isEmpty()) {
                throw new FinATypeException(FinATypeException.Type.USER_PROGRAMMATICALLY_DELETE, new String[]{String.valueOf(userIds.get(0))});
            }
        }
    }

    private void removeUsersFromFis(long userId, List<Long> fiIds) {
        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM SYS_BANK_USERS bu WHERE bu.userId  = :userId ");
        if (!fiIds.isEmpty()) {
            sb.append(" AND NOT ( ").append(DBUtil.get().generateConcatenatedInStatementWithIds("bu.bankId", fiIds)).append(" )");
        }

        Query query = em.createQuery(sb.toString());
        query.setParameter("userId", userId);

        query.executeUpdate();
    }

    private boolean hasPermission(Collection<Permission> permissions, String permCode) {
        if (permissions != null) {
            for (Permission pm : permissions) {
                if (pm.getIdName().equals(permCode)) {
                    return true;
                }
            }
        }
        return false;
    }


    @SuppressWarnings("JpaQlInspection")
    private List<Predicate> getUserFilterPredicate(CriteriaBuilder cb, Root<User> root, Map<UserFilterType, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        if (filter != null) {
            for (Map.Entry<UserFilterType, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case LOGIN:
                        predicates.add(cb.like(root.get(User_.login), "%" + entry.getValue() + "%"));
                        break;
                    case NAME:
                        List<Long> descriptions = em.createQuery("select u.id from SYS_STRINGS s inner join SYS_USERS u on u.description=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(User_.id).in(descriptions.isEmpty() ? Collections.singletonList(-1L) : descriptions));
                        break;
                    case TITLE:
                        List<Long> titleIds = em.createQuery("select u.id from SYS_STRINGS s inner join SYS_USERS u on u.titledescription=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();
                        predicates.add(root.get(User_.id).in(titleIds.isEmpty() ? Collections.singletonList(-1L) : titleIds));
                        break;
                    case CONTACT_PERSON:
                        List<Long> contactPersonIds = em.createQuery("select u.id from SYS_STRINGS s inner join SYS_USERS u on u.contactPersonDescription=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(User_.id).in(!contactPersonIds.isEmpty() ? contactPersonIds : Collections.singletonList(-1L)));
                        break;
                    case PHONE:
                        predicates.add(cb.like(root.get(User_.phone), "%" + entry.getValue() + "%"));
                        break;
                    case MAIL:
                        predicates.add(cb.like(root.get(User_.email), "%" + entry.getValue() + "%"));
                        break;

                    case SEARCH_VALUE:
                        List<Long> userIds = em.createQuery("select u.id from SYS_STRINGS ss inner join SYS_USERS u on u.description = ss.id " +
                                        "where ss.langId=:langId and (ss.value like :searchValue or u.login like :searchValue)", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("searchValue", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(User_.id).in(!userIds.isEmpty() ? userIds : Collections.singletonList(-1L)));
                        break;
                }
            }
        }

        return predicates;
    }

    @SuppressWarnings("JpaQlInspection")
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Role> root, Join<Role, User> userJoin, long roleId, String searchValue) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchValue != null && !searchValue.trim().isEmpty()) {
            long langId = ThreadLocalHolder.getLanguage().getId();
            List<Long> userIds = em.createQuery(
                            "select u.id from SYS_USERS u " +
                                    "left join SYS_STRINGS ss on u.description = ss.id and ss.langId = :langId " +
                                    "where (ss.id is not null and lower(trim(ss.value)) like :searchValue) " +
                                    "or lower(trim( u.login)) like :searchValue", Long.class)
                    .setParameter("langId", langId)
                    .setParameter("searchValue", "%" + searchValue.toLowerCase().trim() + "%")
                    .getResultList();


            predicates.add(userJoin.get(User_.id).in(!userIds.isEmpty() ? userIds : Collections.singletonList(-1L)));
        }

        predicates.add(cb.equal(root.get(Role_.id), roleId));

        return predicates;
    }

    private void validateUserRequiredFields(User user) throws FinATypeException {

        if (!checkLoginUnique(user)) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        //validations should be only for Fina Users
        if (user.getUserType() != null && user.getUserType() == UserType.FINA_USER) {
            String userRequiredFields = propertyLocal.getSystemProperty(PropertyKeys.USER_REQUIRED_FIELDS);

            if (userRequiredFields != null && !userRequiredFields.isBlank()) {
                String[] fields = userRequiredFields.split(",");
                List<String> fieldsList = Arrays.stream(fields).toList();

                if (fieldsList.contains("email")) {
                    validateUserEmail(user);
                }

                if (fieldsList.contains("phone")) {
                    validateUserPhone(user);
                }
            }
        }


    }


    private void validateUserPhone(User user) throws FinATypeException {
        List<Long> userIds = em.createNamedQuery("checkPhoneUnique", Long.class)
                .setParameter("id", user.getId())
                .setParameter("phone", user.getPhone())
                .getResultList();

        if (!userIds.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.PHONE_UNIQUE);
        }
    }

    private <T> boolean appendDependencyMessage(StringBuilder errorMessage, Collection<T> collection, String label, boolean hasDependencies) {
        if (collection != null && !collection.isEmpty()) {
            errorMessage.append("\n");
            errorMessage.append(label).append(": ")
                    .append(collection.stream().map(Object::toString).collect(Collectors.joining(",\n ")))
                    .append("\n");
            return true;
        }
        return hasDependencies;
    }

}
