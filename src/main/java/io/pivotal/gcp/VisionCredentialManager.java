package io.pivotal.gcp;

import com.google.api.services.vision.v1.Vision;

import java.io.IOException;

public interface VisionCredentialManager extends CredentialManager {

    Vision getClient() throws IOException;

}
