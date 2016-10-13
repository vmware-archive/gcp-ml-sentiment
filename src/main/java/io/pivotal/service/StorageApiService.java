package io.pivotal.service;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;

import io.pivotal.CredentialManager;

public class StorageApiService {

    private static final CredentialManager CREDENTIAL_MANAGER = new CredentialManager();

    public boolean upload(MultipartFile file, String bucket) {
	String name = file.getOriginalFilename();
	StorageObject objectMetadata = new StorageObject().setName(name)
		.setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

	String type = file.getContentType();
	if (bucket == null) {
	    return false;
	}
	try {
	    InputStreamContent content = new InputStreamContent(type, file.getInputStream());
	    CREDENTIAL_MANAGER.getStorageClient().objects().insert(bucket, objectMetadata, content).setName(name).execute();
	} catch (IOException e) {
	    System.err.println(e);
	    return false;
	}
	return true;
    }

    public static String getPublicUrl(String bucket, String object) {
	return String.format("http://storage.googleapis.com/%s/%s", bucket, object);
    }
}
