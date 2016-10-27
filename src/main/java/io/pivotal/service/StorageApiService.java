package io.pivotal.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.api.services.storage.Storage;
import io.pivotal.domain.MultipartFileWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;

import io.pivotal.CredentialManager;
import org.springframework.web.servlet.mvc.condition.MediaTypeExpression;

@Component
public class StorageApiService {

    private static final CredentialManager CREDENTIAL_MANAGER = new CredentialManager();
    private static final int THUMBNAIL_SIZE = 256; // Size in pixels of square box image will be sized to fit
    private static final int VISION_SIZE = 800; // Optimal size for the Vision API

    @Value("${image-resizing-service-url:NOT_SET}")
    private String imageResizingServiceUrl;

    public StorageApiService() {
    }

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

    public Map<String, String> getUploadedImages(String bucket) {
        try {
            List<StorageObject> objects = CREDENTIAL_MANAGER.getStorageClient().objects().list(bucket).execute()
                    .getItems(); // It seems that a NPE is possible here, if there are no images
            Map<String, String> images = objects.stream().collect(
                    Collectors.toMap(s -> s.getId().substring(s.getId().lastIndexOf("/") + 1), StorageObject::getName));
            return images;
        } catch (Exception e) {
            System.err.println(e);
            return new HashMap<>();
        }
    }

    public void deleteUploadedImages(String bucket) {
        try {
            // storage.objects().delete("mybucket", "myobject").execute();
            List<StorageObject> objects = CREDENTIAL_MANAGER.getStorageClient().objects().list(bucket).execute()
                    .getItems(); // It seems that a NPE is possible here, if there are no images
            for (StorageObject object : objects) {
                // getId() returns: gcp-storage-mike/the_image.jpg/1477418629210000
                String fileName = object.getId().split("/")[1];
                System.out.printf("Deleting %s from bucket %s\n", fileName, bucket);
                CREDENTIAL_MANAGER.getStorageClient().objects().delete(bucket, fileName).execute();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public String getResizedImageUrl(String bucket, String object, int size) {
        // http://image-resizing-service.apps.pcf-on-gcp.com/?size=1200&urlBase64=aHR0cDovL3N...X0Jlbi5qcGc=
        String url = getPublicUrl(bucket, object);
        if (imageResizingServiceUrl != null && !"NOT_SET".equals(imageResizingServiceUrl)) {
            System.out.println("Using image resize service.  URL: " + imageResizingServiceUrl);
            try {
                String urlBase64 = new String(Base64.getEncoder().encode(url.getBytes("UTF-8")), "UTF-8");
                url = String.format("%s/?size=%d&urlBase64=%s", imageResizingServiceUrl, size, urlBase64);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    public String getThumbnailUrl(String bucket, String object) {
        return getResizedImageUrl(bucket, object, THUMBNAIL_SIZE);
    }

    // Assumption: Vision API will also accept a URL.  TODO: verify this.
    public String getVisionUrl(String bucket, String object) {
        return getResizedImageUrl(bucket, object, VISION_SIZE);
    }

    public String getPublicUrl(String bucket, String object) {
        return String.format("http://storage.googleapis.com/%s/%s", bucket, object);
    }

}
