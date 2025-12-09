package net.fina.first.dto.workflow;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for submitting questionnaire answers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireAnswerRequest {

    @NotNull(message = "Answers are required")
    private List<QuestionAnswer> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionAnswer {

        @NotNull(message = "Questionnaire ID is required")
        private Long questionnaireId;

        @Size(max = 4000, message = "Answer must not exceed 4000 characters")
        private String answer;

        @Size(max = 2000, message = "Note must not exceed 2000 characters")
        private String note;
    }
}
