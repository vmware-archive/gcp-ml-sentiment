package io.pivotal.gcp;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BiqQueryCredentialManager extends AbstractCredentialManager<Bigquery> {


    private final String projectId;

    public BiqQueryCredentialManager(@Value("${vcap.services.gcp-bigquery.credentials.PrivateKeyData:}") String privateKeyData,
                                     @Value("${vcap.services.gcp-bigquery.credentials.ProjectId:}") String projectId) {
        super(privateKeyData);
        this.projectId = projectId;
    }

    @Override
    public Bigquery getClient() {
        return new Bigquery.Builder(getTransport(), getJsonFactory(), getCredential(BigqueryScopes.all()))
                .setApplicationName("Bigquery Samples")
                .build();
    }

    public String getProjectId() {
        return projectId;
    }
}
