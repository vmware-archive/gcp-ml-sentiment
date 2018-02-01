package io.pivotal.gcp;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class BiqQueryCredentialManager extends AbstractCredentialManager<Bigquery> {

    @Override
    public Bigquery getClient() {
        return new Bigquery.Builder(getTransport(), getJsonFactory(), getCredential(BigqueryScopes.all()))
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
