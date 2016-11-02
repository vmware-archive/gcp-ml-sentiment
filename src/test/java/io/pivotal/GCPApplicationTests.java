package io.pivotal;


import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import io.pivotal.service.BigQueryApiService;
import io.pivotal.service.VisionApiService;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class GCPApplicationTests {

    private Map<String, Path> fileToPath;

    public GCPApplicationTests() {
        fileToPath = new HashMap<>();
    }

    // Provide access to files within src/test/resources/ directory
    private Path getPath(String fileName) {
        Path rv;
        System.out.println("Getting path for " + fileName);
        if (fileToPath.containsKey(fileName)) {
            rv = fileToPath.get(fileName);
        } else {
            //ClassLoader classLoader = getClass().getClassLoader();
            //File file = new File(classLoader.getResource(fileName).getFile()); // Better
            //File file = new File(getClass().getResource(fileName).getFile()); // Worse
            String path = this.getClass().getClassLoader().getResource(fileName).getPath();
            if (path != null) {
                System.out.println("Path: " + path);
            } else {
                System.out.println("Path is null!");
            }
            File file = new File(path);
            rv = file.toPath();
            fileToPath.put(fileName, rv);
        }
        return rv;
    }

    @Test
    public void accessVisionApiTest() throws Exception {
        VisionApiService vps = new VisionApiService();
        byte[] array = Files.readAllBytes(getPath("bridge.jpg"));
        assertFalse(vps.requestLandmarkInfo(array).isEmpty());
        array = Files.readAllBytes(getPath("taj.jpg"));
        assertFalse(vps.requestLandmarkInfo(array).isEmpty());
    }

    @Test
    public void accessLandmarkApiTest() throws Exception {
        VisionApiService vps = new VisionApiService();
        
        byte[] array = Files.readAllBytes(getPath("bridge.jpg"));
        //assertFalse(vps.requestPhotoLabelInfo(array).isEmpty());
        assertFalse(vps.identifyLandmark(array, 10).isEmpty());

        array = Files.readAllBytes(getPath("taj.jpg"));
        //assertFalse(vps.requestPhotoLabelInfo(array).isEmpty());
        assertFalse(vps.identifyLandmark(array, 10).isEmpty());
    }

    @Test
    public void testExecuteBigQueryQuery() throws IOException {
        BigQueryApiService bqs = new BigQueryApiService("Taj Mahal");

        List<TableRow> results = bqs.executeQuery();
        System.out.println("Iterating over returned results");
        System.out.println(results.size());
        for (TableRow row : results) {
            for (TableCell field : row.getF()) {
                System.out.printf("%-50s", field.getV());
            }
            System.out.println();
        }
    }

    /*
    @Test
    public void testExecuteBigQueryQueryWithLandmarkName() throws IOException, GeneralSecurityException {
        VisionApiService vps = new VisionApiService();
        byte[] array = Files.readAllBytes(getPath("taj.jpeg"));
        List<EntityAnnotation> landmarkInfoArray = vps.requestLandmarkInfo(array);
        assertFalse(landmarkInfoArray.isEmpty());
        EntityAnnotation landmarkResult = landmarkInfoArray.get(0);
        String landmarkName = landmarkResult.getDescription();
        System.out.println(landmarkName.trim());
        BigQueryApiService bqs = new BigQueryApiService(landmarkName);
        java.util.List<TableRow> results = bqs.executeQuery();
        assertNotEquals(0, results.size());
        for (TableRow row : results) {
            for (TableCell field : row.getF()) {
                System.out.printf("%-50s", field.getV());
            }
            System.out.println();
        }
        array = Files.readAllBytes(getPath("flatiron_building.jpg"));
        landmarkInfoArray = vps.requestLandmarkInfo(array);
        assertFalse(landmarkInfoArray.isEmpty());
        landmarkResult = landmarkInfoArray.get(0);
        landmarkName = landmarkResult.getDescription();
        System.out.println(landmarkName.trim());
        bqs = new BigQueryApiService(landmarkName);
        results = bqs.executeQuery();
        assertNotEquals(0, results.size());
        for (TableRow row : results) {
            for (TableCell field : row.getF()) {
                System.out.printf("%-50s", field.getV());
            }
            System.out.println();
        }
    } */
}