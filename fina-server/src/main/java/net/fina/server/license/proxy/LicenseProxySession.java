package net.fina.server.license.proxy;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.license.api.LicenseLocal;
import net.fina.server.license.entity.BankingOperation;
import net.fina.server.license.entity.LicenceType;
import net.fina.server.license.model.*;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class LicenseProxySession {
    @Inject
    private Logger log;

    @Inject
    private FiLocal fiLocal;
    @Inject
    private LicenseLocal licenseLocal;

    public List<LicenseTypeMetaModel> loadLicenseType() {
        long langId = ThreadLocalHolder.getLanguage().getId();

        return LicenseTypeMetaModelHelper.toModels(licenseLocal.loadLicenseTypes(), langId);
    }

    public LicenseTypeMetaModel save(LicenseTypeMetaModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        LicenceType saved = licenseLocal.save(LicenseTypeMetaModelHelper.toEntity(model, langId));
        return LicenseTypeMetaModelHelper.toModel(saved, langId);
    }

    public void deleteLicenceType(long licenseTypeId) throws FinATypeException {
        licenseLocal.deleteLicenceType(licenseTypeId);
    }


    public List<LicenseMetaModel> loadFiLicenses(long fiId) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return LicenseMetaModelHelper.toModels(fiLocal.loadFiLicences(fiId), langId);
    }

    public void deleteLicense(long licenseId) throws FinATypeException {
        licenseLocal.deleteLicenceType(licenseId);
    }

    public List<BankingOperationMetaModel> loadBankingOperations(long licenseTypeId, long parentId) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        return BankingOperationModelHelper.toModels(licenseLocal.loadByParentBankingOperation(licenseTypeId, parentId), langId);
    }

    public BankingOperationMetaModel saveBankingOperation(BankingOperationMetaModel operation) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        BankingOperation bankingOperation = null;
        if (operation.getId() > 0) {
            bankingOperation = licenseLocal.updateBankingOperation(BankingOperationModelHelper.toEntity(operation, langId));
        } else {
            bankingOperation = licenseLocal.createBankingOperation(BankingOperationModelHelper.toEntity(operation, langId));
        }
        return BankingOperationModelHelper.toModel(bankingOperation, langId);
    }


    public void deleteBankingOperation(long operationId) throws FinATypeException {
        licenseLocal.deleteBankingOperation(operationId);
    }

    public LicenseTypeMetaModel getLicenseTypeById(long typeId) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        LicenseTypeMetaModel model = LicenseTypeMetaModelHelper.toModel(licenseLocal.getLicenseTypeById(typeId), langId);
        List<BankingOperationMetaModel> firstLevelOperations = model.getOperations().stream().filter(op -> op.getParentId() == 0).collect(Collectors.toList());
        model.setOperations(firstLevelOperations);
        return model;
    }
}
