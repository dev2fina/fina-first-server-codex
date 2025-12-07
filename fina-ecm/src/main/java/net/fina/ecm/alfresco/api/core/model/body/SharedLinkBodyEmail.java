package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedLinkBodyEmail implements BaseRepresentation {
    public  String client;
    public  String message;
    public  String locale;
    public  List<String> recipientEmails;

    public SharedLinkBodyEmail() {
    }

    public SharedLinkBodyEmail(String client, String message, String locale, List<String> recipientEmails) {
        this.client = client;
        this.message = message;
        this.locale = locale;
        this.recipientEmails = recipientEmails;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(List<String> recipientEmails) {
        this.recipientEmails = recipientEmails;
    }
}
