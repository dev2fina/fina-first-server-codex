package net.fina.server.fi.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.FiFilter;
import net.fina.common.client.fis.*;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.fi.FiPairModel;
import net.fina.common.shared.legalperson.metainfo.CodeDescriptionModel;
import net.fina.common.shared.user.UserModel;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiHistoryLocal;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.*;
import net.fina.server.fi.impl.RegionCacheManager;
import net.fina.server.fi.model.*;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.metainfo.BaseLegalEntityType;
import net.fina.server.legalperson.entity.metainfo.LegalEntityType;
import net.fina.server.legalperson.model.BeneficiaryMetaModel;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.legalperson.model.helper.LegalPersonMetaInfoModelHelper;
import net.fina.server.license.api.LicenseLocal;
import net.fina.server.license.entity.*;
import net.fina.server.license.model.*;
import net.fina.server.person.api.PersonLocal;
import net.fina.server.person.entity.CriminalRecord;
import net.fina.server.person.entity.Person;
import net.fina.server.person.model.CriminalRecordMetaModel;
import net.fina.server.person.model.ShareMetaModel;
import net.fina.server.person.model.helper.CriminalRecordModelHelper;
import net.fina.server.person.model.helper.ShareModelHelper;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.security.model.helper.UserModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.FINA_BANK_REVIEW)
public class FiProxySession {

    @Inject
    private FiLocal fiLocal;
    @Inject
    private FiHistoryLocal fiHistoryLocal;

    @Inject
    private LanguageLocal languageLocal;

    @Inject
    private UserLocal userLocal;
    @Inject
    private LicenseLocal licenseLocal;
    @Inject
    private PersonLocal personLocal;

    @Inject
    private RegionCacheManager regionCacheManager;


    public List<String> loadFiCodes() {
        return fiLocal.loadFiCodes();
    }

    public Collection<FiTypeSimpleModel> loadAllFisAndTypes(String locale) {
        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);

        Map<Long, FiTypeSimpleModel> fiTypeMap = new HashMap<>();

        Map<FiFilter, Object> filterObjectMap = new HashMap<>();
        filterObjectMap.put(FiFilter.USER_ID, userLocal.getCurrentUserId());

        List<Fi> userFis = fiLocal.load(filterObjectMap);
        for (Fi fi : userFis) {
            FiModelFull fiModel = FiModelFullHelper.toModel(fi, lang.getId());

            if (fiTypeMap.containsKey(fi.getFiType().getId())) {
                fiTypeMap.get(fi.getFiType().getId()).getFis().add(fiModel);
            } else {
                FiTypeSimpleModel fiTypeSimpleModel = new FiTypeSimpleModel(fi.getFiType().getId(), fi.getFiType().getCode(), fi.getFiType().getDescription().getDescription(lang.getId()));
                List<FiModel> fis = new ArrayList<>();
                fis.add(fiModel);
                fiTypeSimpleModel.setFis(fis);
                fiTypeMap.put(fi.getFiType().getId(), fiTypeSimpleModel);
            }
        }

