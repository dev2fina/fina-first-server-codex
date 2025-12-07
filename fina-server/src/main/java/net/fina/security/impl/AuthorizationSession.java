package net.fina.security.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import net.fina.auditlog.api.AuditLogLocal;
import net.fina.auditlog.entity.AuditLog;
import net.fina.auditlog.impl.AuditLogBuilder;
import net.fina.common.client.constants.OperationType;
import net.fina.common.client.property.PropertyKeys;
import net.fina.security.api.AuthorizationLocal;
import net.fina.security.auth.CustomPrincipal;
import net.fina.security.util.SecurityUtil;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.Fi_;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.reports.entity.Report;
import net.fina.server.reports.entity.Report_;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnDefinition_;
import net.fina.server.security.entity.Role;
import net.fina.server.security.entity.Role_;
import net.fina.server.security.entity.User;
import net.fina.server.security.entity.User_;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.*;

@Stateless
@Local(AuthorizationLocal.class)
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
public class AuthorizationSession implements AuthorizationLocal {
    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private EntityManager em;

    @Inject
    private AuditLogLocal auditLogLocal;
    @Inject
    private LoginAttemptPolicyManager loginAttemptPolicyManager;

    @Resource(lookup = "java:jboss/infinispan/cache/fina-custom-principal-cache-container/fina-custom-principal-cache")
    private org.infinispan.Cache<String, CustomPrincipal> principalCache;

    @Resource
    private SessionContext sc;

    private int getSysPropertyValue(String key) {
        Query query = em.createQuery("select c.value from SYS_PROPERTIES c where c.propKey=:key").setParameter("key", key);
        return Integer.parseInt(query.getSingleResult().toString());
    }

    @Override
    public String findByLoginPassword(String username, String password) {
        User user;

        try {
            user = em.createQuery("select u from SYS_USERS as u where trim(LOWER(u.login))=:username and trim(u.password)=:password and u.deleted=:deleted", User.class)
                    .setParameter("username", username.toLowerCase())
                    .setParameter("password", password)
                    .setParameter("deleted", false).getSingleResult();
        } catch (PersistenceException ex) {
            log.error("User " + username + " cannot log in.");
            Integer incorrectLoginCounter = loginAttemptPolicyManager.get(username);
            log.warn("User " + username + " login attempt : " + (incorrectLoginCounter == null ? 1 : incorrectLoginCounter + 1));
            if (isBlockUser(username)) {
                blockUser(username);
            }
            return null;
        }

        if (user.getBlocked()) {
            log.error("Login failed. User account is blocked. User name: " + username);
            return null;
        } else if (user.isDisabled()) {
            log.error("Login failed. User account is disabled. User name: " + username);
            return null;
        } else {
            int allowedAccountInactivityPeriod = getSysPropertyValue(PropertyKeys.ALLOWED_ACCOUNT_INACTIVITY_PERIOD);

            Calendar lastLogin = Calendar.getInstance();
            Date lastLoginDate = user.getLastLoginDate();
            if (lastLoginDate == null) {
                lastLoginDate = new Date();
            }
            lastLogin.setTime(lastLoginDate);
            lastLogin.add(Calendar.DAY_OF_YEAR, allowedAccountInactivityPeriod);

            Calendar currTime = Calendar.getInstance();

            if (!lastLogin.after(currTime) && allowedAccountInactivityPeriod != -1) {
                blockUser(user.getId(), user.getLogin());
                log.info("Account inactivity period is overdue. User name: " + username);
                return null;
            } else {
                log.info("User " + username + " Logged in successfully, clearing login attempt cache.");
                loginAttemptPolicyManager.remove(username);
                log.info("User " + username + " Set Last Login Date");
                updateUserLastLoginDate(user.getId());
                log.info("User " + username + " Updated Last Login Date");

                int passValPeriod = getSysPropertyValue(PropertyKeys.PASSWORD_VALIDITY_PERIOD);

                Calendar lastPwddChangeDate = Calendar.getInstance();
                if (user.getLastPasswordChangeDate() != null) {
                    lastPwddChangeDate.setTime(user.getLastPasswordChangeDate());
                }
                lastPwddChangeDate.add(Calendar.DAY_OF_YEAR, passValPeriod);

                if (passValPeriod != -1 && (user.getLastPasswordChangeDate() == null || (!lastPwddChangeDate.after(currTime)))) {
                    log.info("Password expired. Forcing user to change password. User name: " + username);
                    setChangePassword(user.getId());
                }
            }
        }
        return user.getPassword();
    }

