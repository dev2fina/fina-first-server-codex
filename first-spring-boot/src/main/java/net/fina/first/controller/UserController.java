package net.fina.first.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.fina.first.dto.common.ApiResponse;
import net.fina.first.dto.common.PageResponse;
import net.fina.first.dto.response.UserResponse;
import net.fina.first.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users with search and pagination")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "username", direction = Sort.Direction.ASC)
            Pageable pageable) {
        PageResponse<UserResponse> response = userService.findAll(search, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> findByUsername(@PathVariable String username) {
        UserResponse response = userService.findByUsername(username);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create new user")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.create(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getRoleIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(
                id,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getRoleIds());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Reset user password (admin)")
    @PreAuthorize("hasAuthority('USER_RESET_PASSWORD')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(id, request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }

    @PostMapping("/{id}/lock")
    @Operation(summary = "Lock user account")
    @PreAuthorize("hasAuthority('USER_LOCK')")
    public ResponseEntity<ApiResponse<Void>> lockAccount(@PathVariable Long id) {
        userService.lockAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account locked successfully"));
    }

    @PostMapping("/{id}/unlock")
    @Operation(summary = "Unlock user account")
    @PreAuthorize("hasAuthority('USER_UNLOCK')")
    public ResponseEntity<ApiResponse<Void>> unlockAccount(@PathVariable Long id) {
        userService.unlockAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account unlocked successfully"));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user")
    @PreAuthorize("hasAuthority('USER_DEACTIVATE')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate user")
    @PreAuthorize("hasAuthority('USER_ACTIVATE')")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long id) {
        userService.activate(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully"));
    }

    @Data
    public static class CreateUserRequest {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8, max = 100)
        private String password;

        @Size(max = 100)
        private String firstName;

        @Size(max = 100)
        private String lastName;

        private Set<Long> roleIds;
    }

    @Data
    public static class UpdateUserRequest {
        @Email
        private String email;

        @Size(max = 100)
        private String firstName;

        @Size(max = 100)
        private String lastName;

        private Set<Long> roleIds;
    }

    @Data
    public static class ResetPasswordRequest {
        @NotBlank
        @Size(min = 8, max = 100)
        private String newPassword;
    }
}
