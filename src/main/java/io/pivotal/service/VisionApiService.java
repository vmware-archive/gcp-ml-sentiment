package io.pivotal.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageSource;
import com.google.common.collect.ImmutableList;

import io.pivotal.CredentialManager;


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

    public List<EntityAnnotation> identifyLandmark(String gcsUrl, int maxResults) {
        Image image = new Image().setSource(new ImageSource().setGcsImageUri(gcsUrl));
        return identifyLandmark(image, maxResults);
    }

    public List<EntityAnnotation> identifyLandmark(byte [] rawImage, int maxResults)  {
	Image image = new Image().encodeContent(rawImage);
	return identifyLandmark(image, maxResults);
    }

    private List<EntityAnnotation> identifyLandmark(Image image, int maxResults) {
        List<EntityAnnotation> visionApiResults = null;
        try {
            CredentialManager credentialManager = new CredentialManager();
            Vision vision = credentialManager.getVisionService();
            AnnotateImageRequest request =
                    new AnnotateImageRequest()
                            .setImage(image)
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

}
