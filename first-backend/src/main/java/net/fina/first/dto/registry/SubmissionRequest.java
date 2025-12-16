package net.fina.first.dto.registry;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotBlank
    private String submittedBy;
}