    private void blockUser(String username) {
        log.warn("User " + username + " tried to log in " + loginAttemptPolicyManager.get(username) + " times, Blocking it!");
        loginAttemptPolicyManager.remove(username);
        em.createQuery("update SYS_USERS set blocked=:blocked, lastLoginDate=:lastLoginDate where trim(LOWER(login))=:username")
                .setParameter("username", username.toLowerCase())
                .setParameter("blocked", true)
                .setParameter("lastLoginDate", new Date())
                .executeUpdate();
        SecurityUtil.flushDcsUserAuthCache(username);
    }

    private void blockUser(long userId, String login) {
        em.createQuery("update SYS_USERS set blocked=:blocked where id=:userId")
                .setParameter("userId", userId)
                .setParameter("blocked", true)
                .executeUpdate();
        SecurityUtil.flushDcsUserAuthCache(login);
    }

    private void updateUserLastLoginDate(long userId) {
        em.createQuery("update SYS_USERS set lastLoginDate=:lastLoginDate where id=:userId")
                .setParameter("userId", userId)
                .setParameter("lastLoginDate", new Date())
                .executeUpdate();
    }

    private void setChangePassword(long userId) {
        em.createQuery("update SYS_USERS set changePassword=:changePassword where id=:userId")
                .setParameter("userId", userId)
                .setParameter("changePassword", true)
                .executeUpdate();
    }

    @Override
    public String findByLoginPasswordSimple(String username, String password) {
        Query query = em.createQuery("select u.login from SYS_USERS u where trim(LOWER(u.login))=:username and trim(u.password)=:password and u.blocked=:blocked and u.deleted=:deleted and u.disabled=:disabled");
        query.setParameter("username", username.toLowerCase());
        query.setParameter("password", password);
        query.setParameter("blocked", false);
        query.setParameter("deleted", false);
        query.setParameter("disabled", false);
        return query.getSingleResult().toString();
    }

    private boolean isBlockUser(String username) {

        Integer loginAttemptNumber = loginAttemptPolicyManager.get(username);

        if (loginAttemptNumber == null) {
            loginAttemptNumber = 1;
        } else {
            loginAttemptNumber = loginAttemptNumber + 1;
        }

        loginAttemptPolicyManager.put(username, loginAttemptNumber);

        return loginAttemptNumber >= getSysPropertyValue(PropertyKeys.ALLOWED_LOGIN_ATTEMPT_NUMBER);
    }

    @SuppressWarnings("unchecked")
    public List<String> loadUserPermission(String login) {
        String loadUserPermissiosSql = "SELECT DISTINCT PERMS.ID AS BYROLE " + " FROM SYS_USERS_ROLES USERROLES, " + " SYS_ROLE_PERMISSIONS ROLEPERMS," + " SYS_PERMISSIONS      PERMS, " + " SYS_USERS USRS " + " WHERE USRS.ID = USERROLES.USERID " + " AND USERROLES.ROLEID = ROLEPERMS.ROLEID" + " AND ROLEPERMS.PERMISSIONID = PERMS.ID " + " AND ltrim(rtrim(LOWER(USRS.LOGIN))) =? " + " UNION  "
                + "SELECT DISTINCT PERM.ID AS BYUSER " + " FROM SYS_USERS USRS, " + " SYS_USER_PERMISSIONS USRPERMS," + " SYS_PERMISSIONS PERM  " + "WHERE USRS.ID = USRPERMS.USERID  " + " AND USRPERMS.PERMISSIONID = PERM.ID  " + " AND ltrim(rtrim(LOWER(USRS.LOGIN))) =?";
        Query query = em.createNativeQuery(loadUserPermissiosSql);
        query.setParameter(1, login.trim().toLowerCase());
        query.setParameter(2, login.trim().toLowerCase());
        List<BigDecimal> userPermIds = query.getResultList();

        List<String> result = new ArrayList<String>();

        if (userPermIds.size() > 0) {
            Query idNamesQuery = em.createQuery("SELECT PERM.idName FROM SYS_PERMISSIONS PERM WHERE PERM.id IN " + userPermIds.toString().replace('[', '(').replace(']', ')'));
            result = idNamesQuery.getResultList();
        }

        return result;
    }

