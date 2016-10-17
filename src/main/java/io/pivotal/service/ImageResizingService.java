package io.pivotal.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.io.IOUtils;
import org.omg.CORBA.portable.ValueInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mgoddard on 10/17/16.
 */

@Component
public class ImageResizingService {

    @Value("${image-resizing-service-url:NOT_SET}")
    private String imageResizingServiceUrl;

    private static final int THUMB_SIZE = 256;
    private static final int VISION_SIZE = 800;

    public ImageResizingService() {}

    public boolean isEnabled () {
        return imageResizingServiceUrl != null;
    }

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

}
