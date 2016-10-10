package io.pivotal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.GeneralSecurityException;
import java.util.Base64;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
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

	public  CredentialManager() {

	}

	public  CloudNaturalLanguageAPI getNLPAPI() throws IOException, GeneralSecurityException {
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

	public  Vision getVisionService() throws IOException, GeneralSecurityException {
		String APPLICATION_NAME = "Landmark Finder";

		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		GoogleCredential cred = credential();
		if (cred.createScopedRequired()) {
			cred = cred.createScoped(CloudNaturalLanguageAPIScopes.all());
		}

		return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, cred)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	public  Bigquery getBiqQueryClient() throws IOException {
		// Create the credential
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		GoogleCredential cred = credential();

		// Depending on the environment that provides the default credentials (e.g. Compute Engine, App
		// Engine), the credentials may require us to specify the scopes we need explicitly.
		// Check for this case, and inject the Bigquery scope if required.
		if (cred.createScopedRequired()) {
			cred = cred.createScoped(CloudNaturalLanguageAPIScopes.all());
		}
		return new Bigquery.Builder(transport, jsonFactory, cred)
				.setApplicationName("Bigquery Samples")
				.build();
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
    // VERSION FOR USING `cf cups ...'
	private static String getPrivateKeyData() {
		String env = System.getenv("VCAP_SERVICES");
		System.out.println(env);
		JSONObject json = new JSONObject(env);
		JSONArray root = json.getJSONArray("user-provided");
		System.out.println("root: " + root);
		JSONObject obj0 = root.getJSONObject(0);
		/*String credString = obj0.getString("credentials");
		System.out.println("credString: " + credString);
		JSONObject cred = new JSONObject(credString);
		String rv = cred.getString("PrivateKeyData");*/
		String rv = obj0.getJSONObject("credentials").getString("PrivateKeyData");
		System.out.println("PrivateKeyData: " + rv);
		//return cred.getString("PrivateKeyData");
		return rv;
	}

	// Return the Base64 encoded private key data string
	// ORIGINAL VERSION
	private static String __getPrivateKeyData() {
		String env = System.getenv("VCAP_SERVICES");
		System.out.println(env);
		JSONObject json = new JSONObject(env);
		JSONArray root = json.getJSONArray("google-ml-apis");
		System.out.println("root: " + root);
		JSONObject obj0 = root.getJSONObject(0);
		String credString = obj0.getString("credentials");
		System.out.println("credString: " + credString);
		JSONObject cred = new JSONObject(credString);
		String rv = cred.getString("PrivateKeyData");
		System.out.println("PrivateKeyData: " + rv);
		//return cred.getString("PrivateKeyData");
		return rv;
	}

	@SuppressWarnings("unused")
	private static void printClasspath() {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();
		for (URL url : urls) {
			System.out.println(url.getFile());
		}
	}


}
