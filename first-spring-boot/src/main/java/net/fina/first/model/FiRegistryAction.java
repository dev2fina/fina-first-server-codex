package net.fina.first.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.AuditableEntity;
import net.fina.first.model.enums.ActionStatus;
import net.fina.first.model.enums.ActionType;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FI Registry Action entity representing workflow processes on FI registrations.
 * Tracks registration, change, cancellation, and other workflow actions.
 */
@Entity
@Table(name = "first_fi_registry_actions")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FiRegistryAction extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id", nullable = false)
    private FiRegistry fiRegistry;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ActionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ActionStatus status = ActionStatus.INITIATED;

    @Column(name = "step")
    private Integer step;

    @Column(name = "author", length = 255)
    private String author;

    @Column(name = "identity", length = 100)
    private String identity;

    @Column(name = "process_id", length = 100)
    private String processId;

    @Column(name = "relevance_time")
    private LocalDateTime relevanceTime;

    @Column(name = "documents_folder_id", length = 100)
    private String documentsFolderId;

    // === Cancellation Fields ===

    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;

    @Column(name = "cancellation_is_liquidator_required")
    private Boolean cancellationIsLiquidatorRequired;

    // === Control Fields ===

    @Column(name = "control_status", length = 50)
    private String controlStatus;

    @Column(name = "redacting_status", length = 50)
    private String redactingStatus;

    @Column(name = "final_progress_status", length = 50)
    private String finalProgressStatus;

    // === Notes and Comments ===

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "rejection_reason", length = 2000)
    private String rejectionReason;

    // === Questionnaire Association ===

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @Builder.Default
    private List<ActionQuestionnaire> questionnaires = new ArrayList<>();

    // === Document Association ===

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    // === Notification Association ===

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @Builder.Default
    private List<NotificationInfo> notifications = new ArrayList<>();

    // === Helper Methods ===

    public void addQuestionnaire(ActionQuestionnaire questionnaire) {
        questionnaires.add(questionnaire);
        questionnaire.setAction(this);
    }

    public void removeQuestionnaire(ActionQuestionnaire questionnaire) {
        questionnaires.remove(questionnaire);
        questionnaire.setAction(null);
    }

    public void addDocument(Document document) {
        documents.add(document);
        document.setAction(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setAction(null);
    }

    public void addNotification(NotificationInfo notification) {
        notifications.add(notification);
        notification.setAction(this);
    }

    public void removeNotification(NotificationInfo notification) {
        notifications.remove(notification);
        notification.setAction(null);
    }

    /**
     * Checks if the action can be cancelled.
     */
    public boolean canBeCancelled() {
        return status == ActionStatus.INITIATED ||
               status == ActionStatus.IN_PROGRESS ||
               status == ActionStatus.PENDING_APPROVAL;
    }

    /**
     * Checks if the action is in a final state.
     */
    public boolean isFinalState() {
        return status == ActionStatus.COMPLETED ||
               status == ActionStatus.REJECTED ||
               status == ActionStatus.CANCELLED;
    }
}
