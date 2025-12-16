package net.fina.first.dto.registry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.fina.first.model.registry.QuestionAnswer;
import net.fina.first.model.registry.QuestionnaireState;

@Data
public class QuestionnaireResponseDto {
    private Long id;

    @NotBlank
    @Size(max = 512)
    private String question;

    @Size(max = 255)
    private String category;

    private boolean obligatory;

    @NotNull
    private QuestionAnswer answer;

    private QuestionnaireState state = QuestionnaireState.CURRENT;
}
