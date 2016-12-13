package io.pivotal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.pivotal.domain.MultipartFileWrapper;
import io.pivotal.service.ImageResizingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.vision.v1.model.EntityAnnotation;

import io.pivotal.domain.LabelResultsViewMapping;
import io.pivotal.domain.LandmarkNameWithScore;
import io.pivotal.domain.QueryResultsViewMapping;
import io.pivotal.service.BigQueryApiService;
import io.pivotal.service.StorageApiService;
import io.pivotal.service.VisionApiService;

/**
 * Created by mross on 10/10/16.
 */

@Controller
public class WebController {

    @Value("${gcp-storage-bucket}")
    private String bucketName;

    @Autowired
    private StorageApiService storage;

    @Autowired
    private ImageResizingService imageSizer;

    private Map<String, String> imageIdToName;

    public WebController() {
    }

    @RequestMapping("/")
    public String renderIndex(Model model) {
        imageIdToName = storage.getUploadedImages(bucketName);
        Map<String, String> imageUrlToId = imageIdToName.entrySet().stream().collect(
                Collectors.toMap(s -> storage.getThumbnailUrl(bucketName, s.getValue()), s -> s.getKey()));
        model.addAttribute("images", imageUrlToId);
        return "index";
    }

    @RequestMapping("/results")
    public String renderResults(Model model) {
        return "results";
    }

    @RequestMapping("/labels")
    public String renderLabels(Model model) {
        return "labels";
    }

    @RequestMapping("/delete")
    public String deleteThumbnails(Model model) {
        System.out.println("Deleting image thumbnails");
        storage.deleteUploadedImages(bucketName);
        return "redirect:/";
    }

