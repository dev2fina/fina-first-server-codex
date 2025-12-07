package net.fina.server.feedback.model;

public class FeedbackMetaModel {
    private long id;
    private long nameStrId;
    private String description;
    private FeedbackCategoryMetaModel feedbackCategory;
    private int rating;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FeedbackCategoryMetaModel getFeedbackCategory() {
        return feedbackCategory;
    }

    public void setFeedbackCategory(FeedbackCategoryMetaModel feedbackCategory) {
        this.feedbackCategory = feedbackCategory;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
