package net.fina.server.fi.impl;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.fis.LicenceStatus;
import net.fina.common.server.fi.sync.model.FiBranchSyncMetaModel;
import net.fina.common.server.fi.sync.model.FiDescriptionSyncMetaModel;
import net.fina.common.server.fi.sync.model.FiManagementSyncMetaModel;
import net.fina.common.server.fi.sync.model.FiSyncMetaModel;
import net.fina.security.auth.CustomPrincipal;
import net.fina.server.fi.api.FiFirstSyncLocal;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.*;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.license.entity.Licence;
import net.fina.server.license.entity.LicenceType;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
@Local(FiFirstSyncLocal.class)
@Interceptors(RecordingAuditor.class)
public class FiFirstSyncSession implements FiFirstSyncLocal {
    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;
    @Inject
    private UserLocal userLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private RegionLocal regionLocal;

    @Override
    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 30)
    public void syncFis(List<FiSyncMetaModel> models, List<Long> languageIds) throws FinATypeException {

        try (StatisticsLogger statLog = new StatisticsLogger("FIRST FI Registry to Database.");) {

            List<String> failedFiCodes = new ArrayList<>();
            Throwable lastException = null;
            statLog.logStage(String.format("Start sync FIRST to database. Number of FIs to sync - %d", models != null ? models.size() : -1));

            if (models != null && !models.isEmpty()) {
                User current = userLocal.getCurrentUser();
                CustomPrincipal principal = userLocal.getCallerPrincipal();

                LicenceType generalLicenceType = fiLocal.getLicenceTypeByCode("GL");
                Management synchronizedTypeManagement = fiLocal.findManagementByCode("SYNCHRONIZED");
                Map<String, Region> regionDescriptionIdMap = getRegionDescriptionMap(languageIds);

                for (FiSyncMetaModel model : models) {
                    try {
                        switch (model.getStatus()) {
                            case ACTIVE:
                                syncFi(model, regionDescriptionIdMap, current, principal, generalLicenceType, synchronizedTypeManagement, false);
                                break;
                            case SUSPENDED:
                            case REVOKED:
                                syncFi(model, regionDescriptionIdMap, current, principal, generalLicenceType, synchronizedTypeManagement, true);
                                break;
                        }
                        statLog.logStage(String.format("Successfully synced FI with code %s", model.getCode()));
                    } catch (Throwable t) {
                        String failedFiCode = model.getCode();
                        statLog.logMessage(String.format("Could not sync FI with code %s", failedFiCode), Logger.Level.ERROR);

                        failedFiCodes.add(failedFiCode);
                        lastException = t;
                    }
                }
            }


            if (!failedFiCodes.isEmpty()) {
                statLog.logStage("Failed to sync FIs with following codes: " + String.join(", ", failedFiCodes) + ".");
            }


            if (!failedFiCodes.isEmpty()) {
                String[] params = failedFiCodes.toArray(new String[0]);
                Throwable t = new Throwable(lastException.getCause());
                FinATypeException exception = new FinATypeException(t, FinATypeException.Type.FIS_FIRST_SYNC_FAILED, params);
                exception.setStackTrace(lastException.getStackTrace());
                throw exception;
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void syncFi(FiSyncMetaModel model, Map<String, Region> regionDescriptionIdMap, User current,
                        CustomPrincipal principal, LicenceType generalLicenceType, Management synchronizedTypeManagement, boolean isDisabled) {
        Fi fi = new Fi();

        List<Fi> fis = em.createQuery("select b from IN_BANKS  b where b.code=:code", Fi.class)
                .setParameter("code", model.getCode())
                .getResultList();

        if (!fis.isEmpty()) {
            fi = fis.stream().findFirst().get();
            fi.setDisable(isDisabled);
            setFiProperties(fi, regionDescriptionIdMap, model);
            em.merge(fi);
            if (!current.getFis().contains(fi)) {
                current.getFis().add(fi);
                principal.getFis().add(fi.getId());
            }
        } else {
            setFiProperties(fi, regionDescriptionIdMap, model);

            List<FiType> fiTypes = em.createQuery("select bt from IN_BANK_TYPES  bt where bt.code=:fiTypeCode", FiType.class)
                    .setParameter("fiTypeCode", model.getFiTypeCode())
                    .getResultList();

            FiType fiType;

            if (fiTypes.isEmpty()) {
                fiType = new FiType();
                fiType.setCode(model.getFiTypeCode());

                em.persist(fiType);

            } else {
                fiType = fiTypes.get(0);
            }

            fi.setFiType(fiType);
            fi.setDisable(isDisabled);

            em.persist(fi);

            current.getFis().add(fi);
            principal.getFis().add(fi.getId());
        }

        syncBranches(fi.getId(), regionDescriptionIdMap, model.getBranches());

        syncGeneralLicence(fi, model.getRegistrationDate(), generalLicenceType, isDisabled, model.getLegalActDate());

        syncManagement(fi.getId(), model.getManagement(), synchronizedTypeManagement);

        if (isDisabled) {
            disableFiExternalUsers(model.getCode());
        }
    }

    public void disableFiExternalUsers(String fiCode) {
        em.createQuery("update SYS_USERS " +
                "set disabled = 1 where id in " +
                "(select distinct u.id from SYS_USER_BANKS ub " +
                "join SYS_USERS u on u.id = ub.userId " +
                "join IN_BANKS b on b.id = ub.bankId " +
                "left join u.permissions p " +
                "left join u.roles r " +
                "left join r.permissions rp " +
                "where b.code = :fiCode and " +
                "(p.idName = :externalUserPermission or rp.idName = :externalUserPermission))")
                .setParameter("fiCode", fiCode)
                .setParameter("externalUserPermission", PermissionIdNames.FINA_WEB_EXTERNAL_USER)
                .executeUpdate();
    }

    private void setFiProperties(Fi fi, Map<String, Region> regionDescriptionIdMap, FiSyncMetaModel metaModel) {
        Description name = getDescription(metaModel.getDescriptions());

        fi.setShortName(name);
        fi.setDescription(name);
        fi.setCode(metaModel.getCode());
        fi.setIdentificationCode(metaModel.getIdentificationCode());
        fi.setPhone(metaModel.getPhone());
        fi.setEmail(metaModel.getEmail());
        fi.setAddressDescription(getDescription(metaModel.getAddressDescriptions()));

        Region region = regionDescriptionIdMap.get(metaModel.getRegionName());
        if (region != null) {
            fi.setRegionId(region.getId());
        }
    }

    private Description getDescription(List<FiDescriptionSyncMetaModel> fiDescriptionSyncMetaModels) {
        Description description = null;
        if (fiDescriptionSyncMetaModels != null && !fiDescriptionSyncMetaModels.isEmpty()) {
            description = new Description();
            description.setNameStrId(0);
            Map<Long, String> descriptions = new HashMap<>();
            for (FiDescriptionSyncMetaModel fiDescriptionSyncMetaModel : fiDescriptionSyncMetaModels) {
                descriptions.put(fiDescriptionSyncMetaModel.getLangId(), fiDescriptionSyncMetaModel.getValue());
            }
            description.setDescriptions(descriptions);
        }
        return description;
    }

    private void syncGeneralLicence(Fi fi, Date registrationDate, LicenceType generalLicenceType, boolean isFiDisabled, Date legalActDate) {
        if (registrationDate != null) {
            Collection<Licence> licences = fi.getLicences();
            if (licences != null && !licences.isEmpty()) {
                for (Licence licence : licences) {
                    if (licence.getLicenseType().getId() == generalLicenceType.getId()) {
                        licence.setCreationDate(registrationDate);
                        return;
                    }
                }
            }

            Licence licence = new Licence();
            licence.setLicenseType(generalLicenceType);
            licence.setFi(fi);
            licence.setCreationDate(registrationDate);


            if (isFiDisabled) {
                licence.setLicenceStatus(LicenceStatus.REVOKED);
                licence.setDateOfChange(legalActDate);
            } else {
                licence.setLicenceStatus(LicenceStatus.ACTIVE);
            }

            em.persist(licence);
        }
    }

    private void syncBranches(long fiId, Map<String, Region> regionDescriptionIdMap, List<FiBranchSyncMetaModel> fiBranchSyncMetaModels) {
        // Clear existing branches
        em.createQuery("delete from IN_BANK_BRANCHES bb where bb.bankId=:bankId")
                .setParameter("bankId", fiId)
                .executeUpdate();

        if (fiBranchSyncMetaModels != null && !fiBranchSyncMetaModels.isEmpty()) {
            for (FiBranchSyncMetaModel metaModel : fiBranchSyncMetaModels) {
                FiBranch fiBranch = new FiBranch();
                fiBranch.setBankId(fiId);
                fiBranch.setName(getDescription(metaModel.getNameDescriptions()));
                fiBranch.setShortName(getDescription(metaModel.getShortNameDescription()));
                fiBranch.setAddress(getDescription(metaModel.getAddressDescriptions()));
                fiBranch.setDisable(metaModel.getStatus() == null || !metaModel.getStatus().trim().equalsIgnoreCase("ACTIVE"));
                fiBranch.setCreateDate(metaModel.getRegistrationDate());
                fiBranch.setChangeDate(metaModel.getLegalActDate());

                Region region = regionDescriptionIdMap.get(metaModel.getRegionName());
                fiBranch.setRegion(region);

                em.persist(fiBranch);
            }
        }
    }

    private void syncManagement(long fiId, List<FiManagementSyncMetaModel> managementSyncMetaModels, Management management) {
        // clear existing synchronized management
        em.createQuery("delete from IN_BANK_MANAGEMENT ibm where ibm.fiId=:fiId and ibm.management.id=:managementBodyId")
                .setParameter("fiId", fiId)
                .setParameter("managementBodyId", management.getId())
                .executeUpdate();

        if (managementSyncMetaModels != null && !managementSyncMetaModels.isEmpty()) {
            for (FiManagementSyncMetaModel metaModel : managementSyncMetaModels) {
                FiManagement fiManagement = new FiManagement();
                fiManagement.setFiId(fiId);
                fiManagement.setManagement(management);
                fiManagement.setPhone(metaModel.getPhone());
                fiManagement.setDescription(getDescription(metaModel.getFirstNameDescriptions()));
                fiManagement.setLastDescription(getDescription(metaModel.getLastNameDescriptions()));
                fiManagement.setPost(getDescription(metaModel.getPositionDescriptions()));
                fiManagement.setResident(metaModel.getNonResidentDocumentNumber() == null || metaModel.getNonResidentDocumentNumber().trim().isEmpty());
                fiManagement.setDisable(metaModel.getFinalStatus() == null || !metaModel.getFinalStatus().trim().equalsIgnoreCase("ACTIVE"));

                em.persist(fiManagement);
            }
        }
    }

    private Map<String, Region> getRegionDescriptionMap(List<Long> languageIds) {
        List<Region> regions = regionLocal.loadRegions();

        Map<String, Region> regionDescriptionIdMap = new HashMap<>();

        for (Region region : regions) {
            for (Long languageId : languageIds) {
                String regionName = region.getDescription().getDescription(languageId);
                regionDescriptionIdMap.put(regionName, region);
            }
        }

        return regionDescriptionIdMap;
    }

}
