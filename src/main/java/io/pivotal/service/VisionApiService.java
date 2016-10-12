package io.pivotal.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.*;
import com.google.common.collect.ImmutableList;
import io.pivotal.CredentialManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mross on 10/7/16.
 */
public class VisionApiService {


    public VisionApiService() {

    }

    public List<EntityAnnotation> requestLandmarkInfo(byte [] rawImage) throws IOException, GeneralSecurityException {
        List<EntityAnnotation> responseResults = identifyLandmark(rawImage,10);
        for (EntityAnnotation annotation : responseResults) {
            System.out.printf("\t%s\n", annotation.getDescription());
        }


        return responseResults;
    }

    // This is used only in the tests
    public List<EntityAnnotation> requestPhotoLabelInfo(byte [] rawImage) throws IOException, GeneralSecurityException {


        List<EntityAnnotation> responseResults  = labelImage(rawImage, 10);
        for (EntityAnnotation annotation : responseResults) {
            System.out.println("Description: \"" + annotation.getDescription() + "\", Confidence: " + annotation.getConfidence()
            + ", " + annotation.getScore());
        }

        return responseResults;
    }

    public List<EntityAnnotation> identifyLandmark(byte [] rawImage, int maxResults)  {
        List<EntityAnnotation> visionApiResults = null;

        try {


            CredentialManager credentialManager = new CredentialManager();
            Vision vision = credentialManager.getVisionService();
            AnnotateImageRequest request =
                    new AnnotateImageRequest()
                            .setImage(new Image().encodeContent(rawImage))
                            .setFeatures(ImmutableList.of(
                                    new Feature().setType("LANDMARK_DETECTION").setMaxResults(maxResults),
                                    new Feature().setType("LABEL_DETECTION").setMaxResults(maxResults)
                            ));
            Vision.Images.Annotate annotate =
                    vision.images()
                            .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            annotate.setDisableGZipContent(true);

            BatchAnnotateImagesResponse batchResponse = annotate.execute();
            assert batchResponse.getResponses().size() == 1;
            AnnotateImageResponse response = batchResponse.getResponses().get(0);

            visionApiResults = response.getLandmarkAnnotations();
            // No landmark detected?  Fall back to label detection.
            if (visionApiResults == null) {
                visionApiResults = response.getLabelAnnotations();
            }
            for (EntityAnnotation annotation : visionApiResults) {
                System.out.println("Description: \"" + annotation.getDescription() + "\", Score: " + getScoreAsPercent(annotation));
            }

        } catch (Exception e) {
                System.out.println(e);
        }
        return visionApiResults;
    }

    // Return a percent value, with '%'
    public static String getScoreAsPercent (EntityAnnotation entityAnnotation) {
        return String.format("%d%%", (int) (100.0 * entityAnnotation.getScore()));
    }

    public List<EntityAnnotation> labelImage(byte [] rawImage, int maxResults) throws IOException, GeneralSecurityException {
        // [START construct_request]
        CredentialManager credentialManager = new CredentialManager();

        Vision vision = credentialManager.getVisionService();

        AnnotateImageRequest request =
                new AnnotateImageRequest()
                        .setImage(new Image().encodeContent(rawImage))
                        .setFeatures(ImmutableList.of(
                                new Feature()
                                        .setType("LANDMARK_DETECTION")
                                        .setMaxResults(maxResults)));
        Vision.Images.Annotate annotate =
                vision.images()
                        .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);
        // [END construct_request]

        // [START parse_response]
        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getLabelAnnotations() == null) {
            throw new IOException(
                    response.getError() != null
                            ? response.getError().getMessage()
                            : "Unknown error getting image annotations");
        }
        return response.getLabelAnnotations();
        // [END parse_response]
    }

}
