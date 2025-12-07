package net.fina.server.tools.mdt.v2;

/**
 * Created by olegdm on 1/10/14.
 */

import java.io.InputStream;
import java.io.Serializable;

/**
 * Default implementation of a StreamedContent
 */
public class DefaultStreamedContent implements StreamedContent, Serializable {

    private InputStream stream;

    private String contentType;

    private String name;

    public DefaultStreamedContent() {}

    public DefaultStreamedContent(InputStream stream) {
        this.stream = stream;
    }

    public DefaultStreamedContent(InputStream stream, String contentType) {
        this.contentType = contentType;
        this.stream = stream;
    }

    public DefaultStreamedContent(InputStream stream, String contentType, String name) {
        this.contentType = contentType;
        this.stream = stream;
        this.name = name;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
