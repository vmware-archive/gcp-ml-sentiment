package io.pivotal;

import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.vision.v1.model.EntityAnnotation;
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


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {


        QueryResultsViewMapping viewMapping = new QueryResultsViewMapping();

        if (file.getSize() < 4000000 ) {


            VisionApiService vps = new VisionApiService();
            ArrayList<QueryResultsViewMapping> queryResults = new ArrayList<>();

            try {

                StopWatch visionApiStopwatch = new StopWatch();
                visionApiStopwatch.start();
                List<EntityAnnotation> visionApiResults = vps.identifyLandmark(file.getBytes(), 10);
                visionApiStopwatch.stop();

                if (visionApiResults == null) {
                    redirectAttributes.addFlashAttribute("alert",
                            "Google Vision API was not able to identify your image, please try another");
                } else {

                    EntityAnnotation landmarkResult = visionApiResults.get(0);
                    String landmarkName = landmarkResult.getDescription();
                    redirectAttributes.addFlashAttribute("latitude",landmarkResult.getLocations().get(0).getLatLng().getLatitude());
                    redirectAttributes.addFlashAttribute("longitude",landmarkResult.getLocations().get(0).getLatLng().getLongitude());

                    System.out.println(landmarkResult.getLocations().get(0).getLatLng());
                    redirectAttributes.addFlashAttribute("landmarkName", landmarkName);

                    BigQueryApiService bqs = new BigQueryApiService(landmarkName);
                    StopWatch biqQueryStopwatch = new StopWatch();
                    biqQueryStopwatch.start();
                    java.util.List<TableRow> results = bqs.executeQuery();
                    biqQueryStopwatch.stop();

                    redirectAttributes.addFlashAttribute("visionApiTiming", visionApiStopwatch.getTotalTimeSeconds());
                    redirectAttributes.addFlashAttribute("bigQueryApiTiming", biqQueryStopwatch.getTotalTimeSeconds());

                    if (results != null) {
                        for (TableRow row : results) {
                            viewMapping = new QueryResultsViewMapping(row);
                            queryResults.add(viewMapping);

                        }
                        redirectAttributes.addFlashAttribute("queryResults",
                                queryResults);

                        return "redirect:/results";
                    }
                }

                } catch(Exception e){
                    System.out.println(e);
                    redirectAttributes.addFlashAttribute("alert",
                            "There was a problem processing your file, please try another image");


                }
            } else{
                redirectAttributes.addFlashAttribute("alert",
                        "The max file upload size is 4mb, please try a smaller image");
            }

        return "redirect:/";
    }



}
