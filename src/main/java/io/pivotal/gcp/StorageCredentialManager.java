package io.pivotal.gcp;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class StorageCredentialManager extends AbstractCredentialManager<Storage> {

    private String bucketName;

    @Override
    public Storage getClient() {
        return new Storage.Builder(getTransport(), getJsonFactory(), getCredential(StorageScopes.all()))
                .setApplicationName(APP_NAME).build();
    }

    @Override
    protected final void extractSpecializedInfos(JSONObject cred) {
        bucketName = cred.getString("bucket_name");
    }

    @Override
    public String getVCapKey() {
        return "google-storage";
    }

    public String getBucketName() {
        return bucketName;
    }
}
