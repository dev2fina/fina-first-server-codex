package net.fina.first.dto.registry;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.fina.first.model.registry.ManagerRole;

@Data
public class ManagerDto {
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String fullName;

    @NotBlank
    @Size(max = 32)
    private String personalCode;

    @Size(max = 64)
    private String position;

    @Email
    @Size(max = 128)
    private String email;

    @Size(max = 32)
    private String phone;

    @NotNull
    private ManagerRole role;
}
