package io.pivotal;

import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.language.v1beta1.CloudNaturalLanguageAPI;
import com.google.api.services.language.v1beta1.model.AnalyzeSentimentRequest;
import com.google.api.services.language.v1beta1.model.AnalyzeSentimentResponse;
import com.google.api.services.language.v1beta1.model.Document;
import com.google.api.services.language.v1beta1.model.Sentiment;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import io.pivotal.Domain.QueryResultsViewMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class NLPController {


    @RequestMapping("/")
    public String rednerIndex() {
        return "index";
    }

    @RequestMapping("/results")
    public String renderResults(Model model) {
        return "results";
    }



    @RequestMapping(name="/upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {


        QueryResultsViewMapping viewMapping = new QueryResultsViewMapping();
        System.out.println(file.getContentType());
        System.out.println(file.getName());
        System.out.println("You successfully uploaded " + file.getOriginalFilename() + "!");
        redirectAttributes.addFlashAttribute("message",
                "Processed File " + file.getOriginalFilename() + "!");


        VisionApiService vps = new VisionApiService();
        ArrayList<QueryResultsViewMapping> queryResults = new ArrayList<>();

        try {
            List<EntityAnnotation> landmarkInfoArray = vps.identifyLandmark(file.getBytes(),10);
            EntityAnnotation landmarkResult = landmarkInfoArray.get(0);
            String landmarkName = landmarkResult.getDescription();

            BigQueryApiService bqs = new BigQueryApiService();
            String query = String.format("SELECT BookMeta_Title, BookMeta_Creator, BookMeta_Subjects FROM (TABLE_QUERY([gdelt-bq:internetarchivebooks], 'REGEXP_EXTRACT(table_id, r\"(\\d{4})\") BETWEEN \"1819\" AND \"2014\"')) WHERE LOWER(BookMeta_Subjects) CONTAINS LOWER(\"%s \")",landmarkName);

            java.util.List<TableRow> results =bqs.executeQuery(query);
            for (TableRow row : results) {
                viewMapping = new QueryResultsViewMapping(row);
                queryResults.add(viewMapping);

//                for (TableCell field : row.getF()) {
//
//
//                    System.out.printf("%-50s", field.getV());
//                    queryResults.add(row.getF().toString());
//                }


                System.out.println();
            }
            System.out.println(queryResults);


            redirectAttributes.addFlashAttribute("queryResults",
                    queryResults);
        }  catch (Exception e) {
            System.out.println(e);
        }


        return "redirect:/results";
    }


	@RequestMapping("/test")
    @ResponseBody
    public HashMap<String,String> index() {
        // printClasspath(); // to debug that class loader issue
        PrintStream stdout = System.out;
        CredentialManager credentialManager = new CredentialManager();
        stdout.println("Attempting to parse VCAP_SERVICES ...");
        // stdout.println(getPrivateKeyData());
        stdout.println("Attempting to get instance of NLP API ...");

        HashMap<String,String> resultSet = new HashMap<>();

        try {

            CloudNaturalLanguageAPI api = credentialManager.getNLPAPI();
            stdout.println("Ok");
            // TODO: pull all this out and into a request handler method
            // Ref.
            // https://cloud.google.com/natural-language/docs/sentiment-tutorial
            AnalyzeSentimentRequest req = new AnalyzeSentimentRequest(); // Reuse this?
            String tweet = "It doesn’t play well on TV ... and frankly, the ignoring of the female moderator "
                    + "was really a low point.";
            Document doc = new Document();
            doc.setType("PLAIN_TEXT");
            doc.setContent(tweet);
            req.setDocument(doc);
            AnalyzeSentimentResponse resp = api.documents().analyzeSentiment(req).execute();
            Sentiment sentiment = resp.getDocumentSentiment();
            // Most useful: multiply polarity X magnitude
            Float polarity = sentiment.getPolarity();
            Float magnitude = sentiment.getMagnitude();

            resultSet.put("Tweet",tweet);
            resultSet.put("Magnitude",magnitude+"");
            resultSet.put("Polarity",polarity+"");
            stdout.println("Tweet: \"" + tweet + "\"");
            stdout.println("  Magnitude: " + magnitude + ", Polarity: " + polarity);


        } catch (Exception e ) {
            System.out.println(e);
            resultSet.put("exception",e+ "");
        }

        return resultSet;
    }

}
