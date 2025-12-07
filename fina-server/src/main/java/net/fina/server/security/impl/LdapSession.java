package net.fina.server.security.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.constants.LdapSearchScope;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.EncryptionUtil;
import net.fina.common.shared.LdapUserMetaModel;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.LdapLocal;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import java.text.MessageFormat;
import java.util.*;


@Stateless
@Local(LdapLocal.class)
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
public class LdapSession implements LdapLocal {
    private static final int PAGE_SIZE = 999;
    private final Logger log = Logger.getLogger(LdapSession.class.getSimpleName());
    private Map<String, String> ldapProperties = new HashMap<>();
    private Set<String> attributes = new HashSet<>(Arrays.asList("givenName", "description", "sn", "cn", "mail", "telephoneNumber"));
    private Map<String, String> attributeNameMap = new HashMap<>();

    @Inject
    private UserLocal userLocal;

    @Inject
    private PropertyLocal propertyLocal;


    @Override
    public void init() {
        postConstruct();
    }

    @PostConstruct
    public void postConstruct() {
        initLdapEnvironment(propertyLocal.getSystemProperty());
    }

    public List<LdapUserMetaModel> getLdapUsers(String searchText) {
        String OU = ldapProperties.get("OU");
        Hashtable<String, String> env = getLdapProperties(ldapProperties);
        List<LdapUserMetaModel> data = new ArrayList<>();
        LdapUserMetaModel user;
        String loginAttribute = ldapProperties.get(PropertyKeys.LDAP_LOGIN_ATTRIBUTE_NAME);
        String[] attributes = this.attributes.toArray(new String[0]);
        String filter = constructFilter(ldapProperties);

        if (searchText != null && !searchText.trim().isEmpty()) {
            filter = getSearchFilter(searchText);
        }

        LdapContext dirContext;

        LdapSearchScope searchScope = getValue(ldapProperties.get(PropertyKeys.LDAP_SEARCH_SCOPE));

        try {
            byte[] cookie = null;


            dirContext = new InitialLdapContext(env, null);
            SearchControls controls = new SearchControls();
            controls.setReturningAttributes(attributes);
            controls.setSearchScope(searchScope.ordinal());

            do {
                dirContext.setRequestControls(new Control[]{
                        new PagedResultsControl(PAGE_SIZE, cookie, Control.CRITICAL)
                });
                NamingEnumeration<SearchResult> result = dirContext.search(OU, filter, controls);

                List<String> existingUsers = userLocal.getAllUsersLogin(true);

                while (result.hasMoreElements()) {
                    SearchResult sr = result.next();
                    Attributes attr = sr.getAttributes();

                    String userLogin = attr.get(loginAttribute) != null && attr.get(loginAttribute).get() != null ? String.valueOf(attr.get(loginAttribute).get()) : null;

                    if (userLogin != null && !existingUsers.contains(userLogin)) {
                        user = constructUser(attr, loginAttribute);

                        user.setType(LdapUserMetaModel.Type.USER);
                        user.setBlocked(false);
                        user.setPasswordChanged(false);

                        data.add(user);
                    }
                }
                cookie = null;
                cookie = prepareNextPage(dirContext);

            } while (cookie != null);

            dirContext.close();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return data;
    }


    public LdapUserMetaModel getLdapUser(String uid) {
        String filter = constructFilter(ldapProperties);
        String searchAttr = "(" + ldapProperties.get(PropertyKeys.LDAP_LOGIN_ATTRIBUTE_NAME) + "=" + uid + ")";
        filter = "(&" + filter + searchAttr + ")";

        List<LdapUserMetaModel> result = findUsers(filter);

        LdapUserMetaModel user = new LdapUserMetaModel();
        user.setId(-1);

        return !result.isEmpty() ? result.get(0) : user;
    }


    private Map<String, String> initLdapEnvironment(Map<String, String> properties) {
        Map<String, String> env = new HashMap<String, String>();
        String DN = "distinguished name";
        String PW = "password";
        String AS = "simple";
        String OU = "organizational unit";
        String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
        String HOST = "ldap://localhost:389";
        String FILTER = "(objectClass=person)";
        String LOGIN_ATRIBUTE = "uid";
        String LDAP_SEARCH_SCOPE = "SUBTREE_SCOPE";

        if (properties != null && !properties.isEmpty()) {
            if (check(properties, PropertyKeys.LDAP_URL_IP) && check(properties, PropertyKeys.LDAP_URL_PORT)) {
                HOST = "ldap://" + properties.get(PropertyKeys.LDAP_URL_IP) + ":" + properties.get(PropertyKeys.LDAP_URL_PORT);
            }
            if (check(properties, PropertyKeys.LDAP_DOMAIN_COMPONENT)) {
                DN = properties.get(PropertyKeys.LDAP_DOMAIN_COMPONENT);
            }
            if (check(properties, PropertyKeys.LDAP_ADMIN_PASSWORD)) {
                try {
                    PW = new EncryptionUtil().decrypt(properties.get(PropertyKeys.LDAP_ADMIN_PASSWORD));
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            }
            if (check(properties, PropertyKeys.LDAP_AUTHENTICATION_SECURITY)) {
                AS = properties.get(PropertyKeys.LDAP_AUTHENTICATION_SECURITY);
            }
            if (check(properties, PropertyKeys.LDAP_ORGANIZATIONAL_UNIT)) {
                OU = properties.get(PropertyKeys.LDAP_ORGANIZATIONAL_UNIT);
            }
            if (check(properties, PropertyKeys.LDAP_ATTRIBUTE_FILTER)) {
                FILTER = properties.get(PropertyKeys.LDAP_ATTRIBUTE_FILTER);
            }
            if (check(properties, PropertyKeys.LDAP_LOGIN_ATTRIBUTE_NAME)) {
                LOGIN_ATRIBUTE = properties.get(PropertyKeys.LDAP_LOGIN_ATTRIBUTE_NAME);
            }
            if (check(properties, PropertyKeys.LDAP_SEARCH_SCOPE)) {
                LDAP_SEARCH_SCOPE = properties.get(PropertyKeys.LDAP_SEARCH_SCOPE);
            }
            if (check(properties, PropertyKeys.LDAP_ATTRIBUTE_NAMES)) {
                initAttributes(properties.get(PropertyKeys.LDAP_ATTRIBUTE_NAMES));
            }
        }
        env.put("HOST", HOST);
        env.put("DN", DN);
        env.put("PW", PW);
        env.put("AS", AS);
        env.put("OU", OU);
        env.put("INITCTX", INITCTX);
        env.put("FILTER", FILTER);
        env.put(PropertyKeys.LDAP_LOGIN_ATTRIBUTE_NAME, LOGIN_ATRIBUTE);
        env.put(PropertyKeys.LDAP_SEARCH_SCOPE, LDAP_SEARCH_SCOPE);

        ldapProperties = env;
        attributes.add(LOGIN_ATRIBUTE);
        return env;
    }

    private void initAttributes(String attributesJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(attributesJson, Map.class);
            this.attributes = new HashSet<>(map.values());
            this.attributeNameMap = map;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private Hashtable<String, String> getLdapProperties(Map<String, String> properties) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, properties.get("INITCTX"));
        env.put(Context.PROVIDER_URL, properties.get("HOST"));
        env.put(Context.SECURITY_AUTHENTICATION, properties.get("AS"));
        env.put(Context.SECURITY_PRINCIPAL, properties.get("DN"));
        env.put(Context.SECURITY_CREDENTIALS, properties.get("PW"));

        return env;
    }

    private boolean check(Map<String, String> properties, String property) {
        return properties.get(property) != null && !properties.get(property).isEmpty();
    }


    private String constructFilter(Map<String, String> properties) {
        final String DEFAULT_FILTER = "";
        String filter = properties.get("FILTER");
        return (filter == null || filter.isEmpty()) ? DEFAULT_FILTER : filter;
    }

    private LdapUserMetaModel constructUser(Attributes attr, String loginAttribute) throws NamingException {
        LdapUserMetaModel user = new LdapUserMetaModel();
        user.setLogin(getSafeAttributeValue(attr, loginAttribute));
        user.setTitle(getSafeAttributeValue(attr, attributeNameMap.get("titleAttribute")));
        String firstName = getSafeAttributeValue(attr, attributeNameMap.get("firstNameAttribute"));
        String lastName = getSafeAttributeValue(attr, attributeNameMap.get("lastNameAttribute"));
        String fullName = firstName + " " + lastName;
        user.setName(fullName.trim().isEmpty() ? "NONAME" : fullName);
        user.setEmail(getSafeAttributeValue(attr, attributeNameMap.get("mailAttribute")));
        user.setPhone(getSafeAttributeValue(attr, attributeNameMap.get("phoneAttribute")));
        return user;
    }

    private List<LdapUserMetaModel> findUsers(String filter) {
        List<LdapUserMetaModel> result = new ArrayList<>();


        String OU = ldapProperties.get("OU");
        Hashtable<String, String> env = getLdapProperties(ldapProperties);
        String loginAttribute = ldapProperties.get(PropertyKeys.LDAP_LOGIN_ATTRIBUTE_NAME);

        String[] attributes = this.attributes.toArray(new String[0]);

        LdapSearchScope searchScope = getValue(ldapProperties.get(PropertyKeys.LDAP_SEARCH_SCOPE));

        DirContext dirContext;
        SearchResult sr;
        try {
            dirContext = new InitialDirContext(env);
            SearchControls controls = new SearchControls();
            controls.setReturningAttributes(attributes);
            controls.setSearchScope(searchScope.ordinal());
            NamingEnumeration<SearchResult> searchResult = dirContext.search(OU, filter, controls);
            while (searchResult.hasMoreElements()) {
                sr = searchResult.next();
                Attributes attr = sr.getAttributes();

                LdapUserMetaModel user = constructUser(attr, loginAttribute);
                result.add(user);
            }


            dirContext.close();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return result;
    }


    private byte[] prepareNextPage(LdapContext ldapContext) throws Exception {
        Control[] responseControls = ldapContext.getResponseControls();

        byte[] cookie = null;
        if (responseControls != null) {
            for (Control responseControl : responseControls) {
                if (responseControl instanceof PagedResultsResponseControl) {
                    PagedResultsResponseControl prrc = (PagedResultsResponseControl) responseControl;
                    cookie = prrc.getCookie();
                }
            }
        }

        return cookie;
    }

    private String getSearchFilter(String filterText) {
        return MessageFormat.format("(|(" + ldapProperties.get(PropertyKeys.LDAP_LOGIN_ATTRIBUTE_NAME) + "=*{0}*)(description=*{0}*)(sn=*{0}*)(cn=*{0}*)(mail=*{0}*)(telephoneNumber=*{0}*))", filterText);
    }

    private String getSafeAttributeValue(Attributes attr, String key) throws NamingException {
        return attr.get(key) != null ? String.valueOf(attr.get(key).get()) : "";
    }

    /**
     * Get search scope by param.
     *
     * @param param parameter.
     * @return search scope.
     */
    private LdapSearchScope getValue(String param) {
        try {
            return LdapSearchScope.valueOf(param.toUpperCase());
        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }
        return LdapSearchScope.SUBTREE_SCOPE;
    }
}
