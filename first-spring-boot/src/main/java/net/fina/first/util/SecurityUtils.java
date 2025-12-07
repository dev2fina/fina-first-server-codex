package net.fina.first.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return Optional.of(userDetails.getUsername());
        } else if (principal instanceof String username) {
            return Optional.of(username);
        }

        return Optional.empty();
    }

    public static String getCurrentUsernameOrSystem() {
        return getCurrentUsername().orElse("system");
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    public static boolean hasAuthority(String authority) {
        return getAuthorities().contains(authority);
    }

    public static boolean hasAnyAuthority(String... authorities) {
        Set<String> currentAuthorities = getAuthorities();
        for (String authority : authorities) {
            if (currentAuthorities.contains(authority)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAllAuthorities(String... authorities) {
        Set<String> currentAuthorities = getAuthorities();
        for (String authority : authorities) {
            if (!currentAuthorities.contains(authority)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasRole(String role) {
        String roleAuthority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return hasAuthority(roleAuthority);
    }

    public static boolean hasAnyRole(String... roles) {
        Set<String> currentAuthorities = getAuthorities();
        for (String role : roles) {
            String roleAuthority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            if (currentAuthorities.contains(roleAuthority)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptySet();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return Collections.emptySet();
        }

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public static Set<String> getRoles() {
        return getAuthorities().stream()
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> auth.substring(5))
                .collect(Collectors.toSet());
    }
}
