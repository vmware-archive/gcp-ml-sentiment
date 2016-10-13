package io.pivotal.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by mgoddard on 10/13/16.
 */
public class FileHandler {

    // To clean up temp files, look for this prefix on file names
    private static final String FILENAME_PREFIX = "landmark_";
    private static String imageFileDir; // Directory housing images

    private MultipartFile multipartFile;
    private File imageFile;

    public FileHandler(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    // Provide access to the uploaded file
    private void convertMultipartFileToFile() throws IOException
    {
        // Filename accessible with convFile.getAbsolutePath()
        this.imageFile = File.createTempFile("temp", ".xlsx"); // TODO: finish this part
        FileOutputStream fos = new FileOutputStream(this.imageFile);
        fos.write(multipartFile.getBytes());
        fos.close();
    }

    // Provide a random file name
    private static String getFilename () {
        return FILENAME_PREFIX + UUID.randomUUID().toString();
    }

    // Test to see where temp files will get written, save that as imageFileDir
    // Static so it can be called without creating an instance, by FileServerConfig
    public static String getImageFileDir () throws IOException {
        if (imageFileDir == null) {
            synchronized(FileHandler.imageFileDir) {
                File temp = File.createTempFile("test_for_path", "test");
                imageFileDir = temp.getParent();
                temp.delete();
            }
        }
        return imageFileDir;
    }

    // Types supported by Vision: PNG, JPEG, GIF (strip the 'E' in JPEG)
    // Ref. https://cloud.google.com/vision/docs/best-practices
    private String getFileExtension () {
        String contentType = multipartFile.getContentType();
        String ext = contentType.split("/")[1]; // "image/jpeg"
        if ("jpeg".equals(ext)) {
            ext = "jpg";
        }
        return ext;
    }

}
