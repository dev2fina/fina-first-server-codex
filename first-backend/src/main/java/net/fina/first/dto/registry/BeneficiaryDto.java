package net.fina.first.dto.registry;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BeneficiaryDto {
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String country;

    @Min(0)
    @Max(100)
    private int ownershipPercent;
}
