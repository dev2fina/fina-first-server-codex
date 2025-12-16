package net.fina.first.dto.registry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;
import net.fina.first.model.registry.LicenseStatus;
import net.fina.first.model.registry.LicenseType;

@Data
public class LicenseDto {
    private Long id;

    @NotNull
    private LicenseType licenseType;

    @NotBlank
    @Size(max = 64)
    private String licenseNumber;

    @NotNull
    private LicenseStatus licenseStatus;

    private LocalDate issuedDate;

    private LocalDate expiryDate;
}
