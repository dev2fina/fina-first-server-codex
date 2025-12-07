package net.fina.server.survey.model;

public class SurveyResponseMetaModel {
    private long id;
    private String name;
    private String progression;

    public SurveyResponseMetaModel() {
    }

    public SurveyResponseMetaModel(long id, String name, String progression) {
        this.id = id;
        this.name = name;
        this.progression = progression;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgression() {
        return progression;
    }

    public void setProgression(String progression) {
        this.progression = progression;
    }
}
