package net.fina.first.dto.workflow;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.ControllerDecision;

import java.time.LocalDate;

/**
 * Request DTO for Controller decision on registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControllerDecisionRequest {

    @NotNull(message = "Decision is required")
    private ControllerDecision decision;

    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;

    /**
     * Reason for decline (required when decision is DECLINE).
     */
    @Size(max = 2000, message = "Decline reason must not exceed 2000 characters")
    private String declineReason;

    /**
     * Deadline for correction (used when decision is DECLINE).
     */
    private LocalDate correctionDeadline;

    /**
     * Number of days allowed for correction.
     */
    private Integer correctionDays;
}
