package net.fina.first.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Authentication response DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response")
public class AuthenticationResponse {

    @Schema(description = "JWT access token")
    private String accessToken;

    @Schema(description = "JWT refresh token")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration time in seconds", example = "86400")
    private Long expiresIn;

    @Schema(description = "User information")
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "User information")
    public static class UserInfo {

        @Schema(description = "User ID")
        private Long id;

        @Schema(description = "Username")
        private String username;

        @Schema(description = "Email")
        private String email;

        @Schema(description = "Full name")
        private String fullName;

        @Schema(description = "User roles")
        private Set<String> roles;

        @Schema(description = "User permissions")
        private Set<String> permissions;

        @Schema(description = "Password change required")
        private boolean changePassword;

        @Schema(description = "Last login date")
        private LocalDateTime lastLoginDate;
    }
}
