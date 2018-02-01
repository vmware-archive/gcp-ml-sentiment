package io.pivotal.service;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mgoddard on 10/17/16.
 */

@Component
public class ImageResizingService {

    private final static Logger logger = LoggerFactory.getLogger(ImageResizingService.class);

    @Value("${image-resizing-service-url}")
    private String imageResizingServiceUrl;

    private static final int THUMB_SIZE = 256;
    private static final int VISION_SIZE = 800;
    private static Boolean IS_AVAILABLE;
    private static final String STATUS_OK = "STATUS_OK"; // Matches return value of /status in resizing service

    public ImageResizingService() {
    }

    // Get an optimally sized version for the ML Vision API
    public byte[] resizeForVisionApi(byte[] imgIn) throws IOException {
        return resizeImage(imgIn, VISION_SIZE);
    }

    // Resize image to specified size, using the service at resizeUrl
    public byte[] resizeImage(byte[] imgIn, int size) throws IOException {
        try (CloseableHttpClient httpclient = getCloseableHttpClient()) {
            HttpPost httppost = new HttpPost(imageResizingServiceUrl + "/resize");
            ByteArrayBody bin = new ByteArrayBody(imgIn, "filename");
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", bin)
                    .addTextBody("size", Integer.toString(size))
                    .build();
            httppost.setEntity(reqEntity);
            logger.info("Calling image resizing service: " + httppost.getRequestLine());
            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                logger.info("HTTP status: " + response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                byte[] imgBuf = null;
                if (resEntity != null) {
                    InputStream imgStream = resEntity.getContent();
                    imgBuf = IOUtils.toByteArray(imgStream);
                    imgStream.close();
                } else {
                    logger.info("HTTP response was null");
                }
                return imgBuf;
            }
        }
    }

    private CloseableHttpClient getCloseableHttpClient() {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();
            clientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext));
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            logger.warn(e.getMessage(), e);
        }

        return clientBuilder.build();
    }

    public boolean isEnabled() {
        boolean rv = false;
        if (imageResizingServiceUrl != null && imageResizingServiceUrl.startsWith("http")) {
            rv = isAvailable();
        }
        return rv;
    }

    // Run once per startup of this app.
    private synchronized boolean isAvailable() {
        boolean rv = false;
        if (IS_AVAILABLE == null) {
            try (CloseableHttpClient httpclient = getCloseableHttpClient()) {
                // This route, "/test", must exist in the Python based resizing service
                HttpGet httpGet = new HttpGet(imageResizingServiceUrl + "/status");
                try (CloseableHttpResponse resp =httpclient.execute(httpGet)) {
                    ;
                    StatusLine statusLine = resp.getStatusLine();
                    logger.info("Image Resizing Service test result: " + statusLine);
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        rv = true;
                        logger.info("Got 200 HTTP response");
                        HttpEntity entity = resp.getEntity();
                        if (EntityUtils.toString(entity).equals(STATUS_OK)) {
                            logger.info("Got " + STATUS_OK + " from /status");
                            rv = true;
                        }
                    } else {
                        rv = false;
                        logger.info("Got ERROR HTTP response");
                    }
                }
            } catch(Exception e){
                logger.error(e.getMessage(), e);
                rv = false;
            }
            IS_AVAILABLE = rv;
        } else {
            rv = IS_AVAILABLE;
        }
        return rv;
    }

}
