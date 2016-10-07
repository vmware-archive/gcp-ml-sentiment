package io.pivotal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.GeneralSecurityException;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.language.v1beta1.CloudNaturalLanguageAPI;
import com.google.api.services.language.v1beta1.CloudNaturalLanguageAPIScopes;
import com.google.api.services.language.v1beta1.model.AnalyzeSentimentRequest;
import com.google.api.services.language.v1beta1.model.AnalyzeSentimentResponse;
import com.google.api.services.language.v1beta1.model.Document;
import com.google.api.services.language.v1beta1.model.Sentiment;

/*
 * Google API: https://developers.google.com/api-client-library/java/
 * SCDF: http://engineering.pivotal.io/post/spring-cloud-data-flow-sink/
 */

public class CredentialManager {

	public static CloudNaturalLanguageAPI nlpApi() throws IOException, GeneralSecurityException {
		// HttpTransport trans = UrlFetchTransport.getDefaultInstance();
		HttpTransport trans = GoogleNetHttpTransport.newTrustedTransport();
		// JacksonFactory jFactory = new JacksonFactory();
		// JsonFactory jFactory = new JacksonFactory();
		JsonFactory jFactory = JacksonFactory.getDefaultInstance();
		GoogleCredential cred = credential();
		if (cred.createScopedRequired()) {
			cred = cred.createScoped(CloudNaturalLanguageAPIScopes.all());
		}
		return new CloudNaturalLanguageAPI.Builder(trans, jFactory, cred).setApplicationName(APP_NAME).build();
	}

	private static GoogleCredential credential = null;
	private static final String APP_NAME = "spring-nlp";

	private static GoogleCredential credential() throws IOException {
		if (credential == null) {
			InputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(getPrivateKeyData()));
			/*
			 * The following line causes an error:
			 * "java.lang.VerifyError: Cannot inherit from final class"
			 * 
			 * The approach shown in this link, adding "-verbose:class", does
			 * help track down the cause:
			 * 
			 * http://stackoverflow.com/questions/100107/causes-of-getting-a-
			 * java-lang-verifyerror
			 */
			credential = GoogleCredential.fromStream(stream);
		}
		return credential;
	}

	// Return the Base64 encoded private key data string
	private static String getPrivateKeyData() {
		String env = System.getenv("VCAP_SERVICES");
		JSONObject json = new JSONObject(env);
		JSONArray root = json.getJSONArray("google-ml-apis");
		JSONObject obj0 = root.getJSONObject(0);
		String credString = obj0.getString("credentials");
		JSONObject cred = new JSONObject(credString);
		return cred.getString("PrivateKeyData");
	}

	@SuppressWarnings("unused")
	private static void printClasspath() {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();
		for (URL url : urls) {
			System.out.println(url.getFile());
		}
	}

	public static void main(String[] args) throws Exception {
		// printClasspath(); // to debug that class loader issue
		PrintStream stdout = System.out;
		stdout.println("Attempting to parse VCAP_SERVICES ...");
		// stdout.println(getPrivateKeyData());
		stdout.println("Attempting to get instance of NLP API ...");
		CloudNaturalLanguageAPI api = nlpApi();
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
		stdout.println("Tweet: \"" + tweet + "\"");
		stdout.println("  Magnitude: " + magnitude + ", Polarity: " + polarity);
	}

}
