package net.fina.server.postbox.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.FileTypeCheckUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.postbox.api.PostboxLocal;
import net.fina.server.postbox.entity.PostboxFile;
import net.fina.server.postbox.model.PostboxFileMetaModel;
import net.fina.server.postbox.model.PostboxFileMetaModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.POSTBOX_REVIEW)
public class PostboxProxy {

    @EJB
    private PostboxLocal postboxLocal;

    public PostboxFileMetaModel load(long fileId) {
        return PostboxFileMetaModelHelper.toModel(postboxLocal.findById(fileId));
    }

    public List<PostboxFileMetaModel> load(int limit, int offset) {
        return load(limit, offset, null, null, null).getList();
    }

    public PaginatedListWrapper<PostboxFileMetaModel> load(int limit, int offset, Integer page, String sortField, String sortDir) {

        SortInfo sortInfo = getSortInfo(sortField, sortDir);

        List<PostboxFileMetaModel> postboxFileMetaModels = PostboxFileMetaModelHelper.toModels(postboxLocal.load(limit, offset, sortInfo));

        PaginatedListWrapper<PostboxFileMetaModel> result = new PaginatedListWrapper<>();
        result.setList(postboxFileMetaModels);
        result.setTotalResults(getFileCount());
        result.setPageSize(limit);
        result.setCurrentPage(page != null ? page : 1);
        return result;
    }

    public PostboxFileMetaModel loadWithContents(long fileId) throws FinATypeException {
        PostboxFile file = postboxLocal.findById(fileId);
        try {
            PostboxFileMetaModel model = PostboxFileMetaModelHelper.toModel(file);
            model.setContent(postboxLocal.getContent(file));
            return model;
        } catch (Throwable t) {
            throw new FinATypeException(t, FinATypeException.Type.GENERAL_ERROR);
        }
    }

    public List<PostboxFileMetaModel> loadWithContents(List<Long> fileIds) throws FinATypeException {
        List<PostboxFileMetaModel> result = new ArrayList<>();

        for (Long fileId : fileIds) {
            result.add(loadWithContents(fileId));
        }

        return result;
    }

    public int getFileCount() {
        return ((Long) postboxLocal.getFileCount()).intValue();
    }

    public PostboxFileMetaModel save(PostboxFileMetaModel fileModel) throws FinATypeException, WrongFileTypeException {
        PostboxFile file = PostboxFileMetaModelHelper.fromModel(fileModel);
        long originalFileId = file.getId();

        if (originalFileId <= 0) {
            FileTypeCheckUtil.checkExecutable(fileModel.getContent());
        }

        file = postboxLocal.save(file);
        if (originalFileId <= 0) {
            postboxLocal.saveContent(file, fileModel.getContent());
        }
        fileModel.setId(file.getId());
        return fileModel;
    }

    public void delete(long fileId) throws FinATypeException {
        postboxLocal.delete(fileId);
    }

    public List<Long> delete(List<Long> fileIds) throws FinATypeException {
        if (fileIds != null && !fileIds.isEmpty()) {
            for (Long fileId : fileIds) {
                delete(fileId);
            }
        }
        return fileIds;
    }

    private SortInfo getSortInfo(String sortField, String sortDir) {
        sortField = sortField == null || sortField.trim().isEmpty() ? "uploadTime" : sortField;
        sortDir = sortDir == null || sortDir.trim().isEmpty() ? "DESC" : sortDir.toUpperCase();

        return new SortInfo(sortField, sortDir);
    }
}
