package net.fina.server.license.model;

import net.fina.server.license.entity.BankingOperation;
import net.fina.server.i18n.helper.Description;
import net.fina.server.license.entity.LicenceType;

import java.util.List;
import java.util.stream.Collectors;

public class BankingOperationModelHelper {

    public static BankingOperationMetaModel toModel(BankingOperation operation, long langId) {
        BankingOperationMetaModel result = new BankingOperationMetaModel();

        result.setId(operation.getId());
        result.setParentId(operation.getParentId());
        result.setCode(operation.getCode());
        result.setForeignCurrency(operation.isForeignCurrency());
        result.setNationalCurrency(operation.isNationalCurrency());
        result.setDescription(operation.getDescription().getDescription(langId));
        if (operation.getLicenceType() != null) {
            result.setLicenseTypeId(operation.getLicenceType().getId());
        }
        return result;
    }

    public static BankingOperation toEntity(BankingOperationMetaModel operation, long langId) {
        BankingOperation result = new BankingOperation();

        result.setId(operation.getId());
        result.setParentId(operation.getParentId());
        result.setCode(operation.getCode());
        result.setForeignCurrency(operation.isForeignCurrency());
        result.setNationalCurrency(operation.isNationalCurrency());
        result.setDescription(new Description(langId, operation.getDescriptionStrId(), operation.getDescription()));

        if (operation.getLicenseTypeId() > 0) {
            LicenceType licenceType = new LicenceType();
            licenceType.setId(operation.getLicenseTypeId());
            result.setLicenceType(licenceType);
        }
        return result;
    }

    public static List<BankingOperationMetaModel> toModels(List<BankingOperation> operations, long langId) {
        return operations.stream().map(op -> toModel(op, langId)).collect(Collectors.toList());
    }

    public static List<BankingOperation> toEntities(List<BankingOperationMetaModel> operations, long langId) {
        return operations.stream().map(op -> toEntity(op, langId)).collect(Collectors.toList());
    }
}
