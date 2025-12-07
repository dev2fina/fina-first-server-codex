package net.fina.server.faq.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import net.fina.server.security.entity.User;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "IN_FAQ_ITEMS")
public class FaqItem implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "faq_items_sequence", sequenceName = "faq_items_sequence", allocationSize = 1)
    @GeneratedValue(generator = "faq_items_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    @Column(name = "QUESTIONSTRID")
    private Description question;

    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    @Column(name = "ANSWERSTRID")
    private Description answer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publish;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private FaqCategory category;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "ITEM_SEQUENCE")
    private int sequence;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getQuestion() {
        return question;
    }

    public void setQuestion(Description question) {
        this.question = question;
    }

    public Description getAnswer() {
        return answer;
    }

    public void setAnswer(Description answer) {
        this.answer = answer;
    }

    public Date getPublish() {
        return publish;
    }

    public void setPublish(Date publish) {
        this.publish = publish;
    }

    public FaqCategory getCategory() {
        return category;
    }

    public void setCategory(FaqCategory category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
