package io.pivotal.gcp;

import com.google.api.client.googleapis.services.AbstractGoogleClient;

public interface CredentialManager<T extends AbstractGoogleClient> {
    String APP_NAME = "spring-nlp";

    T getClient();

    String getProjectId();
}
