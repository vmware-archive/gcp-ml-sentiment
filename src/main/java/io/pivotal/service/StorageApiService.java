package io.pivotal.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;
import io.pivotal.gcp.StorageCredentialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class StorageApiService {

    private final static Logger logger = LoggerFactory.getLogger(StorageApiService.class);

    private static final int THUMBNAIL_SIZE = 256; // Size in pixels of square box image will be sized to fit
    private static final int VISION_SIZE = 800; // Optimal size for the Vision API

    @Autowired
    private StorageCredentialManager credentialManager;

    @Value("${image-resizing-service-url:NOT_SET}")
    private String imageResizingServiceUrl;

    public StorageApiService() {
    }

    public boolean upload(MultipartFile file) {
        String name = file.getOriginalFilename();
        StorageObject objectMetadata = new StorageObject().setName(name)
                .setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

        String type = file.getContentType();

        try {
            InputStreamContent content = new InputStreamContent(type, file.getInputStream());
            credentialManager.getClient().objects().insert(credentialManager.getBucketName(), objectMetadata, content).setName(name).execute();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public Map<String, String> getUploadedImages() {
        return listObjects()
                .stream()
                .collect(Collectors.toMap(s -> s.getId().substring(s.getId().lastIndexOf("/") + 1), StorageObject::getName));
    }

    public void deleteUploadedImages() {
        String bucket = credentialManager.getBucketName();
        Storage client = credentialManager.getClient();

        listObjects().stream()
                .map(o -> o.getId().split("/")[1])
                .forEach(filename -> {
                    logger.info(String.format("Deleting %s from bucket %s\n", filename, bucket));
                    try {
                        client.objects().delete(bucket, filename).execute();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                });
    }

    private List<StorageObject> listObjects() {
        try {
            Storage client = credentialManager.getClient();
            String bucketName = credentialManager.getBucketName();
            return Optional.ofNullable(client.objects()
                    .list(bucketName)
                    .execute()
                    .getItems()).orElseThrow(EmptyStackException::new);
        } catch (EmptyStackException e) {
            logger.warn("Empty bucket");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public String getResizedImageUrl(String object, int size) {
        if (imageResizingServiceUrl != null && !"NOT_SET".equals(imageResizingServiceUrl)) {
            logger.info("Using image resize service.  URL: " + imageResizingServiceUrl);
            String urlBase64 = getPublicUrlBase64(object);
            return String.format("%s/?size=%d&urlBase64=%s", imageResizingServiceUrl, size, urlBase64);
        } else {
            return getPublicUrl(object);
        }
    }

    public String getThumbnailUrl(String object) {
        return getResizedImageUrl(object, THUMBNAIL_SIZE);
    }

    public String getGSUrl(String object) {
        return String.format("gs://%s/%s", credentialManager.getBucketName(), object);
    }

    // Assumption: Vision API will also accept a URL.  TODO: verify this.
    public String getVisionUrl(String object) {
        return getResizedImageUrl(object, VISION_SIZE);
    }

    public String getPublicUrl(String object) {
        return String.format("http://storage.googleapis.com/%s/%s", credentialManager.getBucketName(), object);
    }

    public String getPublicUrlBase64(String object) {
        try {
            return new String(Base64.getEncoder().encode(getPublicUrl(object).getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


}
