package net.fina.server.feedback.entity;

import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

@Entity(name = "IN_FEEDBACK")
@Table(name = "IN_FEEDBACK")
public class Feedback {

    @Id
    @SequenceGenerator(name = "feedback_sequence", sequenceName = "feedback_sequence", allocationSize = 1)
    @GeneratedValue(generator = "feedback_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    @Column(name = "DESCRIPTIONSTRID")
    private Description description;

    @OneToOne
    @JoinColumn(name = "CATEGORY_ID")
    private FeedbackCategory feedbackCategory;

    private int rating;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public FeedbackCategory getFeedbackCategory() {
        return feedbackCategory;
    }

    public void setFeedbackCategory(FeedbackCategory feedbackCategory) {
        this.feedbackCategory = feedbackCategory;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
