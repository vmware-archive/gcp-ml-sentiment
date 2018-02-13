package io.pivotal.gcp;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Set;

public abstract class AbstractCredentialManager<T extends AbstractGoogleClient> implements CredentialManager<T> {

    private final static Logger logger = LoggerFactory.getLogger(AbstractCredentialManager.class);
    private final String privateKeyData;

    public AbstractCredentialManager(String privateKeyData) {
        this.privateKeyData = privateKeyData;
    }

    final JsonFactory getJsonFactory() {
        return new JacksonFactory();
    }

    final HttpTransport getTransport() {
        return new NetHttpTransport();
    }

    final GoogleCredential getCredential(Set<String> all) {
        byte[] buf = Base64.getDecoder().decode(privateKeyData);
        try (InputStream stream = new ByteArrayInputStream(buf)) {
            GoogleCredential cred = GoogleCredential.fromStream(stream);
            if (cred.createScopedRequired()) {
                cred = cred.createScoped(all);
            }
            return cred;
        } catch (IOException e) {
            logger.error("Unable to read credentials", e);
            throw new RuntimeException(e);
        }
    }

}
