package io.pivotal.service;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.*;
import com.google.common.collect.ImmutableList;
import io.pivotal.LandmarkQualifier;
import io.pivotal.gcp.VisionCredentialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mross on 10/7/16.
 */
@Component
public class VisionApiService {

    private final static Logger logger = LoggerFactory.getLogger(VisionApiService.class);

    @Autowired
    private VisionCredentialManager credentialManager;

    public VisionApiService() {

    }

    public List<EntityAnnotation> requestLandmarkInfo(byte[] rawImage) throws IOException, GeneralSecurityException {
        List<EntityAnnotation> responseResults = identifyLandmark(rawImage, 10);
        for (EntityAnnotation annotation : responseResults) {
            System.out.printf("\t%s\n", annotation.getDescription());
        }
        return responseResults;
    }

    public List<EntityAnnotation> identifyLandmark(String gcsUrl, int maxResults) {
        Image image = new Image().setSource(new ImageSource().setGcsImageUri(gcsUrl));
        return identifyLandmark(image, maxResults);
    }

    public List<EntityAnnotation> identifyLandmark(byte[] rawImage, int maxResults) {
        Image image = new Image().encodeContent(rawImage);
        return identifyLandmark(image, maxResults);
    }

    private List<EntityAnnotation> identifyLandmark(Image image, int maxResults) {
        List<EntityAnnotation> visionApiResults = null;
        try {
//            CredentialManager credentialManager = new CredentialManager();
            Vision vision = credentialManager.getClient();
            AnnotateImageRequest request =
                    new AnnotateImageRequest()
                            .setImage(image)
                            .setFeatures(ImmutableList.of(
                                    new Feature().setType("LANDMARK_DETECTION").setMaxResults(maxResults),
                                    new Feature().setType("LABEL_DETECTION").setMaxResults(maxResults),
                                    new Feature().setType("SAFE_SEARCH_DETECTION").setMaxResults(maxResults)
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

                /*
                 * Add Safe Search 12 December 2016.
                 * If there is objectionable content uploaded, need to:
                 * 1. Provide a message about what a landmark is
                 * 2. Prevent us from storing that image (or, remove it)
                 * 3. Prevent the display of this image.
                 */

                // Here, just look at the probability the image is of a landmark at all.
                List<String> descriptionList = new ArrayList<String>();
                for (EntityAnnotation annotation : visionApiResults) {
                    descriptionList.add(annotation.getDescription());
                }
                double landmarkFraction = LandmarkQualifier.getWordFraction(descriptionList);
                boolean isPossibleLandmark = LandmarkQualifier.isPossibleLandmark(descriptionList);
                System.out.printf("Probability this is a landmark: %.3f\n", landmarkFraction);
                System.out.printf("Is this possibly a landmark? %s\n", isPossibleLandmark ? "Yes" : "No");
                if (!isPossibleLandmark) {
                    visionApiResults = null;
                }
                // Now, see if the image triggers the adult filters.
                SafeSearchAnnotation safeSearchAnnotation = response.getSafeSearchAnnotation();
                String adultLikelihood = safeSearchAnnotation.getAdult();
                if (adultLikelihood != null) {
                    logger.info("Adult likelihood: " + adultLikelihood);
                    if ("POSSIBLE".equals(adultLikelihood) || "LIKELY".equals(adultLikelihood)
                            || "VERY_LIKELY".equals(adultLikelihood)) {
                        visionApiResults = null;
                    }
                }
            }

            if (visionApiResults != null) {
                for (EntityAnnotation annotation : visionApiResults) {
                    logger.info("Description: \"" + annotation.getDescription() + "\", Score: " + getScoreAsPercent(annotation));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return visionApiResults;
    }

    // Return a percent value, with '%'
    public static String getScoreAsPercent(EntityAnnotation entityAnnotation) {
        return String.format("%d%%", (int) (100.0 * entityAnnotation.getScore()));
    }

}