    @Override
    public CustomPrincipal getUserCustomPrincipal(String login) {
        login = login.trim().toLowerCase();
        long userId = em.createQuery("select u.id from SYS_USERS u where trim(LOWER(u.login))=:login", Long.class).setParameter("login", login).getSingleResult();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        Map<Long, Boolean> returnVersions = new HashMap<>();

        // User Fis
        CriteriaQuery<Long> userFisQuery = cb.createQuery(Long.class);
        Root<User> userFiRoot = userFisQuery.from(User.class);
        Join<User, Fi> userFiJoin = userFiRoot.join(User_.fis);
        userFisQuery.select(userFiJoin.get(Fi_.id));
        userFisQuery.where(cb.equal(cb.trim(cb.lower(userFiRoot.get(User_.login))), login));
        Set<Long> fis = new HashSet<>(em.createQuery(userFisQuery).getResultList());

        //user Group Fis
        CriteriaQuery<Long> userRolesFisQuery = cb.createQuery(Long.class);
        Root<User> userAndRolesRoot = userRolesFisQuery.from(User.class);
        Join<User, Role> userAndRoleJoin = userAndRolesRoot.join(User_.roles);
        Join<Role, Fi> roleFisJoin = userAndRoleJoin.join(Role_.fis);
        userRolesFisQuery.select(roleFisJoin.get(Fi_.id));
        userRolesFisQuery.where(cb.equal(cb.trim(cb.lower(userAndRolesRoot.get(User_.login))), login));
        fis.addAll(em.createQuery(userRolesFisQuery).getResultList());

        // User Return Definitions
        CriteriaQuery<Long> userReturnDefinitionsQuery = cb.createQuery(Long.class);
        Root<User> userReturnDefinitionsRoot = userReturnDefinitionsQuery.from(User.class);
        Join<User, ReturnDefinition> userReturnDefinitionJoin = userReturnDefinitionsRoot.join(User_.returnDefinitions);
        userReturnDefinitionsQuery.select(userReturnDefinitionJoin.get(ReturnDefinition_.id));
        userReturnDefinitionsQuery.where(cb.equal(cb.trim(cb.lower(userReturnDefinitionsRoot.get(User_.login))), login));
        Set<Long> returnDefinitions = new HashSet<>(em.createQuery(userReturnDefinitionsQuery).getResultList());

        // User Role Return Definitions
        CriteriaQuery<Long> userRolesReturnDefinitionsQuery = cb.createQuery(Long.class);
        Root<User> userRolesRoot = userRolesReturnDefinitionsQuery.from(User.class);
        Join<User, Role> userRoleJoin = userRolesRoot.join(User_.roles);
        Join<Role, ReturnDefinition> roleReturnDefinitionJoin = userRoleJoin.join(Role_.returnDefinitions);
        userRolesReturnDefinitionsQuery.select(roleReturnDefinitionJoin.get(ReturnDefinition_.id));
        userRolesReturnDefinitionsQuery.where(cb.equal(cb.trim(cb.lower(userRolesRoot.get(User_.login))), login));
        returnDefinitions.addAll(em.createQuery(userRolesReturnDefinitionsQuery).getResultList());

        // User Return versions
        List<Object[]> userReturnVersions = em.createQuery("select rv.returnVersionId.returnVersion.id,rv.canAmend from SYS_USERS u, IN(u.returnVersions) rv where trim(lower( u.login))=:login", Object[].class)
                .setParameter("login", login)
                .getResultList();
        serReturnVersions(userReturnVersions, returnVersions);

        // User Role Return Version
        List<Object[]> userRoleReturnVersions = em.createQuery("select rv.returnVersionId.returnVersion.id,rv.canAmend from SYS_USERS  u, IN(u.roles) r, IN(r.returnVersions) rv where trim(lower(u.login))=:login", Object[].class)
                .setParameter("login", login)
                .getResultList();
        serReturnVersions(userRoleReturnVersions, returnVersions);

        // User Reports
        CriteriaQuery<Integer> userReportsQuery = cb.createQuery(Integer.class);
        Root<User> userReportsRoot = userReportsQuery.from(User.class);
        Join<User, Report> userReportJoin = userReportsRoot.join(User_.reports);
        userReportsQuery.select(userReportJoin.get(Report_.id));
        userReportsQuery.where(cb.equal(cb.trim(cb.lower(userReportsRoot.get(User_.login))), login));
        Set<Integer> reports = new HashSet<>(em.createQuery(userReportsQuery).getResultList());

        // User Role Reports
        CriteriaQuery<Integer> userRoleReportsQuery = cb.createQuery(Integer.class);
        Root<User> userRoleReportRoot = userRoleReportsQuery.from(User.class);
        Join<User, Role> userRoleReportJoin = userRoleReportRoot.join(User_.roles);
        Join<Role, Report> roleReportJoin = userRoleReportJoin.join(Role_.reports);
        userRoleReportsQuery.select(roleReportJoin.get(Report_.id));
        userRoleReportsQuery.where(cb.equal(cb.trim(cb.lower(userRoleReportRoot.get(User_.login))), login));
        reports.addAll(em.createQuery(userRoleReportsQuery).getResultList());

        List<Long> roleIds = em.createNativeQuery("select ROLEID from SYS_USERS_ROLES  where USERID=:userId", Long.class)
                .setParameter("userId", userId)
                .getResultList();

        // Check Empty collections
        if (fis.isEmpty()) {
            fis.add(0L);
        }
        if (returnDefinitions.isEmpty()) {
            returnDefinitions.add(0L);
        }
        if (returnVersions.isEmpty()) {
            returnVersions.put(0L, false);
        }
        if (reports.isEmpty()) {
            reports.add(0);
        }
        if (roleIds.isEmpty()) {
            roleIds.add(0L);
        }

        //Audit Log
        AuditLog auditLog = new AuditLogBuilder().build(login, OperationType.LOGIN, new User(), login, new Object[]{new Date()}, null, new String[]{"login date"});
        if (auditLog != null) {
            auditLogLocal.storeAuditLog(auditLog);
        }

        return new CustomPrincipal(userId, login, fis, returnDefinitions, returnVersions, reports, roleIds);
    }

