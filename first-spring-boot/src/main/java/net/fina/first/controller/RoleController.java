package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.response.RoleResponse;
import net.fina.first.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management endpoints")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<List<RoleResponse>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<RoleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get role by name")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<RoleResponse> findByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.findByName(name));
    }

    @PostMapping
    @Operation(summary = "Create new role")
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleService.create(
                request.getName(),
                request.getDescription(),
                request.getPermissionIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<RoleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        RoleResponse response = roleService.update(
                id,
                request.getName(),
                request.getDescription(),
                request.getPermissionIds());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
    }

    @PostMapping("/{id}/permissions")
    @Operation(summary = "Add permissions to role")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<RoleResponse> addPermissions(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        RoleResponse response = roleService.addPermissions(id, permissionIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/permissions")
    @Operation(summary = "Remove permissions from role")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<RoleResponse> removePermissions(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        RoleResponse response = roleService.removePermissions(id, permissionIds);
        return ResponseEntity.ok(response);
    }

    @Data
    public static class CreateRoleRequest {
        @NotBlank
        @Size(max = 50)
        private String name;

        @Size(max = 255)
        private String description;

        private Set<Long> permissionIds;
    }

    @Data
    public static class UpdateRoleRequest {
        @Size(max = 50)
        private String name;

        @Size(max = 255)
        private String description;

        private Set<Long> permissionIds;
    }
}
