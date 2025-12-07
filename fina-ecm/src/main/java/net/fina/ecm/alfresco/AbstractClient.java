package net.fina.ecm.alfresco;

import net.fina.ecm.alfresco.api.core.ExternalAuthFilter;
import net.fina.ecm.util.AlfrescoConfiguration;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.WebTarget;

public abstract class AbstractClient<T> {

    protected RestClient restClient;

    public <T> T getAPI(final Class<T> service) {
        WebTarget target = restClient.client.target(restClient.endpoint);
        ResteasyWebTarget resteasyWebTarget = (ResteasyWebTarget) target;

        return resteasyWebTarget.proxy(service);
    }


    protected AbstractClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public static abstract class Builder<T> {

        protected String endpoint, username, password, userHashSalt;
        protected String acceptLanguage;
        protected Client httpClient;

        public Builder<T> connect(String acceptLanguage) {
            this.acceptLanguage = acceptLanguage;
            return connect(AlfrescoConfiguration.get().getAlfrescoProperty("endpoint"), AlfrescoConfiguration.get().getAlfrescoProperty("username"), AlfrescoConfiguration.get().getAlfrescoProperty("password"));
        }

        public Builder<T> connect() {
            return connect(AlfrescoConfiguration.get().getAlfrescoProperty("endpoint"), AlfrescoConfiguration.get().getAlfrescoProperty("username"), AlfrescoConfiguration.get().getAlfrescoProperty("password"));
        }

        public Builder<T> connect(String endpoint, String username, String password) {

            if (endpoint != null && !endpoint.isEmpty()) {
                this.endpoint = (endpoint.lastIndexOf("/") == (endpoint.length() - 1)) ? endpoint
                        : endpoint.concat("/");
            }
            this.username = username;
            this.password = password;

            return this;
        }

        public Builder<T> connectExternal(String endpoint, String username, String userHashSalt) {
            this.userHashSalt = userHashSalt;
            connect(endpoint, username, null);
            return this;
        }

        public Builder<T> connectExternal(String acceptLanguage, String endpoint, String username, String userHashSalt) {
            this.userHashSalt = userHashSalt;
            this.acceptLanguage = acceptLanguage;
            connect(endpoint, username, null);
            return this;
        }

        public T build() {

            // Check Parameters
            if (endpoint == null || endpoint.isEmpty()) {
                throw new IllegalArgumentException("Invalid url");
            }

            if (httpClient == null) {
                if (username != null && password != null) {
                    httpClient = ClientBuilder.newClient()
                            .register(new LoggingFilter())
                            .register(new Authenticator(username, password));
                } else if (username != null && !username.trim().isEmpty()) {
                    httpClient = ClientBuilder.newClient()
                            .register(new LoggingFilter())
                            .register(new ExternalAuthFilter(username, userHashSalt));
                }
            }

            if (httpClient != null && acceptLanguage != null && !acceptLanguage.trim().isEmpty()) {
                httpClient.register((ClientRequestFilter) requestContext -> requestContext.getHeaders().add("Accept-Language", acceptLanguage));
            }

            return create(new RestClient(endpoint, httpClient, username));
        }

        public abstract T create(RestClient restClient);

    }
}