    @Override
    public boolean hasUserPermission(String login, String permission) {
        return !em.createNativeQuery("SELECT PERMS.ID FROM SYS_USERS_ROLES USERROLES, SYS_ROLE_PERMISSIONS ROLEPERMS, SYS_PERMISSIONS PERMS, SYS_USERS USRS " +
                        "WHERE USRS.ID = USERROLES.USERID " +
                        "AND USERROLES.ROLEID = ROLEPERMS.ROLEID " +
                        "AND ROLEPERMS.PERMISSIONID = PERMS.ID " +
                        "AND ltrim(rtrim(LOWER(USRS.LOGIN))) =:login " +
                        "and perms.id=(select id from SYS_PERMISSIONS where idname=:IDNAME) " +
                        "UNION " +
                        "SELECT PERM.ID AS BYUSER " +
                        "FROM SYS_USERS USRS, SYS_USER_PERMISSIONS USRPERMS, SYS_PERMISSIONS PERM " +
                        "WHERE USRS.ID = USRPERMS.USERID " +
                        "AND USRPERMS.PERMISSIONID = PERM.ID " +
                        "AND ltrim(rtrim(LOWER(USRS.LOGIN))) =:login " +
                        "and perm.id=(select id from SYS_PERMISSIONS where idname=:IDNAME)")
                .setParameter("login", login.toLowerCase())
                .setParameter("IDNAME", permission).getResultList().isEmpty();
    }

    @Override
    @LogDescription(ignore = true)
    public CustomPrincipal getCallerPrincipal() {
        if (sc.getCallerPrincipal() instanceof CustomPrincipal) {
            return (CustomPrincipal) sc.getCallerPrincipal();
        }

        String login = sc.getCallerPrincipal().getName().toLowerCase().trim();

        if (principalCache.containsKey(login)) {
            return principalCache.get(login);
        } else {
            CustomPrincipal cp = getUserCustomPrincipal(login);
            principalCache.put(login, cp);
            return cp;
        }

    }

    @Override
    public boolean checkUserExist(String login) {
        return !em.createQuery("select u.login from SYS_USERS u where trim(LOWER(u.login))=:username", String.class)
                .setParameter("username", login.toLowerCase())
                .getResultList().isEmpty();
    }

    @Override
    public boolean checkUserDisabledOrBlocked(String login) {
        return !em.createQuery("select u.login from SYS_USERS u where trim(LOWER(u.login))=:username and (u.disabled=true or u.blocked=true)", String.class)
                .setParameter("username", login.toLowerCase())
                .getResultList().isEmpty();
    }

    @Override
    public void flushPrincipalCache(String login) {
        principalCache.remove(login.trim().toLowerCase());
    }

    private void serReturnVersions(List<Object[]> versionObjects, Map<Long, Boolean> returnVersions) {
        for (Object[] objects : versionObjects) {
            Boolean canAmendExisting = returnVersions.get((long) objects[0]);
            canAmendExisting = canAmendExisting != null && canAmendExisting;
            returnVersions.put((long) objects[0], canAmendExisting || (objects[1] != null ? (Boolean) objects[1] : false));
        }
    }
}
