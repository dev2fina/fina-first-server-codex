package net.fina.server.inputs.model;

public class InputManagerUserMetaModel {

    private long id;
    private String login;
    private String name;

    public InputManagerUserMetaModel() {}

    public InputManagerUserMetaModel(long id, String login, String name) {
        this.id = id;
        this.login = login;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
