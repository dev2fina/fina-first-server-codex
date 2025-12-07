package net.fina.server.postbox.model;

import net.fina.server.postbox.entity.PostboxFile;
import net.fina.server.security.model.helper.UserMetaModelHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PostboxFileMetaModelHelper {
    public static PostboxFile fromModel(PostboxFileMetaModel model) {
        PostboxFile file = new PostboxFile();

        file.setId(model.getId());
        file.setName(model.getName());
        file.setUploadTime(model.getUploadTime());
        if (model.getContent() == null) {
            file.setSize(model.getSize());
        } else {
            file.setSize(model.getContent().length);
        }
        file.setUsers(UserMetaModelHelper.fromModels(model.getUsers()));

        return file;
    }

    public static PostboxFileMetaModel toModel(PostboxFile file) {
        PostboxFileMetaModel model = new PostboxFileMetaModel();
        model.setId(file.getId());
        model.setName(file.getName());
        model.setSize(file.getSize());
        model.setUploadTime(file.getUploadTime());
        if (file.getPublisher() != null) {
            model.setPublisher(UserMetaModelHelper.toModel(file.getPublisher()));
        }
        if (file.getUsers() != null) {
            model.setUsers(UserMetaModelHelper.toModels(file.getUsers()));
        }
        return model;
    }

    public static List<PostboxFileMetaModel> toModels(Collection<PostboxFile> files) {
        if (files == null) {
            return Collections.emptyList();
        }

        List<PostboxFileMetaModel> models = new ArrayList<>(files.size());
        for (PostboxFile file : files) {
            models.add(toModel(file));
        }
        return models;
    }
}
