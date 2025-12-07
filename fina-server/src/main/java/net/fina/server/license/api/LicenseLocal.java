package net.fina.server.license.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.license.entity.BankingOperation;
import net.fina.server.license.entity.BankingOperationComment;
import net.fina.server.license.entity.LicenceType;
import net.fina.server.license.entity.LicenseComment;

import java.util.List;

public interface LicenseLocal {
    List<LicenceType> loadLicenseTypes();

    LicenceType save(LicenceType licenseType) throws FinATypeException;

    void deleteLicenceType(long licenseTypeId) throws FinATypeException;

    LicenceType loadLicenseType(long id);

    List<BankingOperation> loadBankingOperation(long licenseTypeId);

    List<BankingOperation> loadByParentBankingOperation(long licenseTypeId, long parentId);

    BankingOperation createBankingOperation(BankingOperation operation) throws FinATypeException;

    BankingOperation updateBankingOperation(BankingOperation operation) throws FinATypeException;

    void deleteBankingOperation(long operationId) throws FinATypeException;

    LicenceType getLicenseTypeById(long typeId);

    LicenseComment saveLicenseComment(long licenseId, LicenseComment comment);

    void deleteLicenseComment(long commentId);

    BankingOperationComment saveBankingOperationComment(long bankingOperationId, BankingOperationComment comment);

    void deleteBankingOperationComment(long commentId);

}
