package net.fina.server.license.model;

import net.fina.server.license.entity.BankingOperationComment;

import java.util.List;
import java.util.stream.Collectors;

public class BankingOperationCommentModelHelper {

    public static BankingOperationComment toEntity(BankingOperationCommentMetaModel operation) {
        BankingOperationComment result = new BankingOperationComment();
        result.setId(operation.getId());
        result.setComment(operation.getComment());
        result.setModifiedAt(operation.getModifiedAt());

        return result;
    }

    public static BankingOperationCommentMetaModel toModel(BankingOperationComment operation) {
        BankingOperationCommentMetaModel result = new BankingOperationCommentMetaModel();
        result.setId(operation.getId());
        result.setComment(operation.getComment());
        result.setModifiedAt(operation.getModifiedAt());
        return result;
    }

    public static List<BankingOperationCommentMetaModel> toModels(List<BankingOperationComment> comments) {
        return comments.stream().map(c -> toModel(c)).collect(Collectors.toList());
    }

    public static List<BankingOperationComment> toEntities(List<BankingOperationCommentMetaModel> comments) {
        return comments.stream().map(c -> toEntity(c)).collect(Collectors.toList());
    }

}
