package net.fina.ecm.alfresco;

import jakarta.ws.rs.client.Client;

public class RestClient {
    public final String username;

    public final String endpoint;

    public final Client client;

    public RestClient(String endpoint, Client client, String username) {
        this.endpoint = endpoint;
        this.client = client;
        this.username = username;
    }
}
