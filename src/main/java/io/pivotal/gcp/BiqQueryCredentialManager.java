package io.pivotal.gcp;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.storage.Storage;

import java.io.IOException;

public interface BiqQueryCredentialManager extends CredentialManager {

    Bigquery getClient() throws IOException;

}
