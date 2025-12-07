package net.fina.common.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ActiveUserModel implements Serializable {

    private String sessionId;
    private String login;
    private String name;
    private String host;
    private String deviceCategory;
    private String browserDetails;
    private String osDetails;
    private LanguageSampleModel language;
    private boolean current;
    private ClientAppName clientAppName = ClientAppName.FINA;
    private Date sessionCreateDate;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public LanguageSampleModel getLanguage() {
        return language;
    }

    public void setLanguage(LanguageSampleModel language) {
        this.language = language;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getBrowserDetails() {
        return browserDetails;
    }

    public void setBrowserDetails(String browserDetails) {
        this.browserDetails = browserDetails;
    }

    public String getOsDetails() {
        return osDetails;
    }

    public void setOsDetails(String osDetails) {
        this.osDetails = osDetails;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public ClientAppName getClientAppName() {
        return clientAppName;
    }

    public void setClientAppName(ClientAppName clientAppName) {
        this.clientAppName = clientAppName;
    }

    public Date getSessionCreateDate() {
        return sessionCreateDate;
    }

    public void setSessionCreateDate(Date sessionCreateDate) {
        this.sessionCreateDate = sessionCreateDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ActiveUserModel other = (ActiveUserModel) obj;
        if (sessionId == null) {
            if (other.sessionId != null)
                return false;
        } else if (!sessionId.equals(other.sessionId))
            return false;
        return true;
    }

}
