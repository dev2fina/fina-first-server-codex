package net.fina.server.license.model;

import net.fina.server.license.entity.LicenseComment;

import java.util.List;
import java.util.stream.Collectors;

public class LicenseCommentModelHelper {

    public static LicenseCommentMetaModel toModel(LicenseComment comment) {
        LicenseCommentMetaModel result = new LicenseCommentMetaModel();

        result.setId(comment.getId());
        result.setComment(comment.getComment());
        if (comment.getModifiedAt() != null) {
            result.setModifiedAt(comment.getModifiedAt());
        }

        return result;
    }

    public static LicenseComment toEntity(LicenseCommentMetaModel comment) {
        LicenseComment result = new LicenseComment();

        result.setId(comment.getId());
        result.setComment(comment.getComment());
        if (comment.getModifiedAt() != null) {
            result.setModifiedAt(comment.getModifiedAt());
        }

        return result;
    }

    public static List<LicenseCommentMetaModel> toModels(List<LicenseComment> comments) {
        return comments.stream().map(LicenseCommentModelHelper::toModel).collect(Collectors.toList());
    }

    public static List<LicenseComment> toEntities(List<LicenseCommentMetaModel> comments) {
        return comments.stream().map(LicenseCommentModelHelper::toEntity).collect(Collectors.toList());
    }
}