    @RequestMapping(value = "/result/{imageId}")
    public String displayResult(@PathVariable String imageId, RedirectAttributes redirectAttributes) {
        String imageName = imageIdToName.get(imageId);
        //String publicUrl = storage.getPublicUrl(bucketName, imageName);
        String publicUrl = storage.getVisionUrl(bucketName, imageName);
        redirectAttributes.addFlashAttribute("imageUrl", publicUrl);

        String gcsUrl = String.format("gs://%s/%s", bucketName, imageName);
        VisionApiService vps = new VisionApiService();
        StopWatch visionApiStopwatch = new StopWatch();
        visionApiStopwatch.start();

        List<EntityAnnotation> visionApiResults = vps.identifyLandmark(gcsUrl, 10);
        visionApiStopwatch.stop();
        return displayResult(visionApiResults, visionApiStopwatch, redirectAttributes);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        MultipartFileWrapper fileResized = new MultipartFileWrapper(file);

        if (!fileResized.mimeTypeSupported()) {
            String msg = "Your file of type " + fileResized.getContentType() +
                    " is not supported. Please try again with a different file type.";
            redirectAttributes.addFlashAttribute("alert", msg);
            return "redirect:/";
        }

        if (imageSizer.isEnabled()) {
            System.out.println("Image resizer is enabled");
            try {
                byte[] b = imageSizer.resizeForVisionApi(file.getBytes());
                fileResized.setBytes(b);
                System.out.println("File resized successfully");
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute(
                        "alert", "There was a problem processing your image. Please try again with a different image");
                return "redirect:/";
            }
        }

        if (fileResized.getSize() > 4 * 1024 * 1024) {
            redirectAttributes.addFlashAttribute("alert", "The max file upload size is 4mb, please try a smaller image");
            return "redirect:/";
        }

        /*
        if (storage.upload(fileResized, bucketName)) {
            redirectAttributes.addFlashAttribute("imageUrl",
                    storage.getPublicUrl(bucketName, fileResized.getOriginalFilename()));
        } else {
            // TODO(dana): Add a better error message
            redirectAttributes.addFlashAttribute("alert", "File upload failed");
            return "redirect:/";
        }
        */

        try {
            VisionApiService vps = new VisionApiService();
            StopWatch visionApiStopwatch = new StopWatch();
            visionApiStopwatch.start();

            List<EntityAnnotation> visionApiResults = vps.identifyLandmark(fileResized.getBytes(), 10);
            visionApiStopwatch.stop();
            if (visionApiResults != null) {
                if (storage.upload(fileResized, bucketName)) {
                    redirectAttributes.addFlashAttribute("imageUrl",
                            storage.getPublicUrl(bucketName, fileResized.getOriginalFilename()));
                } else {
                    // TODO(dana): Add a better error message
                    redirectAttributes.addFlashAttribute("alert", "File upload failed");
                    return "redirect:/";
                }
                return displayResult(visionApiResults, visionApiStopwatch, redirectAttributes);
            } else {
                redirectAttributes.addFlashAttribute("alert",
                        "Your image does not appear to be a landmark.  Please try a different image.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("alert",
                    "There was a problem processing your file, please try another image");
        }
        return "redirect:/";
    }

    private String displayResult(List<EntityAnnotation> visionApiResults, StopWatch visionApiStopwatch,
                                 RedirectAttributes redirectAttributes) {
        // Need to see what type of results we have here: landmark, or label detection
        // This will depend on the size of visionApiResults: (1) -> landmark; (2) -> label (multiple)

        if (visionApiResults == null) {
            redirectAttributes.addFlashAttribute("alert",
                    "Google Vision API was not able to identify your image, please try another");
        } else if (visionApiResults.size() > 0 && visionApiResults.get(0).getLocations() != null) {
            System.out.println(visionApiResults);
            String landmarkName = "";
            List<LandmarkNameWithScore> landmarkList = new ArrayList<LandmarkNameWithScore>();
            Set<String> landmarkSet = new HashSet<>();
            for (EntityAnnotation ea : visionApiResults) {
                // This is to de-dupe the returned landmark names, since we saw to "Eiffel Tower" values returned
                String normalizedName = ea.getDescription().toLowerCase().replaceAll("[^a-z0-9]", "");
                if (landmarkSet.contains(normalizedName)) {
                    continue;
                } else {
                    landmarkSet.add(normalizedName);
                }
                LandmarkNameWithScore lws = new LandmarkNameWithScore(ea.getDescription(), ea.getScore());
                if (landmarkName.length() > 0) {
                    landmarkName += ", ";
                }
                landmarkName += lws.toString();
                landmarkList.add(lws);
            }
            EntityAnnotation landmarkResult = visionApiResults.get(0);
            //String landmarkName = landmarkResult.getDescription();
            System.out.println("Landmark name: \"" + landmarkName + "\"");
            redirectAttributes.addFlashAttribute("latitude", landmarkResult.getLocations().get(0).getLatLng().getLatitude());
            redirectAttributes.addFlashAttribute("longitude", landmarkResult.getLocations().get(0).getLatLng().getLongitude());
            redirectAttributes.addFlashAttribute("landmarkName", landmarkName);
            //redirectAttributes.addFlashAttribute("landmarkScore", VisionApiService.getScoreAsPercent(landmarkResult));

            //BigQueryApiService bqs = new BigQueryApiService(landmarkName);
            BigQueryApiService bqs = new BigQueryApiService(landmarkList);
            StopWatch biqQueryStopwatch = new StopWatch();
            biqQueryStopwatch.start();
            List<TableRow> results = bqs.executeQuery();
            biqQueryStopwatch.stop();

            redirectAttributes.addFlashAttribute("visionApiTiming", visionApiStopwatch.getTotalTimeSeconds());
            redirectAttributes.addFlashAttribute("bigQueryApiTiming", biqQueryStopwatch.getTotalTimeSeconds());
            redirectAttributes.addFlashAttribute("bigQueryBytesProcessed", bqs.getTotalBytesProcessed());
            redirectAttributes.addFlashAttribute("bigQueryIsCached", (bqs.isCached() ? "(cached)" : ""));
            redirectAttributes.addFlashAttribute("bigQueryDataSet", bqs.getDataSetName());

            ArrayList<QueryResultsViewMapping> queryResults = null;
            if (results != null) {
                System.out.println(results.size());
                queryResults = mapResultSetToList(results);
            }
            redirectAttributes.addFlashAttribute("queryResults", queryResults);
            return "redirect:/results";
        } else {
            // Handle the case of multiple possible labels
            System.out.println("Preparing the labels view ...");
            ArrayList<LabelResultsViewMapping> labelResults = new ArrayList<LabelResultsViewMapping>();
            for (EntityAnnotation ea : visionApiResults) {
                LabelResultsViewMapping lab = new LabelResultsViewMapping(ea.getDescription(), ea.getScore());
                System.out.println(lab);
                labelResults.add(lab);
            }
            redirectAttributes.addFlashAttribute("labelResults", labelResults);
            return "redirect:/labels";
        }
        return "redirect:/";
    }

    private ArrayList<QueryResultsViewMapping> mapResultSetToList(List<TableRow> results) {
        QueryResultsViewMapping viewMapping;
        ArrayList<QueryResultsViewMapping> queryResults = new ArrayList<>();

        for (TableRow row : results) {
            viewMapping = new QueryResultsViewMapping(row);
            queryResults.add(viewMapping);

        }

        return queryResults;
    }


}
