package io.pivotal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

	private static final String VCAP_KEY = "google-storage"; // "google-ml-apis", "user-provided"
	private String privateKeyData;
	private String projectId;

	public CredentialManager() {
		String env = System.getenv("VCAP_SERVICES");
		System.out.println(env);
		JSONObject json = new JSONObject(env);
		JSONArray root = json.getJSONArray(VCAP_KEY);
		System.out.println("root: " + root);
		JSONObject obj0 = root.getJSONObject(0);
		JSONObject cred = obj0.getJSONObject("credentials");
		this.privateKeyData = cred.getString("PrivateKeyData");
		String email = cred.getString("Email");
		this.projectId = matchProjectId(email);
		System.out.println("PrivateKeyData: " + privateKeyData);
		System.out.println("project ID: " + projectId);
	}

	private String matchProjectId (String email) {
		Pattern p = Pattern.compile("^[^@]+@([^.]+)\\..+$");
		Matcher m = p.matcher(email);
		String rv = "No match";
		if (m.matches()) {
			rv = m.group(1);
		}
		return rv;
	}

	public String getPrivateKeyData () {
		return this.privateKeyData;
	}

	public String getProjectId () {
		return this.projectId;
	}

	public  CloudNaturalLanguageAPI getNLPAPI() throws IOException, GeneralSecurityException {
		HttpTransport trans = GoogleNetHttpTransport.newTrustedTransport();
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

	private GoogleCredential credential() throws IOException {
		if (credential == null) {
			InputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(getPrivateKeyData()));
			credential = GoogleCredential.fromStream(stream);
		}
		return credential;
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
