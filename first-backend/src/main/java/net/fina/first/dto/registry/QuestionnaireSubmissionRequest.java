package net.fina.first.dto.registry;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class QuestionnaireSubmissionRequest {
    @Valid
    @NotEmpty
    private List<QuestionnaireResponseDto> responses;
}
