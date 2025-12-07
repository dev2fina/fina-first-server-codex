package net.fina.security.util;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.AuthorizationType;
import net.fina.elytron.AuthenticationConstants;
import net.fina.security.api.AuthorizationLocal;
import net.fina.security.impl.AuthorizationSession;
import org.jboss.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class SecurityUtil {
    private static final Logger log = Logger.getLogger(SecurityUtil.class.getName());

    public static String generateGlobalJndiName(InitialContext initialContext, Class<?> bean, Class<?> local) throws NamingException {
        String appName = (String) initialContext.lookup("java:app/AppName");
        String module = (String) initialContext.lookup("java:module/ModuleName");

        boolean test = appName.equalsIgnoreCase("test");

        if (!module.contains("fina-server")) {
            module = "fina-server";
        }

        String beanSimpleName = bean.getSimpleName();
        String beanName = local.getName();

        StringBuilder sb = new StringBuilder();

        sb.append("java:global");
        sb.append("/");
        sb.append(appName);
        sb.append("/");

        if (!test) {
            sb.append(module);
            sb.append("/");
        }

        sb.append(beanSimpleName);
        sb.append("!");
        sb.append(beanName);

        return sb.toString();
    }

    public static String encodePassword(String pass) {
        String encodedPass = null;
        if (pass != null) {
            byte[] password = pass.getBytes();
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e) {
                Logger.getLogger("FinALoginModule").error(e.getMessage(), e);
            }
            password = md.digest(password);

            StringBuffer str = new StringBuffer();
            for (int i = 0; i < password.length; i++) {
                str.append(Integer.toHexString((password[i] & 0xf0) >> 4));
                str.append(Integer.toHexString(password[i] & 0x0f));
            }
            encodedPass = str.toString();
        }
        return encodedPass;
    }

    public static void flushAuthCache(String login) {
        Logger log = Logger.getLogger(login);
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            server.invoke(new ObjectName(
                            "jboss.as:subsystem=elytron,caching-realm=finaCacheRealm"),
                    "clearCache",
                    new String[]{}, new String[]{});
            getAuthorizationLocal().flushPrincipalCache(login);
            log.info("Flush auth cache:" + login);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.warn("Error flushing authenticate cache", ex);
        }
    }

    public static <T, V> T lookupBeanInJNDI(Class<T> t, Class<V> v) throws LoginException {
        try {
            InitialContext ic = new InitialContext();
            Object object = ic.lookup(generateGlobalJndiName(ic, v, t));
            T result = (T) object;
            return result;
        } catch (NamingException e) {
            throw new LoginException(e.toString(true));
        }
    }


    public static List<String> getDaemonUserPermission() {
        return Arrays.asList(AuthenticationConstants.FINA_WS_USER,
                PermissionIdNames.FINA_USER_REVIEW,
                PermissionIdNames.EMS_SYNC_PERMISSION);
    }

    public static void flushDcsUserAuthCache(String user) {
        flushAuthCache(user);
    }

    private static AuthorizationLocal getAuthorizationLocal() throws LoginException {
        try {
            InitialContext ic = new InitialContext();
            Object object = ic.lookup(SecurityUtil.generateGlobalJndiName(ic, AuthorizationSession.class, AuthorizationLocal.class));
            AuthorizationLocal authorizationLocal = (AuthorizationLocal) object;
            return authorizationLocal;
        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new LoginException(e.toString(true));
        }
    }

    public static AuthorizationType getAuthTypeEnum(String authTypeProperty) {
        try {
            return AuthorizationType.valueOf(authTypeProperty.trim().toUpperCase());
        } catch (Throwable Ignore) {
            log.warn("authorization type property not set or invalid - " + authTypeProperty);
        }
        return AuthorizationType.FINA;
    }

}
