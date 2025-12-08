package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * Notification Info entity for tracking notifications related to actions.
 */
@Entity
@Table(name = "first_notification_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationInfo extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    private FiRegistryAction action;

    @Size(max = 255)
    @Column(name = "addressee")
    private String addressee;

    @Column(name = "scheduled_send_date")
    private LocalDateTime scheduledSendDate;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "is_sent")
    @Builder.Default
    private boolean sent = false;

    @Column(name = "is_gap")
    @Builder.Default
    private boolean gap = false;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "notification_type", length = 50)
    private String notificationType;

    @Column(name = "subject", length = 500)
    private String subject;

    @Column(name = "message", length = 4000)
    private String message;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "retry_count")
    @Builder.Default
    private int retryCount = 0;
}
