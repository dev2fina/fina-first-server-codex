package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for User entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User response")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Login username")
    private String login;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Full name")
    private String fullName;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Phone")
    private String phone;

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Department")
    private String department;

    @Schema(description = "Is blocked")
    private boolean blocked;

    @Schema(description = "Is disabled")
    private boolean disabled;

    @Schema(description = "Password change required")
    private boolean changePassword;

    @Schema(description = "Last login date")
    private LocalDateTime lastLoginDate;

    @Schema(description = "Last password change date")
    private LocalDateTime lastPasswordChangeDate;

    @Schema(description = "User roles")
    private Set<RoleResponse> roles;

    @Schema(description = "Assigned FI Registry IDs")
    private Set<Long> assignedFiRegistryIds;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;
}
