package net.fina.server.survey.entity;

import net.fina.server.security.entity.User;

import jakarta.persistence.*;

@Entity(name = "IN_SURVEY")
@Table(name = "IN_SURVEY")
public class Survey {
    @Id
    @SequenceGenerator(name = "survey_sequence", sequenceName = "survey_sequence", allocationSize = 1)
    @GeneratedValue(generator = "survey_sequence", strategy = GenerationType.SEQUENCE)
    private long id;
    private String survey;
    private String name;

    @Column(name = "PROGRESSION")
    private String progression;

    @Column(name = "IS_COMPLETED")
    private boolean isCompleted;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSurvey() {
        return survey;
    }

    public void setSurvey(String survey) {
        this.survey = survey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProgression() {
        return progression;
    }

    public void setProgression(String progression) {
        this.progression = progression;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
