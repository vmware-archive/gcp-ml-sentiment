package io.pivotal.gcp;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StorageCredentialManager extends AbstractCredentialManager<Storage> {

    private final String bucketName;

    public StorageCredentialManager(@Value("${vcap.services.gcp-storage.credentials.PrivateKeyData:}") String privateKeyData,
                                    @Value("${vcap.services.gcp-storage.credentials.bucket_name:}") String bucketName) {
        super(privateKeyData);
        this.bucketName = bucketName;
    }

    @Override
    public Storage getClient() {
        return new Storage.Builder(getTransport(), getJsonFactory(), getCredential(StorageScopes.all()))
                .setApplicationName(APP_NAME).build();
    }

    public String getBucketName() {
        return bucketName;
    }
}
