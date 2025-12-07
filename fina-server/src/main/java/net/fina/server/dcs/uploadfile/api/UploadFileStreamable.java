package net.fina.server.dcs.uploadfile.api;

import java.io.InputStream;

public interface UploadFileStreamable {

    void readFileStream(InputStream inputStream) throws Exception;
}
