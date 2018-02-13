package io.pivotal.service;

import io.pivotal.domain.NamedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by mgoddard on 10/17/16.
 */

@Component
public class ImageResizingService {

    private final static Logger logger = LoggerFactory.getLogger(ImageResizingService.class);

    private final static int VISION_SIZE = 800;

    @Value("${image.resizing.service.url}")
    private String imageResizingServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    // Get an optimally sized version for the ML Vision API
    public byte[] resizeForVisionApi(MultipartFile file) throws IOException {
        return resizeImage(file, VISION_SIZE);
    }

    // Resize image to specified size, using the service at resizeUrl
    private byte[] resizeImage(MultipartFile file, int size) throws IOException {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new NamedResource(file.getBytes(), file.getOriginalFilename()));
        map.add("size", size);
        ResponseEntity<byte[]> response = restTemplate.postForEntity(imageResizingServiceUrl + "/resize", map, byte[].class);
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            logger.info("File resized successfully");
            return response.getBody();
        }
        throw new RuntimeException(String.format("Error while resizing file. Status code[%s]", response.getStatusCodeValue()));
    }

    public boolean isEnabled() {
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(imageResizingServiceUrl + "/status", String.class);
            return entity.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            logger.warn("Rest client exception.", e);
            return false;
        }
    }

}
