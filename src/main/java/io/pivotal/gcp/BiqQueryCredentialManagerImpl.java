package io.pivotal.gcp;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BiqQueryCredentialManagerImpl extends AbstractCredentialManager implements BiqQueryCredentialManager {

    @Override
    public Bigquery getClient() throws IOException {
        GoogleCredential cred = getCredential(BigqueryScopes.all());
        return new Bigquery.Builder(getTransport(), getJsonFactory(), cred)
                .setApplicationName("Bigquery Samples")
                .build();
    }

    @Override
    protected void extractSpecializedInfos(JSONObject cred) {
    }

    @Override
    public String getVCapKey() {
        return "google-bigquery";
    }
}
