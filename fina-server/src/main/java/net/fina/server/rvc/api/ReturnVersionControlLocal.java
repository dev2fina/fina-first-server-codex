package net.fina.server.rvc.api;

import net.fina.fsop.view.model.FsopDataMetaModel;
import net.fina.fsop.view.model.FsopReviewModel;
import net.fina.server.rvc.event.ReturnItemsStoreEvent;
import net.fina.server.rvc.event.ReturnStatusStoreEvent;
import net.fina.server.store.model.RepositoryFile;

import java.io.InputStream;
import java.util.List;

public interface ReturnVersionControlLocal {

    void storeReturn(ReturnItemsStoreEvent itemsStoreEvent, ReturnStatusStoreEvent statusStoreEvent);

    RepositoryFile loadReturnFileByIdAndFileType(long returnId, String fileName);

    InputStream getFileStreamByVersion(String parentPath, String fileName, String versionId);

    FsopReviewModel getFsopReviewModel(List<FsopDataMetaModel> dataModel, String langCode, String fileType);
}
