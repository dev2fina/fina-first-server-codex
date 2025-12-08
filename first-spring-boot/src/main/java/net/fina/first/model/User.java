package net.fina.first.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.AuditableEntity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity for authentication and authorization.
 */
@Entity
@Table(name = "first_users")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends AuditableEntity {

    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 100)
    @Column(name = "login", nullable = false, unique = true, length = 100)
    private String login;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 255)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 255)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email(message = "Invalid email format")
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "department", length = 255)
    private String department;

    @Column(name = "is_blocked")
    @Builder.Default
    private boolean blocked = false;

    @Column(name = "is_disabled")
    @Builder.Default
    private boolean disabled = false;

    @Column(name = "change_password")
    @Builder.Default
    private boolean changePassword = true;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "last_password_change_date")
    private LocalDateTime lastPasswordChangeDate;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    // === Roles ===

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "first_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @NotAudited
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // === Direct Permissions ===

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "first_user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @NotAudited
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    // === FI Access ===

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "first_user_fi_registry",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "fi_registry_id")
    )
    @NotAudited
    @Builder.Default
    private Set<FiRegistry> assignedFiRegistries = new HashSet<>();

    // === Password History ===

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @Builder.Default
    private Set<UserPasswordHistory> passwordHistory = new HashSet<>();

    // === Helper Methods ===

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public void assignFiRegistry(FiRegistry fiRegistry) {
        assignedFiRegistries.add(fiRegistry);
    }

    public void unassignFiRegistry(FiRegistry fiRegistry) {
        assignedFiRegistries.remove(fiRegistry);
    }

    public void recordFailedLogin() {
        this.failedLoginAttempts++;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    public boolean isAccountLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }

    public boolean isAccountActive() {
        return !blocked && !disabled && !deleted;
    }
}
