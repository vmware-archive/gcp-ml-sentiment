package io.pivotal.gcp;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class VisionCredentialManager extends AbstractCredentialManager<Vision> {

    @Override
    public Vision getClient() {
        return new Vision
                .Builder(getTransport(), getJsonFactory(), getCredential(VisionScopes.all()))
                .setApplicationName(APP_NAME)
                .build();
    }

    @Override
    protected void extractSpecializedInfos(JSONObject cred) {
    }

    @Override
    public String getVCapKey() {
        return "google-ml-apis";
    }
}
