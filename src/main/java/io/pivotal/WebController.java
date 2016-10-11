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

        System.out.println(file.getSize());
        if (file.getSize() < 4000000 ) {


            VisionApiService vps = new VisionApiService();
            ArrayList<QueryResultsViewMapping> queryResults = new ArrayList<>();

            try {
                StopWatch visionApiStopwatch = new StopWatch();
                visionApiStopwatch.start();

                List<EntityAnnotation> landmarkInfoArray = vps.identifyLandmark(file.getBytes(), 10);
                visionApiStopwatch.stop();
                EntityAnnotation landmarkResult = landmarkInfoArray.get(0);
                String landmarkName = landmarkResult.getDescription();
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
                } else {
                    redirectAttributes.addFlashAttribute("alert",
                            "There was a problem processing your file, please try another image");
                }


            } catch (Exception e) {
                System.out.println("THERE WAS EXCEPTION");
                System.out.println(e);
                redirectAttributes.addFlashAttribute("alert",
                        "There was a problem processing your file, please try another image");


            }
        } else {
            redirectAttributes.addFlashAttribute("alert",
                    "The max file upload size is 4mb, please try a smaller image");
        }

        return "redirect:/";
    }
}
