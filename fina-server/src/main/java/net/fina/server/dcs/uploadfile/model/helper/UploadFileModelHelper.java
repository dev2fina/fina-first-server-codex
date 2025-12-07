package net.fina.server.dcs.uploadfile.model.helper;


import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UploadFileModelHelper {
    public static final DateFormat DEFAULT_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static UploadFileMetaModel toMetaModel(UploadFile uf) {
        return new UploadFileMetaModel().setUploadFile(uf, DEFAULT_DATETIME_FORMAT);
    }

    public static List<UploadFileMetaModel> toMetaModel(List<UploadFile> uploadFiles) {
        List<UploadFileMetaModel> result = new ArrayList<>();
        if (uploadFiles != null) {
            for (UploadFile uf : uploadFiles) {
                result.add(new UploadFileMetaModel().setUploadFile(uf, DEFAULT_DATETIME_FORMAT));
            }
        }
        return result;
    }
}
