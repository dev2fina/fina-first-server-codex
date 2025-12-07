package net.fina.server.cems.model;

public class CEMSResponsiblePersonModel {
    private long id;
    private String fullName;
    private String position;

    public CEMSResponsiblePersonModel() {
    }

    public CEMSResponsiblePersonModel(long id, String fullName, String position) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
