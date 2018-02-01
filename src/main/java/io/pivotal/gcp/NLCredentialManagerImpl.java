package io.pivotal.gcp;

import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.services.language.v1.CloudNaturalLanguageScopes;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NLCredentialManagerImpl extends AbstractCredentialManager implements NLCredentialManager {

    @Override
    public CloudNaturalLanguage getClient() throws IOException {
        return new CloudNaturalLanguage
                .Builder(getTransport(), getJsonFactory(), getCredential(CloudNaturalLanguageScopes.all()))
                .setApplicationName(APP_NAME).build();
    }

    @Override
    protected void extractSpecializedInfos(JSONObject cred) {
    }

    @Override
    public String getVCapKey() {
        return "google-ml-apis";
    }
}
