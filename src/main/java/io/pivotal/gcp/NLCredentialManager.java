package io.pivotal.gcp;

import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.services.language.v1.CloudNaturalLanguageScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NLCredentialManager extends AbstractCredentialManager<CloudNaturalLanguage> {

    public NLCredentialManager(@Value("${vcap.services.gcp-ml.credentials.PrivateKeyData:}") String privateKeyData) {
        super(privateKeyData);
    }

    @Override
    public CloudNaturalLanguage getClient() {
        return new CloudNaturalLanguage
                .Builder(getTransport(), getJsonFactory(), getCredential(CloudNaturalLanguageScopes.all()))
                .setApplicationName(APP_NAME).build();
    }

}
