package net.fina.server.returns.api;

import net.fina.common.client.returns.ProcessStatus;
import net.fina.server.dcs.uploadfile.entity.UploadFile;

import java.util.List;

public interface ReturnNotificationLocal {

    void sendReturnStatusChangeNotification(List<UploadFile> uploadFiles, ProcessStatus status, String  note,  String langCode);
}
