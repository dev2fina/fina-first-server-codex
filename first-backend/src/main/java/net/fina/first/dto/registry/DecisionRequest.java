package net.fina.first.dto.registry;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DecisionRequest {
    @NotBlank
    private String performedBy;

    private String comment;
}
