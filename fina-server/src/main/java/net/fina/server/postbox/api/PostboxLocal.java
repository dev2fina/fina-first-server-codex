package net.fina.server.postbox.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.postbox.entity.PostboxFile;

import java.util.List;

public interface PostboxLocal {

    PostboxFile findById(long id);

    List<PostboxFile> load(int limit, int offset, SortInfo sortInfo);

    PostboxFile save(PostboxFile item);

    void delete(long id) throws FinATypeException;

    void saveContent(PostboxFile item, byte[] content) throws FinATypeException;

    byte[] getContent(PostboxFile item) throws FinATypeException;

    long getFileCount();

}
