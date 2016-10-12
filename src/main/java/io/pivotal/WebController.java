package io.pivotal;

import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.appengine.repackaged.com.google.io.protocol.proto.ProtocolDescriptor;
import io.pivotal.Domain.LabelResultsViewMapping;
import io.pivotal.Domain.QueryResultsViewMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import io.pivotal.service.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mross on 10/10/16.
 */

@Controller
public class WebController {


    @RequestMapping("/")
    public String renderIndex(Model model) {

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


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.getSize() < 4000000 ) {
            try {
                VisionApiService vps = new VisionApiService();
                StopWatch visionApiStopwatch = new StopWatch();
                visionApiStopwatch.start();

                List<EntityAnnotation> visionApiResults = vps.identifyLandmark(file.getBytes(), 10);
                visionApiStopwatch.stop();

                // Need to see what type of results we have here: landmark, or label detection
                // This will depend on the size of visionApiResults: (1) -> landmark; (2) -> label (multiple)

                if (visionApiResults == null) {
                    redirectAttributes.addFlashAttribute("alert",
                            "Google Vision API was not able to identify your image, please try another");
                } else if (visionApiResults.size() > 0 && visionApiResults.get(0).getLocations() != null) {
                    System.out.println(visionApiResults);
                    EntityAnnotation landmarkResult = visionApiResults.get(0);
                    String landmarkName = landmarkResult.getDescription();
                    System.out.println(landmarkName);
                    redirectAttributes.addFlashAttribute("latitude",landmarkResult.getLocations().get(0).getLatLng().getLatitude());
                    redirectAttributes.addFlashAttribute("longitude",landmarkResult.getLocations().get(0).getLatLng().getLongitude());
                    redirectAttributes.addFlashAttribute("landmarkName", landmarkName);
                    redirectAttributes.addFlashAttribute("landmarkScore", VisionApiService.getScoreAsPercent(landmarkResult));

                    BigQueryApiService bqs = new BigQueryApiService(landmarkName);
                    StopWatch biqQueryStopwatch = new StopWatch();
                    biqQueryStopwatch.start();
                    List<TableRow> results = bqs.executeQuery();
                    biqQueryStopwatch.stop();

                    redirectAttributes.addFlashAttribute("visionApiTiming", visionApiStopwatch.getTotalTimeSeconds());
                    redirectAttributes.addFlashAttribute("bigQueryApiTiming", biqQueryStopwatch.getTotalTimeSeconds());
                    redirectAttributes.addFlashAttribute("bigQueryBytesProcessed", bqs.getTotalBytesProcessed());
                    redirectAttributes.addFlashAttribute("bigQueryIsCached", (bqs.isCached() ? "(cached)" : ""));
                    redirectAttributes.addFlashAttribute("bigQueryDataSet", bqs.getDataSetName());

                    if (results != null) {
                        System.out.println(results.size());

                        ArrayList<QueryResultsViewMapping> queryResults = mapResultSetToList(results);
                        redirectAttributes.addFlashAttribute("queryResults",
                                queryResults);

                        return "redirect:/results";
                    } else {
                        redirectAttributes.addFlashAttribute("alert",
                                "There was a problem processing your file, please try another image");                    }
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

                } catch(Exception e){
                    System.out.println(e.getMessage());
                    redirectAttributes.addFlashAttribute("alert",
                            "There was a problem processing your file, please try another image");
                }
            } else{
                redirectAttributes.addFlashAttribute("alert",
                        "The max file upload size is 4mb, please try a smaller image");
            }

        return "redirect:/";
    }

    private ArrayList<QueryResultsViewMapping> mapResultSetToList(List<TableRow> results){
        QueryResultsViewMapping viewMapping;
        ArrayList<QueryResultsViewMapping> queryResults = new ArrayList<>();

        for (TableRow row : results) {
            viewMapping = new QueryResultsViewMapping(row);
            queryResults.add(viewMapping);

        }

        return queryResults;
    }



}
