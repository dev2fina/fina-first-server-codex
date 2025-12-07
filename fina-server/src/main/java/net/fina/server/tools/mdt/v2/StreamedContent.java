package net.fina.server.tools.mdt.v2;

import java.io.InputStream;

/**
 * Created by olegdm on 1/10/14.
 */
public interface StreamedContent {

    public String getName();

    public InputStream getStream();

    public String getContentType();

}
