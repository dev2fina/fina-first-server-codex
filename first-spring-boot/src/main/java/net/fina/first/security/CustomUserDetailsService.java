package net.fina.first.security;

import lombok.RequiredArgsConstructor;
import net.fina.first.model.User;
import net.fina.first.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginWithRolesAndPermissions(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with login: " + username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Add role authorities
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Add permission authorities
            role.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission.getName())));
        });

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                !user.isDisabled(),
                true,
                !user.isChangePassword(),
                !user.isAccountLocked(),
                authorities);
    }
}
