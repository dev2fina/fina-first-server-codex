package net.fina.server.security.api;

import net.fina.common.shared.LdapUserMetaModel;

import java.util.List;

public interface LdapLocal {
    List<LdapUserMetaModel> getLdapUsers(String searchText);

    LdapUserMetaModel getLdapUser(String uid);

    void init();

}
