package net.fina.server.person.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationMetaModel {
    private long id;
    private PersonMetaModel recommender;
    private String identificationNumber;
    private String passportNumber;
    private Date recommendationDate;
    private String recommenderWorkspace;
    private long recommenderWorkspaceStrId;
    private String cooperationPlace;
    private long cooperationPlaceStrId;
    private String phone;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PersonMetaModel getRecommender() {
        return recommender;
    }

    public void setRecommender(PersonMetaModel recommender) {
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

    public String getRecommenderWorkspace() {
        return recommenderWorkspace;
    }

    public void setRecommenderWorkspace(String recommenderWorkspace) {
        this.recommenderWorkspace = recommenderWorkspace;
    }

    public long getRecommenderWorkspaceStrId() {
        return recommenderWorkspaceStrId;
    }

    public void setRecommenderWorkspaceStrId(long recommenderWorkspaceStrId) {
        this.recommenderWorkspaceStrId = recommenderWorkspaceStrId;
    }

    public String getCooperationPlace() {
        return cooperationPlace;
    }

    public void setCooperationPlace(String cooperationPlace) {
        this.cooperationPlace = cooperationPlace;
    }

    public long getCooperationPlaceStrId() {
        return cooperationPlaceStrId;
    }

    public void setCooperationPlaceStrId(long cooperationPlaceStrId) {
        this.cooperationPlaceStrId = cooperationPlaceStrId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
