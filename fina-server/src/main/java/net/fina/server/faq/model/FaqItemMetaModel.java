package net.fina.server.faq.model;

import java.io.Serializable;
import java.util.Date;

public class FaqItemMetaModel implements Serializable {

    private long id;

    private long questionStrId;
    private String question;

    private long answerStrId;
    private String answer;

    private Date publish;

    private String user;
    private FaqCategoryMetaModel category;

    int sequence;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getQuestionStrId() {
        return questionStrId;
    }

    public void setQuestionStrId(long questionStrId) {
        this.questionStrId = questionStrId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public long getAnswerStrId() {
        return answerStrId;
    }

    public void setAnswerStrId(long answerStrId) {
        this.answerStrId = answerStrId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getPublish() {
        return publish;
    }

    public void setPublish(Date publish) {
        this.publish = publish;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public FaqCategoryMetaModel getCategory() {
        return category;
    }

    public void setCategory(FaqCategoryMetaModel category) {
        this.category = category;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
