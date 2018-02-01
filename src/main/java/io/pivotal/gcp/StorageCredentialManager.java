package io.pivotal.gcp;

import com.google.api.services.storage.Storage;

import java.io.IOException;

public interface StorageCredentialManager extends CredentialManager {

    Storage getClient() throws IOException;

    String getBucketName();
}
