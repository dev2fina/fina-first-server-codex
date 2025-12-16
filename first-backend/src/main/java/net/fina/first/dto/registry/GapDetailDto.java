package net.fina.first.dto.registry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GapDetailDto {
    private Long id;

    @Size(max = 64)
    private String questionReference;

    @NotBlank
    @Size(max = 512)
    private String description;

    @Size(max = 255)
    private String resolution;
}
