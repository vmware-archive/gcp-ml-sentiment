package io.pivotal.domain;

import org.springframework.core.io.AbstractResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class NamedResource extends AbstractResource {

    private final ByteBuffer buffer;
    private final String name;

    public NamedResource(byte[] buffer, String name) {
        this.buffer = ByteBuffer.wrap(buffer);
        this.name = name;
    }

    @Override
    public String getFilename() {
        return name;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(buffer.array());
    }
}
