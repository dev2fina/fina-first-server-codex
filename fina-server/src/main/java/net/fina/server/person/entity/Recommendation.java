package net.fina.server.person.entity;

import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity(name = "IN_RECOMMENDATIONS")
@Table(name = "IN_RECOMMENDATIONS")
public class Recommendation {
    @Id
    @SequenceGenerator(name = "recommendation_sequence", sequenceName = "recommendation_sequence", allocationSize = 1)
    @GeneratedValue(generator = "recommendation_sequence", strategy = GenerationType.SEQUENCE)
    private long id;


    @OneToOne()
    @JoinColumn(name = "RECOMMENDER_ID")
    private Person recommender;

    @Column(name = "IDENTIFICATION_NUMBER")
    private String identificationNumber;

    @Column(name = "PASSPORT_NUMBER")
    private String passportNumber;

    @Column(name = "RECOMMENDATION_DATE")
    private Date recommendationDate;

    @Column(name = "WORKSPACE_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description recommenderWorkspace;

    @Column(name = "PLACE_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description cooperationPlace;

    @Column(name = "PHONE")
    private String phone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getRecommender() {
        return recommender;
    }

    public void setRecommender(Person recommender) {
        this.recommender = recommender;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public Date getRecommendationDate() {
        return recommendationDate;
    }

    public void setRecommendationDate(Date recommendationDate) {
        this.recommendationDate = recommendationDate;
    }

    public Description getRecommenderWorkspace() {
        return recommenderWorkspace;
    }

    public void setRecommenderWorkspace(Description recommenderWorkspace) {
        this.recommenderWorkspace = recommenderWorkspace;
    }

    public Description getCooperationPlace() {
        return cooperationPlace;
    }

    public void setCooperationPlace(Description cooperationPlace) {
        this.cooperationPlace = cooperationPlace;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recommendation that = (Recommendation) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
