package net.fina.server.fi.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.FiBranchFilter;
import net.fina.common.client.filter.FiFilter;
import net.fina.common.client.fis.FiBranchTypeCounterModel;
import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.server.fi.entity.*;
import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.entity.metainfo.BusinessEntityType;
import net.fina.server.legalperson.entity.metainfo.EconomicEntityType;
import net.fina.server.legalperson.entity.metainfo.EquityFormType;
import net.fina.server.legalperson.entity.metainfo.ManagementFormType;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.license.entity.Licence;
import net.fina.server.license.entity.LicenceHistory;
import net.fina.server.license.entity.LicenceType;
import net.fina.server.person.entity.CriminalRecord;
import net.fina.server.person.model.ShareMetaModel;
import net.fina.server.security.entity.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FiLocal {

    List<String> loadFiCodes();

    List<String> loadFiCodes(String userLogin);

    Map<String, Description> loadFiCodeAndNames();

    Map<String, Description> loadFiCodeAndNames(String userLogin);

    boolean canAmendFi(long fiId);

    Fi findFiByCode(String code);

    boolean fiExists(String code);

    Fi save(Fi fi, String langCode) throws FinATypeException;

    Fi saveECM(Fi fi, long langId) throws FinATypeException;

    void delete(long fiId) throws FinATypeException;

    List<Licence> loadFiLicences(long fiId);

    List<Licence> loadFiLicences(long fiId, int start, int limit, String filterValue);

    Collection<LicenceHistory> loadFiLicenceHistories(long fiId);

    Licence saveFiLicence(Licence licence) throws FinATypeException;

    Licence saveFiLicenceECM(Licence licence);

    void deleteFiLicence(long licenceId);

    LicenceType getLicenceTypeByCode(String code);

    Collection<PeerGroup> getFiPeerGroups(long fiId);

    Collection<PeerGroup> setFiPeerGroups(long fiId, List<PeerGroup> groups);

    List<Fi> loadFiByCodes(List<String> codes);

    List<Fi> load(Map<FiFilter, Object> fiFilterObjectMap, int offset, int limit, long langId);

    List<Fi> loadAllFis(Boolean excludeDisabled);

    List<Fi> loadPermittedFiTree(Boolean excludeDisabled);

    List<Fi> loadUserRoleFis(long entityId, boolean isUser, Boolean excludeDisabled);

    List<Fi> load(Map<FiFilter, Object> fiFilterObjectMap);

    long count(Map<FiFilter, Object> filter, long langId);

    // FI Branch
    List<FiBranch> loadFiBranchs(Long fiId);

    List<FiBranch> loadFiBranchs(Long fiId, int offset, int limit);

    List<FiBranch> loadFiBranchesByType(Long fiId, int offset, int limit, long fiBranchTypeId, Map<FiBranchFilter, Object> filter);

    List<FiBranch> loadFiBranchesByType(long fiBranchTypeId);

    FiBranch findFiBranchById(long branchId);

    FiBranch saveFiBranch(FiBranch fiBranch, long langId, boolean importMode) throws FinATypeException;

    void deleteFiBranch(long branchId) throws FinATypeException;

    FiBranch findDeletedFiBranchByBranchCode(long fiId, String fiBranchCode);

    void restoreDeletedFiBranch(long fiBranchId) throws FinATypeException;

    void deleteManagement(long id) throws FinATypeException;

    Management save(Management management) throws FinATypeException;

    List<Management> loadManagements();

    List<FiManagement> loadFiManagement(long fiId);

    List<FiManagement> loadFiManagementByType(long fiId, long managementTypeId, int start, int limit, String filterString);

    long countFiManagementByType(long fiId, long managementTypeId, String filterString);

    void deleteFiManagement(long managementId);

    FiManagement saveFiManagement(FiManagement entity, long langId) throws FinATypeException;

    FiManagement getFiManagementById(long fiManagementId);

    List<FiType> loadFiTypes();

    FiType save(FiType fiType) throws FinATypeException;

    void deleteFiType(long id) throws FinATypeException;

    Description getFiShortNameByCode(String code);

    boolean checkCodeUnique(Fi fi);

    boolean checkEmailUnique(Fi fi);

    boolean checkLicenseCodeUnique(Licence licence);

    boolean checkLicenseTypeCodeUnique(LicenceType licenceType);

    boolean checkCriterionCodeUnique(Criterion criterion);

    boolean checkPeerGroupsCodeUnique(PeerGroup peerGroup);

    boolean checkFiTypeCodeUnique(FiType fiType);

    boolean checkRegionCodeUnique(Region region);

    boolean checkManagementCodeUnique(Management management);

    List<FiType> loadFiTypesByIds(List<Long> ids);

    List<String> loadUserFiTypes();

    List<Long> loadUserFiTypeIds();

    void setGroupsForFiType(long currentFiTypeId, long changedFiTypeId, long criterionId, boolean isDefault) throws FinATypeException;

    List<User> loadFiUsers(long id);

    Map<Long, Long> getFisAndRegionIds();

    Map<String, Long> getNumberOfFisByType(int limit, int offset);

    Map<Long, Long> getNumberOfFisByRegion();

    List<Long> loadFiIdsByTypeId(long typeId);

    byte[] exportFis(List<Long> fiList);

    void importFis(byte[] file, String langCode) throws FinATypeException;

    List<User> loadFiTypeUsers(long fiTypeId);

    Map<Fi, String> loadFiRegionMap(Collection<Long> ids, long langId);

    List<Fi> loadFisByTypeId(long typeId);

    List<String> loadAllFiCodes();

    List<Fi> loadUserRoleFis(long userId);

    Management findManagementByCode(String code);

    List<Long> loadAllUsersInFis(List<Long> fiIds);

    String getFiTypeCodeByFiCode(String fiCode);

    long countBranches(long fiId);

    long countBranches(long fiId, long fiBranchTypeId, Map<FiBranchFilter, Object> filter);

    Fi getFiById(long fiId);

    List<FiBranchTypeCounterModel> loadFiBranchCountGroupByFiType(long fiId, long langId);

    void checkUserHasFiAccess(long fiId) throws FinATypeException;

    boolean checkIdentificationCodeUnique(String identificationCode, long id);

    Fi findByIdentificationCode(String identificationCode);

    List<Beneficiary> getFiShares(Map<PersonFilter, Object> filterMap, long fiId) throws FinATypeException;

    List<CriminalRecord> loadFiCriminalRecords(long fiId) throws FinATypeException;

    CriminalRecord createFiCriminalRecord(long fiId, CriminalRecord criminalRecord) throws FinATypeException;

    void updateFiCriminalRecord(long fiId, CriminalRecord criminalRecord) throws FinATypeException;

    void deleteFiCriminalRecord(long fiId, long recordId) throws FinATypeException;

    Licence loadFiLicense(long licenseId);

    long countFiLicenses(long fiId);

    Fi getFiByIdentificationCode(String identificationNumber);

    long createFiShare(long fiId, ShareMetaModel share) throws FinATypeException;

    void updateFiShare(long fiId, long beneficiaryId, long legalPersonId, double sharePercentage, Date shareDate) throws FinATypeException;

    void deleteFiShare(long companyId, long shareId);

    List<Fi> loadActiveFisByType(long fiTypeId, Boolean active);

    List<Fi> loadActiveFisByType(List<Long> fiTypeIds, Boolean active);

    FiBranch getFiBranchById(long id);

    List<BusinessEntityType> loadBusinessEntityTypes();

    Collection<EconomicEntityType> loadEconomicEntityTypes();

    Collection<EquityFormType> loadEquityFormTypes();

    Collection<ManagementFormType> loadManagementFormTypes();

    Map<Long, FiTypeSimpleModel> loadFiIDTypeMap();

    void removePersonConnectionByConnectionId(long id);

    Map<String, Long> loadFiTypeCodeIdMap();

    List<Fi> loadFisByIds(List<Long> ids);

    List<Fi> loadFisByPeerGroup(long groupId);

    Fi updateFiPermittedUsers(long fiId, List<Long> permittedUsers, List<String> responsibleUsers);

    void removeFiPermission(long fiId);
}