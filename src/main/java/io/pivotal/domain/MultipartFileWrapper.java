package io.pivotal.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mgoddard on 10/17/16.
 */
public class MultipartFileWrapper implements MultipartFile {
    private MultipartFile multipartFile;
    private byte[] bytes;

    public MultipartFileWrapper(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public void setBytes(byte[] b) {
        this.bytes = b;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        if (this.bytes != null) {
            System.out.println("Wrapper: in getBytes()");
            return this.bytes;
        } else {
            return multipartFile.getBytes();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    // The following methods just delegate to the wrapped instance

    @Override
    public String getName() {
        return multipartFile.getName();
    }

    @Override
    public String getOriginalFilename() {
        return multipartFile.getOriginalFilename();
    }

    @Override
    public String getContentType() {
        return multipartFile.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return multipartFile.isEmpty();
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        multipartFile.transferTo(file);
    }
}
