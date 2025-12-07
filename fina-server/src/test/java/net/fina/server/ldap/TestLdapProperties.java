package net.fina.server.ldap;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.text.MessageFormat;
import java.util.Hashtable;

/**
 * Created by fina on 9/4/2016.
 */
@Ignore
public class TestLdapProperties {


    private static final String URL = "ldap://192.168.88.101:10389";
    private static final String AUTH_TYPE = "simple";
    private static final String BASE_DN = "uid=admin,ou=system";
    private static Hashtable<String, String> LDAP_ENVIRONMENT = new Hashtable<>();
    private static LdapContext LDAP_CONTEXT;
    private static final int pageSize = 100;

    @BeforeClass
    public static void init() throws Exception {
        LDAP_ENVIRONMENT = new Hashtable<String, String>(11);
        LDAP_ENVIRONMENT.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        //ldapEnv.put(Context.PROVIDER_URL,  "ldap://societe.fr:389");
        LDAP_ENVIRONMENT.put(Context.PROVIDER_URL, URL);
        LDAP_ENVIRONMENT.put(Context.SECURITY_AUTHENTICATION, AUTH_TYPE);
        //for ms active directory
        // IMPORTANT: sometimes ms active directory takes login name + firstname as login attribute
        //ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=administrateur,cn=users,dc=societe,dc=fr");
        LDAP_ENVIRONMENT.put(Context.SECURITY_PRINCIPAL, BASE_DN);
        LDAP_ENVIRONMENT.put(Context.SECURITY_CREDENTIALS, "fina2demo");
        //ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
        //ldapEnv.put(Context.SECURITY_PROTOCOL, "simple");

        LDAP_CONTEXT = new InitialLdapContext(LDAP_ENVIRONMENT, new Control[]{new PagedResultsControl(pageSize,
                Control.NONCRITICAL)});

    }

    @AfterClass
    public static void done() throws NamingException {
        LDAP_CONTEXT.close();
    }

    @Test
    public void listUsers() throws Exception {
        // Create the search controls
        SearchControls searchCtls = new SearchControls();

        //Specify the attributes to return
        String[] returnedAtts = {"uid", "userPassword", "name", "description", "cn", "mail", "telephoneNumber"};
        searchCtls.setReturningAttributes(returnedAtts);

        //Specify the search scope
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        //specify the LDAP search filter
        String searchFilter = "(&(objectClass=person))";
//            String searchFilterForMsActiveDirectory = "(sAMAccountName={0})";

        /*
         * MS Active Direcotry Syntax
         * search Filter for 2 oraganizational units group users
         */
//            String searchFilter = "(&(objectCategory=Person)(sAMAccountName=*)(|(memberOf=cn=superAdmins,ou=IT Department,dc=fina,dc=ge)(memberOf=cn=internal,ou=Stat Department,dc=fina,dc=ge)))";


        //Specify the Base for the search
        String searchBase = "ou=system";
        //initialize counter to total the results
        printResult(LDAP_CONTEXT.search(searchBase, searchFilter, searchCtls));
    }


    private byte[] prepareNextPage(LdapContext ldapContext) throws Exception {
        Control[] responseControls = ldapContext.getResponseControls();

        byte[] cookie = null;
        if (responseControls != null) {
            for (Control responseControl : responseControls) {
                if (responseControl instanceof PagedResultsResponseControl prrc) {
                    cookie = prrc.getCookie();
                    int totalSize = prrc.getResultSize();
                    System.out.println(cookie);
                }
            }
        }

        if (cookie != null) {
            ldapContext.setRequestControls(new Control[]{new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});
        }
        return cookie;
    }


    @Test
    public void searchUserEquality() throws Exception {
        String user = "test";
        String searchFilter = "(&(objectClass=person))(|(uid=" + user + ")(description=oto))";

        SearchControls searchCtls = new SearchControls();

        String[] returnedAtts = {"uid", "userPassword", "name", "description", "cn", "mail", "telephoneNumber"};
        searchCtls.setReturningAttributes(returnedAtts);

        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String searchBase = "ou=system";

        printResult(LDAP_CONTEXT.search(searchBase, searchFilter, searchCtls));
    }

    @Test
    public void searchUserLike() throws NamingException {
        String text = "ji";
        String searchFilter = MessageFormat.format("(|(uid=*{0}*)(description=*{0}*)(sn=*{0}*)(cn=*{0}*)(mail=*{0}*)(telephoneNumber=*{0}*))", text);


        SearchControls searchCtls = new SearchControls();

        String[] returnedAtts = {"uid", "userPassword", "name", "description", "cn", "mail", "telephoneNumber"};
        searchCtls.setReturningAttributes(returnedAtts);

        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String searchBase = "ou=system";

        printResult(LDAP_CONTEXT.search(searchBase, searchFilter, searchCtls));
    }


    private void printResult(NamingEnumeration<SearchResult> searchResult) throws NamingException {
        int totalResults = 0;

        //Loop through the search results
        while (searchResult.hasMoreElements()) {
            SearchResult sr = searchResult.next();

            totalResults++;
            System.out.println("====================================");
            System.out.println(">>>>>> " + sr.getName());
            Attributes attrs = sr.getAttributes();
            System.out.println(">>>>>> " + attrs.get("cn"));
            System.out.println(">>>>>> " + attrs.get("mail"));
            System.out.println(">>>>>> " + attrs.get("description"));
        }

        System.out.println("Total results: " + totalResults);

    }


    @Test
    public void testPaging() throws Exception {
        try {
            LdapContext ctx = LDAP_CONTEXT;

            // Activate paged results
            int pageSize = 100;
            byte[] cookie = null;
            ctx.setRequestControls(new Control[]{new PagedResultsControl(pageSize,
                    Control.CRITICAL)});
            int total = 0;
            int countrer = 0;

            do {
                SearchControls searchCtls = new SearchControls();

                String[] returnedAtts = {"uid", "userPassword", "name", "description", "cn", "mail", "telephoneNumber"};
                searchCtls.setReturningAttributes(returnedAtts);

                searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                /* perform the search */
                NamingEnumeration results = ctx.search("", "(objectclass=person)",
                        searchCtls);

                /* for each entry print out name + all attrs and values */
                while (results.hasMoreElements()) {
                    SearchResult sr = (SearchResult) results.next();
                    countrer++;
                    System.out.println("====================================");
                    System.out.println(">>>>>> " + sr.getName());
                    Attributes attrs = sr.getAttributes();
                    System.out.println(">>>>>> " + attrs.get("cn"));
                    System.out.println(">>>>>> " + attrs.get("mail"));
                    System.out.println(">>>>>> " + attrs.get("description"));
                }
                // Examine the paged results control response
                Control[] controls = ctx.getResponseControls();
                if (controls != null) {
                    for (int i = 0; i < controls.length; i++) {
                        if (controls[i] instanceof PagedResultsResponseControl prrc) {
                            total = prrc.getResultSize();

                            cookie = prrc.getCookie();
                        }
                    }
                } else {
                    System.out.println("No controls were sent from the server");
                }
                // Re-activate paged results
                ctx.setRequestControls(new Control[]{new PagedResultsControl(
                        pageSize, cookie, Control.CRITICAL)});

            } while (cookie != null);
            System.out.println(countrer);

            ctx.close();

        } catch (NamingException e) {
            System.err.println("PagedSearch failed.");
            System.err.println(e.getMessage());
        }

    }

}