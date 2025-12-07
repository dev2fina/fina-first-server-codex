package net.fina.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.response.RoleResponse;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.DuplicateResourceException;
import net.fina.first.exception.ResourceNotFoundException;
import net.fina.first.mapper.RoleMapper;
import net.fina.first.model.Permission;
import net.fina.first.model.Role;
import net.fina.first.repository.PermissionRepository;
import net.fina.first.repository.RoleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    private final AuditLogService auditLogService;

    @Cacheable(value = "roles")
    public List<RoleResponse> findAll() {
        log.debug("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toResponseList(roles);
    }

    @Cacheable(value = "roles", key = "#id")
    public RoleResponse findById(Long id) {
        log.debug("Fetching role by id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return roleMapper.toResponse(role);
    }

    public RoleResponse findByName(String name) {
        log.debug("Fetching role by name: {}", name);
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
        return roleMapper.toResponse(role);
    }

    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public RoleResponse create(String name, String description, Set<Long> permissionIds) {
        log.info("Creating new role: {}", name);

        if (roleRepository.existsByName(name)) {
            throw new DuplicateResourceException("Role", "name", name);
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setActive(true);

        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);

        auditLogService.log("ROLE_CREATE", "Role", savedRole.getId(),
                null, roleMapper.toResponse(savedRole).toString());

        return roleMapper.toResponse(savedRole);
    }

    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public RoleResponse update(Long id, String name, String description, Set<Long> permissionIds) {
        log.info("Updating role: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        if (role.isSystemRole()) {
            throw new BusinessException("System roles cannot be modified");
        }

        String oldValue = roleMapper.toResponse(role).toString();

        if (name != null && !name.equals(role.getName())) {
            if (roleRepository.existsByName(name)) {
                throw new DuplicateResourceException("Role", "name", name);
            }
            role.setName(name);
        }

        if (description != null) {
            role.setDescription(description);
        }

        if (permissionIds != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);

        auditLogService.log("ROLE_UPDATE", "Role", savedRole.getId(),
                oldValue, roleMapper.toResponse(savedRole).toString());

        return roleMapper.toResponse(savedRole);
    }

    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public void delete(Long id) {
        log.info("Deleting role: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        if (role.isSystemRole()) {
            throw new BusinessException("System roles cannot be deleted");
        }

        if (!role.getUsers().isEmpty()) {
            throw new BusinessException("Cannot delete role assigned to users");
        }

        roleRepository.delete(role);

        auditLogService.log("ROLE_DELETE", "Role", id,
                roleMapper.toResponse(role).toString(), null);
    }

    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public RoleResponse addPermissions(Long roleId, Set<Long> permissionIds) {
        log.info("Adding permissions to role: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.getPermissions().addAll(permissions);

        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public RoleResponse removePermissions(Long roleId, Set<Long> permissionIds) {
        log.info("Removing permissions from role: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        role.getPermissions().removeIf(p -> permissionIds.contains(p.getId()));

        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }
}
