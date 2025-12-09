package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.model.Permission;
import net.fina.first.model.Role;
import net.fina.first.model.User;
import net.fina.first.repository.PermissionRepository;
import net.fina.first.repository.RoleRepository;
import net.fina.first.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Initializes default data on application startup.
 * Creates default permissions, roles, and admin user if they don't exist.
 * Only runs in development profile or when data doesn't exist.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // All system permissions
    private static final List<String[]> PERMISSIONS = Arrays.asList(
            // User permissions
            new String[]{"USER_CREATE", "Create users"},
            new String[]{"USER_READ", "Read users"},
            new String[]{"USER_UPDATE", "Update users"},
            new String[]{"USER_DELETE", "Delete users"},
            new String[]{"USER_LOCK", "Lock user accounts"},
            new String[]{"USER_UNLOCK", "Unlock user accounts"},
            new String[]{"USER_ACTIVATE", "Activate users"},
            new String[]{"USER_DEACTIVATE", "Deactivate users"},
            new String[]{"USER_RESET_PASSWORD", "Reset user passwords"},

            // Role permissions
            new String[]{"ROLE_CREATE", "Create roles"},
            new String[]{"ROLE_READ", "Read roles"},
            new String[]{"ROLE_UPDATE", "Update roles"},
            new String[]{"ROLE_DELETE", "Delete roles"},

            // FI Registry permissions
            new String[]{"FI_REGISTRY_CREATE", "Create FI registrations"},
            new String[]{"FI_REGISTRY_READ", "Read FI registrations"},
            new String[]{"FI_REGISTRY_UPDATE", "Update FI registrations"},
            new String[]{"FI_REGISTRY_DELETE", "Delete FI registrations"},
            new String[]{"FI_REGISTRY_SUBMIT", "Submit FI registrations"},
            new String[]{"FI_REGISTRY_APPROVE", "Approve FI registrations"},
            new String[]{"FI_REGISTRY_REJECT", "Reject FI registrations"},

            // Branch permissions
            new String[]{"BRANCH_CREATE", "Create branches"},
            new String[]{"BRANCH_READ", "Read branches"},
            new String[]{"BRANCH_UPDATE", "Update branches"},
            new String[]{"BRANCH_DELETE", "Delete branches"},

            // Administrator permissions
            new String[]{"ADMINISTRATOR_CREATE", "Create administrators"},
            new String[]{"ADMINISTRATOR_READ", "Read administrators"},
            new String[]{"ADMINISTRATOR_UPDATE", "Update administrators"},
            new String[]{"ADMINISTRATOR_DELETE", "Delete administrators"},

            // Beneficiary permissions
            new String[]{"BENEFICIARY_CREATE", "Create beneficiaries"},
            new String[]{"BENEFICIARY_READ", "Read beneficiaries"},
            new String[]{"BENEFICIARY_UPDATE", "Update beneficiaries"},
            new String[]{"BENEFICIARY_DELETE", "Delete beneficiaries"},

            // License permissions
            new String[]{"LICENSE_CREATE", "Create licenses"},
            new String[]{"LICENSE_READ", "Read licenses"},
            new String[]{"LICENSE_UPDATE", "Update licenses"},
            new String[]{"LICENSE_DELETE", "Delete licenses"},

            // Document permissions
            new String[]{"DOCUMENT_CREATE", "Upload documents"},
            new String[]{"DOCUMENT_READ", "Read documents"},
            new String[]{"DOCUMENT_UPDATE", "Update documents"},
            new String[]{"DOCUMENT_DELETE", "Delete documents"},
            new String[]{"DOCUMENT_DOWNLOAD", "Download documents"},

            // Audit permissions
            new String[]{"AUDIT_READ", "Read audit logs"},

            // Reference data permissions
            new String[]{"REFERENCE_DATA_READ", "Read reference data"},
            new String[]{"REFERENCE_DATA_MANAGE", "Manage reference data"}
    );

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data initialization...");

        // Create permissions
        Set<Permission> allPermissions = createPermissions();

        // Create admin role with all permissions
        Role adminRole = createAdminRole(allPermissions);

        // Create controller role
        Role controllerRole = createControllerRole(allPermissions);

        // Create FI user role
        Role fiUserRole = createFiUserRole(allPermissions);

        // Create default admin user
        createAdminUser(adminRole);

        log.info("Data initialization completed successfully!");
    }

    private Set<Permission> createPermissions() {
        Set<Permission> created = new HashSet<>();

        for (String[] permData : PERMISSIONS) {
            String name = permData[0];
            String description = permData[1];

            Permission permission = permissionRepository.findByName(name)
                    .orElseGet(() -> {
                        log.info("Creating permission: {}", name);
                        Permission p = Permission.builder()
                                .name(name)
                                .description(description)
                                .code(name)
                                .build();
                        return permissionRepository.save(p);
                    });
            created.add(permission);
        }

        log.info("Created/verified {} permissions", created.size());
        return created;
    }

    private Role createAdminRole(Set<Permission> allPermissions) {
        return roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    log.info("Creating ADMIN role with all permissions");
                    Role role = Role.builder()
                            .name("ADMIN")
                            .code("ADMIN")
                            .description("System Administrator with full access")
                            .permissions(allPermissions)
                            .build();
                    return roleRepository.save(role);
                });
    }

    private Role createControllerRole(Set<Permission> allPermissions) {
        return roleRepository.findByName("CONTROLLER")
                .orElseGet(() -> {
                    log.info("Creating CONTROLLER role");
                    Set<Permission> controllerPermissions = new HashSet<>();
                    allPermissions.stream()
                            .filter(p -> p.getName().startsWith("FI_REGISTRY_") ||
                                        p.getName().startsWith("BRANCH_") ||
                                        p.getName().startsWith("ADMINISTRATOR_") ||
                                        p.getName().startsWith("BENEFICIARY_") ||
                                        p.getName().startsWith("LICENSE_") ||
                                        p.getName().startsWith("DOCUMENT_") ||
                                        p.getName().equals("AUDIT_READ") ||
                                        p.getName().equals("REFERENCE_DATA_READ"))
                            .forEach(controllerPermissions::add);

                    Role role = Role.builder()
                            .name("CONTROLLER")
                            .code("CONTROLLER")
                            .description("Controller responsible for reviewing registrations")
                            .permissions(controllerPermissions)
                            .build();
                    return roleRepository.save(role);
                });
    }

    private Role createFiUserRole(Set<Permission> allPermissions) {
        return roleRepository.findByName("FI_USER")
                .orElseGet(() -> {
                    log.info("Creating FI_USER role");
                    Set<Permission> fiUserPermissions = new HashSet<>();
                    allPermissions.stream()
                            .filter(p -> p.getName().equals("FI_REGISTRY_CREATE") ||
                                        p.getName().equals("FI_REGISTRY_READ") ||
                                        p.getName().equals("FI_REGISTRY_UPDATE") ||
                                        p.getName().equals("FI_REGISTRY_SUBMIT") ||
                                        p.getName().startsWith("BRANCH_") ||
                                        p.getName().startsWith("ADMINISTRATOR_") ||
                                        p.getName().startsWith("BENEFICIARY_") ||
                                        p.getName().startsWith("LICENSE_") ||
                                        p.getName().equals("DOCUMENT_CREATE") ||
                                        p.getName().equals("DOCUMENT_READ") ||
                                        p.getName().equals("DOCUMENT_DOWNLOAD") ||
                                        p.getName().equals("REFERENCE_DATA_READ"))
                            .forEach(fiUserPermissions::add);

                    Role role = Role.builder()
                            .name("FI_USER")
                            .code("FI_USER")
                            .description("Financial Institution user submitting registrations")
                            .permissions(fiUserPermissions)
                            .build();
                    return roleRepository.save(role);
                });
    }

    private void createAdminUser(Role adminRole) {
        String adminUsername = "admin";

        if (userRepository.findByLogin(adminUsername).isEmpty()) {
            log.info("Creating default admin user");

            User admin = User.builder()
                    .login(adminUsername)
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@fina.net")
                    .firstName("System")
                    .lastName("Administrator")
                    .disabled(false)
                    .blocked(false)
                    .changePassword(false)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(admin);
            log.info("Admin user created successfully with username: {} and password: admin123", adminUsername);
        } else {
            log.info("Admin user already exists");
        }
    }
}
