package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.response.UserResponse;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.DuplicateResourceException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.UserMapper;
import net.fina.first.model.Role;
import net.fina.first.model.User;
import net.fina.first.model.UserPasswordHistory;
import net.fina.first.repository.RoleRepository;
import net.fina.first.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    private static final int PASSWORD_HISTORY_SIZE = 5;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    public PageResponse<UserResponse> findAll(String search, Pageable pageable) {
        log.debug("Fetching users with search: {}", search);
        Page<User> page;
        if (search != null && !search.isBlank()) {
            page = userRepository.searchByTerm(search, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return PageResponse.of(page, userMapper.toResponseList(page.getContent()));
    }

    public UserResponse findById(Long id) {
        log.debug("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    public UserResponse findByLogin(String login) {
        log.debug("Fetching user by login: {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("User", "login", login));
        return userMapper.toResponse(user);
    }

    /**
     * Alias for findByLogin - finds user by username.
     */
    public UserResponse findByUsername(String username) {
        return findByLogin(username);
    }

    @Transactional
    public UserResponse create(String login, String email, String password,
                               String firstName, String lastName, Set<Long> roleIds) {
        log.info("Creating new user: {}", login);

        validateUniqueLogin(login);
        validateUniqueEmail(email);

        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDisabled(false);
        user.setBlocked(false);
        user.setChangePassword(true);
        user.setFailedLoginAttempts(0);

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        addPasswordToHistory(savedUser, savedUser.getPassword());

        auditLogService.log("USER_CREATE", "User", savedUser.getId(),
                null, userMapper.toResponse(savedUser).toString());

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse update(Long id, String email, String firstName,
                               String lastName, Set<Long> roleIds) {
        log.info("Updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        String oldValue = userMapper.toResponse(user).toString();

        if (email != null && !email.equals(user.getEmail())) {
            validateUniqueEmailExcluding(email, id);
            user.setEmail(email);
        }

        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }

        if (roleIds != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);

        auditLogService.log("USER_UPDATE", "User", savedUser.getId(),
                oldValue, userMapper.toResponse(savedUser).toString());

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        validatePasswordNotInHistory(user, newPassword);

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setLastPasswordChangeDate(LocalDateTime.now());
        user.setChangePassword(false);

        userRepository.save(user);
        addPasswordToHistory(user, encodedPassword);

        auditLogService.log("PASSWORD_CHANGE", "User", userId, null, "Password changed");
    }

    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        log.info("Resetting password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setLastPasswordChangeDate(LocalDateTime.now());
        user.setChangePassword(true);
        user.setBlocked(false);
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);
        addPasswordToHistory(user, encodedPassword);

        auditLogService.log("PASSWORD_RESET", "User", userId, null, "Password reset by admin");
    }

    @Transactional
    public void lockAccount(Long userId) {
        log.info("Locking account for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setBlocked(true);
        user.setLockedUntil(LocalDateTime.now().plusYears(100));
        userRepository.save(user);

        auditLogService.log("ACCOUNT_LOCK", "User", userId, null, "Account locked");
    }

    @Transactional
    public void unlockAccount(Long userId) {
        log.info("Unlocking account for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setBlocked(false);
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        auditLogService.log("ACCOUNT_UNLOCK", "User", userId, null, "Account unlocked");
    }

    @Transactional
    public void deactivate(Long userId) {
        log.info("Deactivating user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setDisabled(true);
        userRepository.save(user);

        auditLogService.log("USER_DEACTIVATE", "User", userId, null, "User deactivated");
    }

    @Transactional
    public void activate(Long userId) {
        log.info("Activating user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setDisabled(false);
        userRepository.save(user);

        auditLogService.log("USER_ACTIVATE", "User", userId, null, "User activated");
    }

    @Transactional
    public void recordFailedLogin(String login) {
        userRepository.findByLogin(login).ifPresent(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setBlocked(true);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
                log.warn("Account locked due to {} failed login attempts: {}", attempts, login);
            }

            userRepository.save(user);
        });
    }

    @Transactional
    public void recordSuccessfulLogin(String login) {
        userRepository.findByLogin(login).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    private void validateUniqueLogin(String login) {
        if (userRepository.existsByLogin(login)) {
            throw new DuplicateResourceException("User", "login", login);
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }
    }

    private void validateUniqueEmailExcluding(String email, Long excludeId) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new DuplicateResourceException("User", "email", email);
            }
        });
    }

    private void validatePasswordNotInHistory(User user, String newPassword) {
        Set<UserPasswordHistory> history = user.getPasswordHistory();
        for (UserPasswordHistory ph : history) {
            if (passwordEncoder.matches(newPassword, ph.getPasswordHash())) {
                throw new BusinessException("Password was used recently. Please choose a different password.");
            }
        }
    }

    private void addPasswordToHistory(User user, String encodedPassword) {
        UserPasswordHistory history = new UserPasswordHistory();
        history.setUser(user);
        history.setPasswordHash(encodedPassword);
        history.setChangedAt(LocalDateTime.now());

        user.getPasswordHistory().add(history);

        if (user.getPasswordHistory().size() > PASSWORD_HISTORY_SIZE) {
            user.getPasswordHistory().stream()
                    .min(Comparator.comparing(UserPasswordHistory::getChangedAt))
                    .ifPresent(oldest -> user.getPasswordHistory().remove(oldest));
        }
    }
}
