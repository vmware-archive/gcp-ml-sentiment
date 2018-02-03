package io.pivotal.gcp;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private final String projectId;

    public AbstractCredentialManager() {
        String env = System.getenv("VCAP_SERVICES");
        JSONObject json = new JSONObject(env);
        logger.debug("Reading services [" + getVCapKey() + "] infos");
        JSONArray root = json.getJSONArray(getVCapKey());
        JSONObject obj0 = root.getJSONObject(0);
        JSONObject cred = obj0.getJSONObject("credentials");
        this.privateKeyData = cred.getString("PrivateKeyData");
        this.projectId = cred.getString("ProjectId");
        extractSpecializedInfos(cred);
    }

    protected abstract void extractSpecializedInfos(JSONObject cred);

    public abstract String getVCapKey();

    private String getPrivateKeyData() {
        return this.privateKeyData;
    }

    public String getProjectId() {
        return this.projectId;
    }

    final JsonFactory getJsonFactory() {
        return new JacksonFactory();
    }

    final HttpTransport getTransport() {
        return new NetHttpTransport();
    }

    final GoogleCredential getCredential(Set<String> all) {
        byte[] buf = Base64.getDecoder().decode(getPrivateKeyData());
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
