package io.pivotal;


import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GCPApplicationTests {


    @Test
    public void accessVisionApiTest () throws Exception {
        VisionApiService vps = new VisionApiService();

        byte[] array = Files.readAllBytes(new File("/Users/mross/Downloads/bridge.jpeg").toPath());

        assertFalse(vps.requestLandmarkInfo(array).isEmpty());

        array = Files.readAllBytes(new File("/Users/mross/Downloads/taj.jpeg").toPath());

        assertFalse(vps.requestLandmarkInfo(array).isEmpty());

    }


    @Test
    public void accessLandmarkApiTest () throws Exception {
        VisionApiService vps = new VisionApiService();

        byte[] array = Files.readAllBytes(new File("/Users/mross/Downloads/bridge.jpeg").toPath());

        assertFalse(vps.requestPhotoLabelInfo(array).isEmpty());

        array = Files.readAllBytes(new File("/Users/mross/Downloads/taj.jpeg").toPath());

        assertFalse(vps.requestPhotoLabelInfo(array).isEmpty());

    }




}