        return fiTypeMap.values();
    }

    public List<LicenseMetaModel> loadLicenceHistory(long id) {
        Collection<LicenceHistory> licenceHistories = fiLocal.loadFiLicenceHistories(id);
        List<LicenseMetaModel> models = new ArrayList<>();

        for (LicenceHistory lh : licenceHistories) {
            LicenseMetaModel model = new LicenseMetaModel();
            model.setId(lh.getId());
            model.setCode(lh.getLicence().getCode());

            LicenseTypeMetaModel typeModel = new LicenseTypeMetaModel();
            typeModel.setCode(lh.getLicence().getLicenseType().getCode());
            model.setLicenseType(typeModel);
            model.setDateOfChange(lh.getChangeDate());
            model.setLicenceStatus(lh.getLicenceStatus());
            model.setReason(lh.getChange());

            models.add(model);
        }
        return models;
    }

    public List<UserAndRoleMetaModel> loadFiUsers(long id) {
        return usersToModels(fiLocal.loadFiUsers(id));
    }

    public String generateExportUrl(List<FiModel> modelList) {
        StringBuilder sb = new StringBuilder();
        for (FiModel model : modelList) {
            if (model.getLevel() == 1) {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(model.getId());
            } else {
                for (long id : fiLocal.loadFiIdsByTypeId(model.getId())) {
                    if (sb.length() != 0) {
                        sb.append(",");
                    }
                    sb.append(id);
                }
            }
        }
        return "ids=" + sb.toString();
    }

    private List<UserAndRoleMetaModel> usersToModels(Collection<User> users) {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        List<UserAndRoleMetaModel> models = new ArrayList<>();
        for (User user : users) {
            UserAndRoleMetaModel model = new UserAndRoleMetaModel();
            model.setId(user.getId());
            model.setLogin(user.getLogin());
            model.setName(user.getDescription().getDescription(lang.getId()));
            models.add(model);
        }

        return models;
    }

    public List<FiPairModel> loadFiTypeMap(Boolean excludeDisabledFis) {
        Map<FiFilter, Object> filterObjectMap = new HashMap<>();
        filterObjectMap.put(FiFilter.USER_ID, userLocal.getCurrentUserId());
        filterObjectMap.put(FiFilter.EXCLUDE_DISABLED, excludeDisabledFis != null ? excludeDisabledFis : true);
        List<FiModel> userFiModels = fisToModels(fiLocal.load(filterObjectMap));

        return getFiPair(userFiModels, null, false);
    }

    public List<SimpleFiPairModel> loadSimpleFiTypeMap(Boolean excludeDisabledFis) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<Fi> allFis = fiLocal.loadPermittedFiTree(excludeDisabledFis);
        return groupedFiTypeMap(allFis, langId);
    }


    public List<FiPairModel> loadFiTypeMap(long id, boolean isUser, Boolean excludeDisabledFis) {
        Map<FiFilter, Object> filterLoadAll = new HashMap<>();
        filterLoadAll.put(FiFilter.LOAD_All, Boolean.TRUE);
        filterLoadAll.put(FiFilter.EXCLUDE_DISABLED, excludeDisabledFis != null ? excludeDisabledFis : true);
        List<FiModel> allFiModels = fisToModels(fiLocal.load(filterLoadAll));
        List<FiModel> userRoleFiModels = new ArrayList<>();

        Map<FiFilter, Object> filterObjectMap = new HashMap<>();
        if (isUser) {
            filterObjectMap.put(FiFilter.USER_ID, id);
            userRoleFiModels = fisToModels(fiLocal.loadUserRoleFis(id));
        } else {
            filterObjectMap.put(FiFilter.ROLE_ID, id);
        }

        filterObjectMap.put(FiFilter.LOAD_All, Boolean.TRUE);
        Collection<FiModel> userFiModels = fisToModels(fiLocal.load(filterObjectMap));

        Set<FiModel> userFiModelsResult = userRoleFiModels.stream().peek(fm -> fm.setRoleFi(true)).collect(Collectors.toCollection(LinkedHashSet::new));
        userFiModelsResult.addAll(userFiModels);

        return getFiPair(allFiModels, new ArrayList<>(userFiModelsResult), true);

    }

    private List<FiPairModel> getFiPair(List<FiModel> allFiModels, List<FiModel> userFiModels, boolean userManagerComponent) {
        List<FiPairModel> result = new ArrayList<>();
        List<FiModel> parents = loadFiRoots();
        for (FiModel parent : parents) {
            List<FiModel> children = new ArrayList<>();
            boolean markRoot = true;
            for (FiModel fiModel : allFiModels) {
                if (fiModel.getFiTypeModel().getId() == parent.getId()) {
                    if (userManagerComponent) {
                        if (userFiModels.contains(fiModel)) {
                            fiModel.setHasFiModel(true);
                            fiModel.setRoleFi(userFiModels.get(userFiModels.indexOf(fiModel)).isRoleFi());
                            parent.setHalfChecked(true);
                        } else {
                            markRoot = false;
                        }
                    }
                    children.add(fiModel);
                }
            }
            if (userManagerComponent && children.size() > 0) {
                parent.setHasFiModel(markRoot);
                if (markRoot) {
                    parent.setHalfChecked(false);
                }
            }

            result.add(new FiPairModel(parent, children));
        }
        return result;
    }

    private List<FiModel> fisToModels(List<Fi> fis) {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        List<FiModel> result = new ArrayList<>();
        for (Fi fi : fis) {
            result.add(fiToModel(fi, lang.getId()));
        }
        return result;
    }

    public List<FiTypeModel> loadFiTypes(boolean calculateActiveFiStatistics) {
        List<FiTypeModel> models = new ArrayList<>();
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        for (FiType fiType : fiLocal.loadFiTypes()) {
            FiTypeModel type = FiTypeModelHelper.toModel(fiType, lang.getId());
            models.add(type);
            if (calculateActiveFiStatistics) {
                type.setActiveFisCount(fiLocal.loadActiveFisByType(type.getId(), true).size());
                type.setInactiveFisCount(fiLocal.loadActiveFisByType(type.getId(), false).size());
            }
        }
        models.sort(Comparator.comparing(o -> o.getCode().toLowerCase()));
        return models;
    }

    public FiTypeModel saveFiType(FiTypeModel model) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        return FiTypeModelHelper.toModel(fiLocal.save(FiTypeModelHelper.toEntity(model, lang.getId())), lang.getId());
    }

    public void deleteFiType(long id) throws FinATypeException {
        fiLocal.deleteFiType(id);
    }

    public void deleteManagement(long id) throws FinATypeException {
        fiLocal.deleteManagement(id);
    }

    public List<ManagementMetaModel> loadManagements(String locale) throws IOException {
        List<ManagementMetaModel> models = new ArrayList<>();
        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);

        for (Management management : fiLocal.loadManagements()) {
            models.add(ManagementMetaModelHelper.toModel(management, lang.getId()));
        }

        return models;
    }

    public ManagementMetaModel saveManagement(ManagementMetaModel model, String locale) throws FinATypeException, IOException {
        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
        return ManagementMetaModelHelper.toModel(fiLocal.save(ManagementMetaModelHelper.toEntity(model, lang.getId())), lang.getId());
    }

    public List<FiModel> loadFiRoots() {
        List<FiModel> fiModels = new ArrayList<>();
        FiModel fiModel;
        List<FiTypeModel> fiTypes = loadFiTypes(false);
        for (FiTypeModel fiTypeModel : fiTypes) {
            fiModel = new FiModel();
            fiModel.setLevel(0);

            fiModel.setId(fiTypeModel.getId());
            fiModel.setCode(fiTypeModel.getCode());
            fiModel.setName(fiTypeModel.getName());
            fiModel.setNameStrId(fiTypeModel.getNameStrId());

            fiModel.setFiTypeModel(fiTypeModel);
            fiModels.add(fiModel);
        }
        return fiModels;
    }


    public PaginatedListWrapper<FiModel> loadFis(Map<FiFilter, Object> filterStringMap, int page, int limit, String searchQuery, boolean userManagerComponent, boolean excludeDisabledFis) {
        int offset = PagingUtil.getOffsetFromPage(page, limit);

        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        if (filterStringMap == null) {
            filterStringMap = new HashMap<>();
        }

        if (!userManagerComponent) {
            filterStringMap.put(FiFilter.USER_ID, userLocal.getCurrentUserId());
        }

        if (excludeDisabledFis) {
            filterStringMap.put(FiFilter.EXCLUDE_DISABLED, true);
        }

        List<FiModel> models = new ArrayList<>();
        for (Fi fi : fiLocal.load(filterStringMap, offset, limit, lang.getId())) {
            FiModel fiModel = fiToModel(fi, lang.getId());
            if (fiModel.getRegionId() != null && fiModel.getRegionId() > 0) {
                fiModel.setRegion(regionCacheManager.getRegion(fiModel.getRegionId(), lang.getId()));
            }
            if (searchQuery == null || filterFiModel(fiModel, searchQuery.toLowerCase())) {
                models.add(fiModel);
            }
        }

        PaginatedListWrapper<FiModel> result = new PaginatedListWrapper<>();
        result.setList(models);
        result.setTotalResults(fiLocal.count(filterStringMap, lang.getId()));
        result.setCurrentPage(page);

        return result;
    }

    private FiModelFull fiToModel(Fi fi, long langId) {
        FiModelFull model = FiModelFullHelper.toModel(fi, langId);

        model.setLevel(1);

        model.setName(fi.getDescription().getDescription(langId));
        model.setNameStrId(fi.getDescription().getNameStrId());

        model.setShortNameString(fi.getShortName().getDescription(langId));
        model.setShortNameStrId(fi.getShortName().getNameStrId());

        model.setAddressString(fi.getAddressDescription() != null ? fi.getAddressDescription().getDescription(langId) : "");
        model.setAddressStrId(fi.getAddressDescription() != null ? fi.getAddressDescription().getNameStrId() : 0);
        model.setContactPerson(fi.getContactPerson());
        model.setCreatedAt(fi.getCreatedAt());
        model.setModifiedAt(fi.getModifiedAt());
        model.setWebSite(fi.getWebSite());
        model.setIdentificationCode(fi.getIdentificationCode());

        if (fi.getLicences() != null) {
            Optional<Licence> licence = fi.getLicences().stream().filter(Licence::getIsDefault).findAny();

            licence.ifPresent(value -> model.setLicenseCode(value.getCode()));
        }

        model.setDisable(fi.isDisable());

        // Type
        FiTypeModel fiTypeModel = new FiTypeModel();
        fiTypeModel.setId(fi.getFiType().getId());
        fiTypeModel.setName(fi.getFiType().getDescription().getDescription(langId));
        fiTypeModel.setNameStrId(fi.getFiType().getDescription().getNameStrId());
        fiTypeModel.setCode(fi.getFiType().getCode());
        fiTypeModel.setVersion(fi.getFiType().getVersion());

        model.setFiTypeModel(fiTypeModel);

        // Fi Groups

        model.setFiGroupModels(FiGroupModelHelper.toModelsFromPeerGroups(fi.getPeerGroup(), langId));
        model.setRegistrationDate(fi.getRegistrationDate());
        model.setCloseDate(fi.getCloseDate());
        model.setNumberOfMobileOffices(fi.getNumberOfMobileOffices());
        model.setNumberOfEmploys(fi.getNumberOfEmploys());
        if (fi.getRegionId() != null) {
            model.setRegionId(fi.getRegionId());
        }

        if (fi.getFiAdditionalInfo() != null) {
            model.setAdditionalInfo(LegalPersonMetaInfoModelHelper.toModel(fi.getFiAdditionalInfo()));
        }

        model.setDecreeNumber(fi.getDecreeNumber());
        model.setInspectionEndDate(fi.getInspectionEndDate());

        return model;
    }

    public List<FiGroupModel> loadFiGroups(long fiId) {

        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        List<FiGroupModel> models = new ArrayList<>();

        for (PeerGroup peerGroup : fiLocal.getFiPeerGroups(fiId)) {
            FiGroupModel model = FiGroupModelHelper.toModel(peerGroup, lang.getId());
            model.setType(FiGroupModel.Type.CHILD);

            models.add(model);
        }
        return models;
    }

    public PaginatedListWrapper<LicenseMetaModel> loadFiLicenses(long fiId, int page, int limit, String filterValue) {
        int offset = PagingUtil.getOffsetFromPage(page, limit);

        PaginatedListWrapper<LicenseMetaModel> result = new PaginatedListWrapper<>();

        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        result.setList(LicenseMetaModelHelper.toModels(fiLocal.loadFiLicences(fiId, offset, limit, filterValue), lang.getId()));
        result.setTotalResults(fiLocal.countFiLicenses(fiId));
        return result;
    }

    public PaginatedListWrapper<FiManagementMetaModel> loadFiManagements(long fiId, long managementTypeId, int page, int limit, String locale, String filterString) {
        int offset = PagingUtil.getOffsetFromPage(page, limit);

        List<FiManagementMetaModel> models = new ArrayList<>();
        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);

        for (FiManagement fiManagement : fiLocal.loadFiManagementByType(fiId, managementTypeId, offset, limit, filterString)) {
            models.add(FiManagementMetaModelHelper.toModel(fiManagement, lang.getId()));
        }

        return new PaginatedListWrapper<>(models, limit, fiLocal.countFiManagementByType(fiId, managementTypeId, filterString));
    }

    public FiManagementMetaModel getFiManagementById(long fiManagementId, String locale) {
        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
        return FiManagementMetaModelHelper.toModel(fiLocal.getFiManagementById(fiManagementId), lang.getId());
    }

    public LicenseMetaModel saveLicense(LicenseMetaModel licenseModel) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        Licence license = LicenseMetaModelHelper.toEntity(licenseModel, lang.getId());
        license.setChange(licenseModel.getChange());
        license.setCreationDate(licenseModel.getCreationDate());
        license.setDateOfChange(license.getDateOfChange());
        license.setReasons(new Description(lang.getId(), licenseModel.getReasonStrId(), licenseModel.getReason()));

        // fi
        Fi fi = fiLocal.findFiByCode(licenseModel.getFiModel().getCode());
        license.setFi(fi);

        // license type
        license.setLicenseType(LicenseTypeMetaModelHelper.toEntity(licenseModel.getLicenseType(), lang.getId()));

        return LicenseMetaModelHelper.toModel(fiLocal.saveFiLicence(license), lang.getId());
    }

    public void deleteLicense(long id) {
        fiLocal.deleteFiLicence(id);
    }


    public void deleteFiManagement(long id) {
        fiLocal.deleteFiManagement(id);
    }

    public FiManagementMetaModel saveFiManagement(FiManagementMetaModel fiManagementModel) throws JsonProcessingException, FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();

        Fi fi = fiLocal.getFiById(fiManagementModel.getFiId());

        FiPerson fiPerson = null;

        if (fiManagementModel.getPerson() != null) {
            Person person = personLocal.getPersonInfo(fiManagementModel.getPerson().getId());
            fiPerson = personLocal.getFiPerson(fi.getId(), person.getId());
            if (fiPerson == null) {
                fiPerson = new FiPerson(0, fi, person);
            }
        }

        FiManagement fiManagement = FiManagementMetaModelHelper.toEntity(fiManagementModel, fiPerson, langId);
        if (fiManagementModel.getManagementModel() != null) {
            fiManagement.setManagement(ManagementMetaModelHelper.toEntity(fiManagementModel.getManagementModel(), langId));
        }
        return FiManagementMetaModelHelper.toModel(fiLocal.saveFiManagement(fiManagement, langId), langId);
    }

    @RolesAllowed(PermissionIdNames.FINA_BANK_AMEND)
    public void setFiGroups(long fiId, List<FiGroupModel> groups) {

        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        List<PeerGroup> peerGroups = new ArrayList<>();
        PeerGroup peerGroup;
        for (FiGroupModel fiGroupModel : groups) {
            peerGroup = FiGroupModelHelper.toPeerGroupEntity(fiGroupModel, lang.getId());
            peerGroup.setDescription(new Description(lang.getId(), fiGroupModel.getNameStrId(), fiGroupModel.getName()));
            peerGroups.add(peerGroup);
        }
        fiLocal.setFiPeerGroups(fiId, peerGroups);
    }

    @RolesAllowed(PermissionIdNames.FINA_BANK_DELETE)
    public void deleteFi(long id) throws FinATypeException {
        fiLocal.delete(id);
    }

    @RolesAllowed(PermissionIdNames.FINA_BANK_AMEND)
    public FiModelFull saveFi(FiModelFull fiModel, boolean removeFiPermissions) throws FinATypeException {
        if (fiModel.getIdentificationCode() == null || fiModel.getIdentificationCode().trim().isEmpty()) {
            throw new FinATypeException("Identification Code Is Required");
        }
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        long langId = lang.getId();
        if (!fiLocal.checkIdentificationCodeUnique(fiModel.getIdentificationCode(), fiModel.getId())) {
            throw new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.exception.IdCodeNotUnique", lang.getCode()), fiModel.getIdentificationCode()));
        }


        Fi fi = FiModelFullHelper.toEntity(fiModel, langId);

        fi.setDescription(new Description(langId, fiModel.getNameStrId(), fiModel.getName()));
        fi.setShortName(new Description(langId, fiModel.getShortNameStrId(), fiModel.getShortNameString()));

        fi.setAddressDescription(new Description(langId, fiModel.getAddressStrId(), fiModel.getAddressString()));

        // Type
        FiType fiType = FiTypeModelHelper.toEntity(fiModel.getFiTypeModel());
        fiType.setDescription(new Description(langId, fiModel.getFiTypeModel().getNameStrId(), fiModel.getFiTypeModel().getName()));
        fi.setFiType(fiType);

        // Groups
        Collection<PeerGroup> peerGroups = new ArrayList<>();
        PeerGroup peerGroup;
        List<FiGroupModel> fiGroupModels = fiModel.getFiGroupModels() != null ? fiModel.getFiGroupModels() : new ArrayList<>();
        for (FiGroupModel fiGroupModel : fiGroupModels) {
            peerGroup = FiGroupModelHelper.toPeerGroupEntity(fiGroupModel, langId);
            peerGroup.setDescription(new Description(langId, fiGroupModel.getNameStrId(), fiGroupModel.getName()));
            peerGroups.add(peerGroup);
        }

        fi.setPeerGroup(peerGroups);

        List<Long> permittedUsers = fiModel.getPermittedUserIds();

        if (fiModel.getUserLoginModels() != null && !fiModel.getUserLoginModels().isEmpty()) {

            List<User> fiUsers = userLocal.loadUsersByLogins(fiModel.getUserLoginModels());

            if (fiModel.getUserLoginModels() != null) {
                fi.setUsers(fiUsers);
            } else {
                fi.setUsers(null);
            }

            for (User user : fiUsers) {
                if (!permittedUsers.contains(user.getId())) {
                    permittedUsers.add(user.getId());
                }
            }
        }

        FiModelFull savedModel = FiModelFullHelper.toModel(fiLocal.save(fi, lang.getCode()), langId);
        if (removeFiPermissions) {
            fiLocal.removeFiPermission(fi.getId());
        }

        //update region
        if (savedModel.getRegionId() != null && savedModel.getRegionId() > 0) {
            savedModel.setRegion(regionCacheManager.getRegion(savedModel.getRegionId(), langId));
        }

        // save user permissions for this bank
        if (!removeFiPermissions && permittedUsers != null && !permittedUsers.isEmpty()) {
            List<UserFi> userFis = new ArrayList<>();
            for (long permittedUserId : permittedUsers) {
                UserFi userFi = new UserFi();
                userFi.setBankId(savedModel.getId());
                userFi.setUserId(permittedUserId);
                userFis.add(userFi);
            }
            userLocal.saveUserFiPermissions(fiModel.getId(), userFis);
        }


        return savedModel;
    }

    public List<LicenseTypeMetaModel> loadLicenseTypes() {
        List<LicenseTypeMetaModel> models = new ArrayList<>();
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        for (LicenceType licenseType : licenseLocal.loadLicenseTypes()) {
            models.add(LicenseTypeMetaModelHelper.toModel(licenseType, lang.getId()));
        }
        return models;
    }

    public LicenseTypeMetaModel saveLicenseType(LicenseTypeMetaModel model) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        return LicenseTypeMetaModelHelper.toModel(licenseLocal.save(LicenseTypeMetaModelHelper.toEntity(model, lang.getId())), lang.getId());
    }

    public void deleteLicenseType(long id) throws FinATypeException {
        licenseLocal.deleteLicenceType(id);
    }

    public PaginatedListWrapper<FiModel> findFiItems(String text, boolean userManagerComponent, boolean excludeDisabledFis, int limit, int offset) {

        List<FiModel> result = new ArrayList<>();

        int total = 0;

        if (text != null && !text.trim().isEmpty()) {

            text = text.toLowerCase();

            List<FiModel> temp = loadFis(null, -1, -1, text, userManagerComponent, excludeDisabledFis).getList();

            total = temp.size();
            int totalLimit = total;

            if (limit > 0) {
                totalLimit = Math.min(offset + limit, totalLimit);
            }

            if (offset < temp.size()) {
                result.addAll(temp.subList(offset, totalLimit));
            }
        }

        return new PaginatedListWrapper<>(result, limit, total);
    }

    private boolean filterFiModel(FiModel fiModel, String text) {
        return nullSafeToLowerCaseContains(fiModel.getCode(), text) || nullSafeToLowerCaseContains(fiModel.getName(), text) || nullSafeToLowerCaseContains(fiModel.getEmail(), text) || nullSafeToLowerCaseContains(fiModel.getIdentificationCode(), text);
    }

    private boolean nullSafeToLowerCaseContains(String source, String text) {
        return source != null && text != null && source.toLowerCase().contains(text);
    }

    @RolesAllowed(PermissionIdNames.FINA_BANK_DELETE)
    public void deleteFis(List<Long> fiIds) throws FinATypeException {
        for (Long fiId : fiIds) {
            deleteFi(fiId);
        }
    }

    public FiModel getFiById(long fiId) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        Fi fi = fiLocal.getFiById(fiId);
        if (fi == null) throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Fi not found");
        FiModel model = fiToModel(fi, lang.getId());

        List<FiBranchTypeCounterModel> branchTypeCounterModels = fiLocal.loadFiBranchCountGroupByFiType(fiId, lang.getId());
        model.setBranchTypeCounterList(branchTypeCounterModels);

        return model;
    }

    public List<FiShareMetaModel> loadFiShares(Map<PersonFilter, Object> filterMap, long fiId) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<FiShareMetaModel> result = new ArrayList<>();
        List<ShareMetaModel> otherShares = ShareModelHelper.beneficiaryToShares(fiLocal.getFiShares(filterMap, fiId), langId);

        Map<LegalPersonMetaModel, List<ShareMetaModel>> map = new HashMap<>();

        otherShares.forEach(share -> {
            if (map.containsKey(share.getCompany())) {
                map.get(share.getCompany()).add(share);
            } else {
                map.put(share.getCompany(), new ArrayList<>());
                map.get(share.getCompany()).add(share);
            }

        });

        long counter = -1;
        for (Map.Entry<LegalPersonMetaModel, List<ShareMetaModel>> entry : map.entrySet()) {
            FiShareMetaModel fiShare = new FiShareMetaModel();
            fiShare.setCompany(entry.getKey());

            ShareMetaModel share = entry.getValue().get(0);
            fiShare.setShareDate(share.getShareDate());

            if (entry.getValue().size() > 1) {
                fiShare.setShares(entry.getValue());
                fiShare.setShare(entry.getValue().stream().mapToDouble(ShareMetaModel::getSharePercentage).sum());
                fiShare.setId(counter--);
            } else {
                fiShare.setId(share.getId());
                fiShare.setShare(share.getSharePercentage());
            }

            result.add(fiShare);
        }

        return result;
    }

    public ShareMetaModel createFiShare(long fiId, ShareMetaModel shareModel) throws FinATypeException {
        if (shareModel.getCompany() == null) {
            throw new FinATypeException("Company Not Presented!");
        }
        fiLocal.checkUserHasFiAccess(fiId);
        long beneficiaryId = fiLocal.createFiShare(fiId, shareModel);
        shareModel.setId(beneficiaryId);
        return shareModel;
    }

    public ShareMetaModel updateFiShare(long fiId, ShareMetaModel shareModel) throws FinATypeException {
        fiLocal.updateFiShare(fiId, shareModel.getId(), shareModel.getCompany().getId(), shareModel.getSharePercentage(), shareModel.getShareDate());
        return shareModel;
    }

    public void deleteFiShare(long companyId, long shareId) throws FinATypeException {
        fiLocal.deleteFiShare(companyId, shareId);
    }

    public List<CriminalRecordMetaModel> loadFiCriminalRecords(long fiId) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        List<CriminalRecord> criminalRecords = fiLocal.loadFiCriminalRecords(fiId);

        return CriminalRecordModelHelper.toModels(criminalRecords, lang.getId());
    }

    public CriminalRecordMetaModel createFiCriminalRecord(long fiId, CriminalRecordMetaModel model) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        CriminalRecord record = fiLocal.createFiCriminalRecord(fiId, CriminalRecordModelHelper.toEntity(model, lang.getId()));
        return CriminalRecordModelHelper.toModel(record, lang.getId());
    }

    public CriminalRecordMetaModel updateFiCriminalRecord(long fiId, CriminalRecordMetaModel model) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        fiLocal.updateFiCriminalRecord(fiId, CriminalRecordModelHelper.toEntity(model, lang.getId()));

        return model;
    }

    public void deleteFiCriminalRecord(long fiId, long recordId) throws FinATypeException {
        fiLocal.deleteFiCriminalRecord(fiId, recordId);
    }

    public LicenseMetaModel loadFiLicense(long fiId, long licenseId) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        Licence licence = fiLocal.loadFiLicense(licenseId);
        LicenseMetaModel model = LicenseMetaModelHelper.toModel(licence, lang.getId());

        List<BankingOperationMetaModel> bankingOperations = BankingOperationModelHelper.toModels(licenseLocal.loadBankingOperation(licence.getLicenseType().getId()), lang.getId());

        List<BankingOperationMetaModel> list = bankingOperations.stream().filter(bo -> bo.getParentId() == 0).collect(Collectors.toList());

        list.forEach(bo -> {
            bo.setChildren(BankingOperationModelHelper.toModels(licenseLocal.loadByParentBankingOperation(licence.getLicenseType().getId(), bo.getId()), lang.getId()));
        });


        model.setAllBankingOperations(bankingOperations);

        return model;
    }

    public LicenseCommentMetaModel saveLicenseComment(long licenseId, LicenseCommentMetaModel comment) {
        LicenseComment licenseComment = licenseLocal.saveLicenseComment(licenseId, LicenseCommentModelHelper.toEntity(comment));

        return LicenseCommentModelHelper.toModel(licenseComment);

    }

    public void deleteLicenseComment(long commentId) {
        licenseLocal.deleteLicenseComment(commentId);
    }

    public BankingOperationCommentMetaModel saveBankingOperationComment(long bankingOperationId, BankingOperationCommentMetaModel comment) {
        BankingOperationComment operationComment = licenseLocal.saveBankingOperationComment(bankingOperationId, BankingOperationCommentModelHelper.toEntity(comment));
        return BankingOperationCommentModelHelper.toModel(operationComment);
    }

    public void deleteBankingOperationComment(long commentId) {
        licenseLocal.deleteBankingOperationComment(commentId);
    }


    public List<CodeDescriptionModel> loadLegalEntityType(LegalEntityType entityType) {
        Collection<? extends BaseLegalEntityType> result = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();
        switch (entityType) {
            case BUSINESS_ENTITY_TYPE -> result = fiLocal.loadBusinessEntityTypes();
            case ECONOMIC_ENTITY_TYPE -> result = fiLocal.loadEconomicEntityTypes();
            case EQUITY_FORM_TYPE -> result = fiLocal.loadEquityFormTypes();
            case MANAGEMENT_FORM_TYPE -> result = fiLocal.loadManagementFormTypes();
        }
        return result.stream().map((entity -> new CodeDescriptionModel(entity.getId(), entity.getCode(), entity.getDescription().getDescription(langId)))).toList();
    }

    public List<UserModel> loadFiPermittedUsers(long fiId) {
        return UserModelHelper.toModels(userLocal.loadFiUsers(fiId), ThreadLocalHolder.getLanguage().getId());
    }


    @RolesAllowed(PermissionIdNames.FINA_BANK_REVIEW)
    public PaginatedListWrapper<FiModel> loadFis(Map<FiFilter, Object> filter, int offset, int limit) {
        filter.put(FiFilter.USER_ID, userLocal.getCurrentUserId());

        long langId = ThreadLocalHolder.getLanguage().getId();

        List<FiModel> models = new ArrayList<>();
        for (Fi fi : fiLocal.load(filter, offset, limit, langId)) {
            FiModel fiModel = fiToModel(fi, langId);
            if (fiModel.getRegionId() != null && fiModel.getRegionId() > 0) {
                fiModel.setRegion(regionCacheManager.getRegion(fiModel.getRegionId(), langId));
            }
            models.add(fiModel);
        }

        PaginatedListWrapper<FiModel> result = new PaginatedListWrapper<>();
        result.setList(models);
        result.setTotalResults(fiLocal.count(filter, langId));

        return result;
    }

    public Map<String, Integer> countFilteredFis(Map<FiFilter, Object> filter) {
        filter.put(FiFilter.USER_ID, userLocal.getCurrentUserId());
        long langId = ThreadLocalHolder.getLanguage().getId();
        Map<String, Integer> result = new HashMap<>();

        for (Fi fi : fiLocal.load(filter, -1, -1, langId)) {
            String typeCode = fi.getFiType().getCode();
            if (result.containsKey(typeCode)) {
                result.put(typeCode, result.get(typeCode) + 1);
            } else {
                result.put(fi.getFiType().getCode(), 1);
            }

        }
        return result;
    }

    public PaginatedListWrapper<EntityHistoryModel<FiModelFull>> loadFiHistory(long fiId, int offset, int limit) {
        List<EntityHistoryModel<FiModelFull>> result = fiHistoryLocal.loadFiHistory(fiId, offset, limit);
        PaginatedListWrapper<EntityHistoryModel<FiModelFull>> paging = new PaginatedListWrapper<>();
        paging.setList(result);
        paging.setPageSize(limit);
        paging.setTotalResults(fiHistoryLocal.countFiHistory(fiId));

        return paging;
    }

    public PaginatedListWrapper<EntityHistoryModel<FiManagementMetaModel>> loadFiManagementHistory(long fiId, long managementId, int offset, int limit) {
        List<EntityHistoryModel<FiManagementMetaModel>> result = fiHistoryLocal.loadFiManagementHistory(fiId, managementId, offset, limit);
        PaginatedListWrapper<EntityHistoryModel<FiManagementMetaModel>> paging = new PaginatedListWrapper<>();
        paging.setList(result);
        paging.setPageSize(limit);
        paging.setTotalResults(fiHistoryLocal.countManagementHistory(fiId, managementId));

        return paging;
    }

    public PaginatedListWrapper<EntityHistoryModel<BeneficiaryMetaModel>> loadFiBeneficiaryHistory(long beneficiaryId, int offset, int limit) {
        List<EntityHistoryModel<BeneficiaryMetaModel>> result = fiHistoryLocal.loadFiBeneficiaryHistory(beneficiaryId, offset, limit);
        PaginatedListWrapper<EntityHistoryModel<BeneficiaryMetaModel>> paging = new PaginatedListWrapper<>();
        paging.setList(result);
        paging.setPageSize(limit);
        paging.setTotalResults(fiHistoryLocal.countBeneficiaryHistory(beneficiaryId));

        return paging;
    }

    public PaginatedListWrapper<EntityHistoryModel<CriminalRecordMetaModel>> loadFiCriminalRecordsHistory(long fiId, int offset, int limit) {
        List<EntityHistoryModel<CriminalRecordMetaModel>> result = fiHistoryLocal.loadFiCriminalRecordsHistory(fiId, offset, limit);
        PaginatedListWrapper<EntityHistoryModel<CriminalRecordMetaModel>> paging = new PaginatedListWrapper<>();
        paging.setList(result);
        paging.setPageSize(limit);
        paging.setTotalResults(fiHistoryLocal.countFiCriminalRecordsHistory(fiId));


        return paging;
    }

    public PaginatedListWrapper<EntityHistoryModel<CriminalRecordMetaModel>> loadCriminalRecordHistory(long criminalRecordId, int offset, int limit) {
        List<EntityHistoryModel<CriminalRecordMetaModel>> result = fiHistoryLocal.loadCriminalRecordHistory(criminalRecordId, offset, limit);
        PaginatedListWrapper<EntityHistoryModel<CriminalRecordMetaModel>> paging = new PaginatedListWrapper<>();
        paging.setList(result);
        paging.setPageSize(limit);
        paging.setTotalResults(fiHistoryLocal.countCriminalRecordHistory(criminalRecordId));


        return paging;
    }

    public PaginatedListWrapper<EntityHistoryModel<LicenseMetaModel>> loadLicenseHistory(long licenseId, int offset, int limit) {
        List<EntityHistoryModel<LicenseMetaModel>> result = fiHistoryLocal.loadFiLicenseHistory(licenseId, offset, limit);
        PaginatedListWrapper<EntityHistoryModel<LicenseMetaModel>> paging = new PaginatedListWrapper<>();
        paging.setList(result);
        paging.setPageSize(limit);
        paging.setTotalResults(fiHistoryLocal.countFiLicenseHistory(licenseId));


        return paging;
    }

    public byte[] exportFis(long fiTypeId, List<Long> fiIds) {
        if (fiTypeId > 0 && fiIds.isEmpty()) {
            fiIds = fiLocal.loadFiIdsByTypeId(fiTypeId);
        }
        return fiLocal.exportFis(new ArrayList<>(fiIds));
    }

    @Asynchronous
    public void importFis(byte[] content, String langCode) throws FinATypeException {
        fiLocal.importFis(content, langCode);
    }

    private List<SimpleFiPairModel> groupedFiTypeMap(List<Fi> fis, long langId) {
        Map<FiType, List<Fi>> fiTypeMap = new HashMap<>();

        for (Fi fi : fis) {
            FiType fiType = fi.getFiType();
            fiTypeMap.computeIfAbsent(fiType, k -> new ArrayList<>()).add(fi);
        }

        List<SimpleFiPairModel> fiPairModels = new ArrayList<>();
        List<FiType> fiTypes = fiLocal.loadFiTypes();

        fiTypes.stream()
                .filter(fiType -> !fiTypeMap.containsKey(fiType))
                .forEach(fiType -> fiTypeMap.put(fiType, new ArrayList<>()));


        for (Map.Entry<FiType, List<Fi>> entry : fiTypeMap.entrySet()) {
            List<FiSimpleModel> simpleModels = entry.getValue().stream().map(e -> new FiSimpleModel(e, langId)).toList();
            FiType fiType = entry.getKey();
            FiSimpleModel fiTypeModel = new FiSimpleModel(fiType, langId);
            SimpleFiPairModel fiPairModel = new SimpleFiPairModel(fiTypeModel, simpleModels);
            fiPairModels.add(fiPairModel);
        }

        fiPairModels.sort(Comparator.comparing(o -> o.getParent().getCode()));

        return fiPairModels;
    }


    @RolesAllowed(PermissionIdNames.FINA_BANK_AMEND)
    public FiModelFull updateFiPermittedUsers(FiModelFull fiModel) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        Fi fi = fiLocal.updateFiPermittedUsers(fiModel.getId(), fiModel.getPermittedUserIds(), fiModel.getUserLoginModels());
        return FiModelFullHelper.toModel(fiLocal.save(fi, lang.getCode()), lang.getId());
    }

}
