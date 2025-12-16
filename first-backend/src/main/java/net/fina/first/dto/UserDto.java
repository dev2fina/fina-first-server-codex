package net.fina.first.dto;

import java.util.Set;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private boolean active;
    private Set<RoleDto> roles;
}
