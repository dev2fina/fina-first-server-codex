package net.fina.server.fi.api;

import net.fina.server.fi.model.EntityHistoryModel;
import net.fina.server.fi.model.FiBranchMetaModel;
import net.fina.server.fi.model.FiManagementMetaModel;
import net.fina.server.fi.model.FiModelFull;
import net.fina.server.legalperson.model.BeneficiaryMetaModel;
import net.fina.server.license.model.LicenseMetaModel;
import net.fina.server.person.model.CriminalRecordMetaModel;

import java.util.List;

public interface FiHistoryLocal {

    List<EntityHistoryModel<FiModelFull>> loadFiHistory(long fiId, int offset, int limit);

    List<EntityHistoryModel<FiBranchMetaModel>> loadFiBranchHistory(long fiId, int offset, int limit);

    List<EntityHistoryModel<FiBranchMetaModel>> loadBranchHistory(long branchId, int offset, int limit);

    List<EntityHistoryModel<FiManagementMetaModel>> loadFiManagementHistory(long fiId, long managementId, int offset, int limit);

    List<EntityHistoryModel<BeneficiaryMetaModel>> loadFiBeneficiaryHistory(long beneficiaryId, int offset, int limit);

    long countFiHistory(long fiId);

    long countFiBranchesHistory(long fiId);

    long countBranchHistory(long branchId);


    long countManagementHistory(long fiId, long managementId);

    long countBeneficiaryHistory(long beneficiaryId);

    List<EntityHistoryModel<CriminalRecordMetaModel>> loadFiCriminalRecordsHistory(long fiId, int offset, int limit);

    long countFiCriminalRecordsHistory(long fiId);

    List<EntityHistoryModel<LicenseMetaModel>> loadFiLicenseHistory(long licenseId, int offset, int limit);

    long countFiLicenseHistory(long licenseId);

    List<EntityHistoryModel<CriminalRecordMetaModel>> loadCriminalRecordHistory(long criminalRecordId, int offset, int limit);

    long countCriminalRecordHistory(long criminalRecordId);
}
