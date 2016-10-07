package io.pivotal;


import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;

import com.google.api.services.bigquery.model.TableRow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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

    @Test
    public void testExecuteBigQueryQuery() throws IOException {
        BigQueryApiService bqs = new BigQueryApiService();
        String query = "SELECT BookMeta_Title, BookMeta_Creator, BookMeta_Subjects FROM (TABLE_QUERY([gdelt-bq:internetarchivebooks], 'REGEXP_EXTRACT(table_id, r\"(\\d{4})\") BETWEEN \"1819\" AND \"2014\"')) WHERE LOWER(BookMeta_Subjects) CONTAINS LOWER(\"Taj Mahal \")";

        java.util.List<TableRow> results =bqs.executeQuery(query);
        System.out.println("Iterating over returned results");
        System.out.println(results.size());
        for (TableRow row : results) {
            for (TableCell field : row.getF()) {
                System.out.printf("%-50s", field.getV());
            }
            System.out.println();
        }

    }

    @Test
    public void testExecuteBigQueryQueryWithLandmarkName() throws IOException, GeneralSecurityException {
        VisionApiService vps = new VisionApiService();

        byte[] array =Files.readAllBytes(new File("/Users/mross/Downloads/taj.jpeg").toPath());
//

        List<EntityAnnotation> landmarkInfoArray = vps.requestLandmarkInfo(array);
        assertFalse(landmarkInfoArray.isEmpty());
        EntityAnnotation landmarkResult = landmarkInfoArray.get(0);
        String landmarkName = landmarkResult.getDescription();
        System.out.println(landmarkName.trim());

        BigQueryApiService bqs = new BigQueryApiService();
        String query = String.format("SELECT BookMeta_Title, BookMeta_Creator, BookMeta_Subjects FROM (TABLE_QUERY([gdelt-bq:internetarchivebooks], 'REGEXP_EXTRACT(table_id, r\"(\\d{4})\") BETWEEN \"1819\" AND \"2014\"')) WHERE LOWER(BookMeta_Subjects) CONTAINS LOWER(\"%s \")",landmarkName);

        java.util.List<TableRow> results =bqs.executeQuery(query);
        assertNotEquals(0,results.size());
        for (TableRow row : results) {
            for (TableCell field : row.getF()) {
                System.out.printf("%-50s", field.getV());
            }
            System.out.println();
        }



    array =Files.readAllBytes(new File("/Users/mross/Downloads/loc-flatiron1.jpg").toPath());


        landmarkInfoArray = vps.requestLandmarkInfo(array);
        assertFalse(landmarkInfoArray.isEmpty());
        landmarkResult = landmarkInfoArray.get(0);
        landmarkName = landmarkResult.getDescription();

        System.out.println(landmarkName.trim());

       bqs = new BigQueryApiService();
        query = String.format("SELECT BookMeta_Title, BookMeta_Creator, BookMeta_Subjects FROM (TABLE_QUERY([gdelt-bq:internetarchivebooks], 'REGEXP_EXTRACT(table_id, r\"(\\d{4})\") BETWEEN \"1819\" AND \"2014\"')) WHERE LOWER(BookMeta_Subjects) CONTAINS LOWER(\"%s \")",landmarkName);

         results =bqs.executeQuery(query);
        assertNotEquals(0,results.size());

        for (TableRow row : results) {
        for (TableCell field : row.getF()) {
            System.out.printf("%-50s", field.getV());
        }
        System.out.println();
    }

}







}