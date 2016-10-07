package io.pivotal;

import com.google.api.services.language.v1beta1.CloudNaturalLanguageAPI;
import com.google.api.services.language.v1beta1.model.AnalyzeSentimentRequest;
import com.google.api.services.language.v1beta1.model.AnalyzeSentimentResponse;
import com.google.api.services.language.v1beta1.model.Document;
import com.google.api.services.language.v1beta1.model.Sentiment;
import com.google.api.services.vision.v1.model.EntityAnnotation;
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
    @ResponseBody
    public String renderResults(Model model) {
        return "Here are results";
    }



    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {



        System.out.println(file.getContentType());
        System.out.println(file.getName());
        System.out.println("You successfully uploaded " + file.getOriginalFilename() + "!");
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");


        VisionApiService vps = new VisionApiService();

        try {
            List<EntityAnnotation> results = vps.identifyLandmark(file.getBytes(),10);
            System.out.println(results);
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
