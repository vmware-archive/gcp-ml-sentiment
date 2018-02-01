package io.pivotal.gcp;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StorageCredentialManagerImpl extends AbstractCredentialManager implements StorageCredentialManager {

    private String bucketName;

    @Override
    protected final void extractSpecializedInfos(JSONObject cred) {
        bucketName = cred.getString("bucket_name");
    }

    @Override
    public String getVCapKey() {
        return "google-storage";
    }

    @Override
    public Storage getClient() throws IOException {
        return new Storage.Builder(getTransport(), getJsonFactory(), getCredential(StorageScopes.all()))
                .setApplicationName(APP_NAME).build();
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }
}
