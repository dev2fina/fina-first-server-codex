package net.fina.security.api;

import net.fina.security.auth.CustomPrincipal;

import java.util.List;

public interface AuthorizationLocal {
    String findByLoginPassword(String username, String password);

    String findByLoginPasswordSimple(String username, String password);

    List<String> loadUserPermission(String login);

    CustomPrincipal getUserCustomPrincipal(String login);

    boolean hasUserPermission(String login, String permission);

    CustomPrincipal getCallerPrincipal();

    boolean checkUserExist(String login);

    boolean checkUserDisabledOrBlocked(String login);

    void flushPrincipalCache(String login);

}
