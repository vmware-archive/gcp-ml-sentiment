package io.pivotal.gcp;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VisionCredentialManager extends AbstractCredentialManager<Vision> {

    public VisionCredentialManager(@Value("${vcap.services.gcp-ml.credentials.PrivateKeyData:}") String privateKeyData) {
        super(privateKeyData);
    }

    @Override
    public Vision getClient() {
        return new Vision
                .Builder(getTransport(), getJsonFactory(), getCredential(VisionScopes.all()))
                .setApplicationName(APP_NAME)
                .build();
    }

}
