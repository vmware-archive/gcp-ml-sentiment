package io.pivotal.gcp;

import com.google.api.services.language.v1.CloudNaturalLanguage;

import java.io.IOException;

public interface NLCredentialManager extends CredentialManager {

    CloudNaturalLanguage getClient() throws IOException;

}
