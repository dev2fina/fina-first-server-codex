package net.fina.common.shared.report;

import java.util.List;

public class ReportingPrincipalModel {
    private String login;
    private List<String> permissions;

    public ReportingPrincipalModel() {
    }

    public ReportingPrincipalModel(String login, List<String> permissions) {
        this.login = login;
        this.permissions = permissions;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
