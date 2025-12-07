package net.fina.server.returns.api;

import net.fina.server.dcs.uploadfile.entity.UploadFile;

import java.util.List;

public interface ReturnNotificationSender {

    void send(List<UploadFile> uploadFiles, String returnStatusBundleText, String note,  String langCode);
}
