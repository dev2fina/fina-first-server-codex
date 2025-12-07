package net.fina.server.feedback.entity;

import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

@Entity(name = "IN_FEEDBACK_CATEGORY")
@Table(name = "IN_FEEDBACK_CATEGORY")
public class FeedbackCategory {

    @Id
    @SequenceGenerator(name = "feedback_categories_sequence", sequenceName = "feedback_categories_sequence", allocationSize = 1)
    @GeneratedValue(generator = "feedback_categories_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    @Column(name = "NAMESTRID")
    private Description name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }
}
