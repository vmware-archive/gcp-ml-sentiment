package io.pivotal.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mgoddard on 10/17/16.
 */

@Component
public class ImageResizingService {

    @Value("${image-resizing-service-url}")
    private String imageResizingServiceUrl;

    private static final int THUMB_SIZE = 256;
    private static final int VISION_SIZE = 800;
    private static Boolean IS_AVAILABLE;
    private static final String STATUS_OK = "STATUS_OK"; // Matches return value of /status in resizing service

    public ImageResizingService() {}

    // Get an optimally sized version for the ML Vision API
    public byte[] resizeForVisionApi(byte[] imgIn) throws IOException {
        return resizeImage(imgIn, VISION_SIZE);
    }

    // Resize image to specified size, using the service at resizeUrl
    public byte[] resizeImage(byte[] imgIn, int size) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(imageResizingServiceUrl + "/resize");
        ByteArrayBody bin = new ByteArrayBody(imgIn, "filename");
        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("file", bin)
                .addTextBody("size", Integer.toString(size))
                .build();
        httppost.setEntity(reqEntity);
        System.out.println("Calling image resizing service: " + httppost.getRequestLine());
        CloseableHttpResponse response = httpclient.execute(httppost);
        System.out.println("HTTP status: " + response.getStatusLine());
        HttpEntity resEntity = response.getEntity();
        byte[] imgBuf = null;
        if (resEntity != null) {
            InputStream imgStream = resEntity.getContent();
            imgBuf = IOUtils.toByteArray(imgStream);
            imgStream.close();
        } else {
            System.out.println("HTTP response was null");
        }
        return imgBuf;
    }

    public boolean isEnabled () {
        boolean rv = false;
        if (imageResizingServiceUrl != null && imageResizingServiceUrl.startsWith("http")) {
            rv = isAvailable();
        }
        return rv;
    }

    // Run once per startup of this app.
    private boolean isAvailable() {
        boolean rv = false;
        if (IS_AVAILABLE == null) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // This route, "/test", must exist in the Python based resizing service
            HttpGet httpGet = new HttpGet(imageResizingServiceUrl + "/status");
            CloseableHttpResponse resp = null;
            try {
                resp = httpclient.execute(httpGet);
                StatusLine statusLine = resp.getStatusLine();
                System.out.println("Image Resizing Service test result: " + statusLine);
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    rv = true;
                    System.out.println("Got 200 HTTP response");
                    HttpEntity entity = resp.getEntity();
                    if (EntityUtils.toString(entity).equals(STATUS_OK)) {
                        System.out.println("Got " + STATUS_OK + " from /status");
                        rv = true;
                    }
                } else {
                    rv = false;
                    System.out.println("Got ERROR HTTP response");
                }
            } catch (Exception e) {
                rv = false;
                e.printStackTrace();
            } finally {
                try {
                    resp.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            IS_AVAILABLE = new Boolean(rv);
        } else {
            rv = IS_AVAILABLE.booleanValue();
        }
        return rv;
    }

}
