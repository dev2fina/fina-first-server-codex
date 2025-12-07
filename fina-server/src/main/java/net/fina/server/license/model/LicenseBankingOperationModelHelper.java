package net.fina.server.license.model;

import net.fina.server.license.entity.LicenseBankingOperation;

import java.util.List;
import java.util.stream.Collectors;

public class LicenseBankingOperationModelHelper {
    public static LicenseBankingOperationMetaModel toModel(LicenseBankingOperation operation, long langId) {
        LicenseBankingOperationMetaModel result = new LicenseBankingOperationMetaModel();
        result.setId(operation.getId());
        result.setActive(operation.isActive());
        result.setChangeDate(operation.getChangeDate());
        result.setComments(BankingOperationCommentModelHelper.toModels(operation.getComments()));

        result.setBankingOperation(BankingOperationModelHelper.toModel(operation.getBankingOperation(), langId));

        return result;
    }

    public static LicenseBankingOperation toEntity(LicenseBankingOperationMetaModel operation, long langId) {
        LicenseBankingOperation result = new LicenseBankingOperation();
        result.setId(operation.getId());
        result.setActive(operation.isActive());
        result.setChangeDate(operation.getChangeDate());
        result.setComments(BankingOperationCommentModelHelper.toEntities(operation.getComments()));

        result.setBankingOperation(BankingOperationModelHelper.toEntity(operation.getBankingOperation(), langId));

        return result;
    }

    public static List<LicenseBankingOperation> toEntities(List<LicenseBankingOperationMetaModel> operations, long langId) {
        return operations.stream().map(op -> toEntity(op, langId)).collect(Collectors.toList());
    }

    public static List<LicenseBankingOperationMetaModel> toModels(List<LicenseBankingOperation> operations, long langId) {
        return operations.stream().map(op -> toModel(op, langId)).collect(Collectors.toList());
    }
}
