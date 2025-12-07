package net.fina.server.fi.entity;

import net.fina.server.ThreadLocalHolder;
import net.fina.server.envers.RevInfo;
import net.fina.server.fi.api.FiHistoryLocal;
import net.fina.server.fi.impl.RegionCacheManager;
import net.fina.server.fi.model.*;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.model.BeneficiaryMetaModel;
import net.fina.server.legalperson.model.helper.BeneficiaryModelHelper;
import net.fina.server.license.entity.Licence;
import net.fina.server.license.model.LicenseMetaModel;
import net.fina.server.license.model.LicenseMetaModelHelper;
import net.fina.server.person.entity.CriminalRecord;
import net.fina.server.person.model.CriminalRecordMetaModel;
import net.fina.server.person.model.helper.CriminalRecordModelHelper;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Local(FiHistoryLocal.class)
@Stateless
public class FiHistorySession implements FiHistoryLocal {
    @Inject
    private EntityManager em;
    @Inject
    private RegionCacheManager regionCacheManager;

    @Override
    public List<EntityHistoryModel<FiModelFull>> loadFiHistory(long fiId, int offset, int limit) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<RevInfo> criminalRecordsRevInfo = auditReader.createQuery().forRevisionsOfEntity(CriminalRecord.class, true).getResultList();
        List<Number> fiRevisions = auditReader.getRevisions(Fi.class, fiId);

        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(Fi.class, false, true)
                .add(AuditEntity.id().eq(fiId))
                .addOrder(AuditEntity.revisionProperty("timestamp").desc());

        if (!criminalRecordsRevInfo.isEmpty() && !fiRevisions.isEmpty()) {
            for (RevInfo revInfo : criminalRecordsRevInfo) {
                long id = revInfo.getId();
                if (fiRevisions.contains(id)) {
                    fiRevisions.remove(id);
                }
            }
            query.add(AuditEntity.revisionNumber().in(fiRevisions));
        }

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        List<Object[]> result = query.getResultList();

        Function<Fi, Object> convertFunction = fi -> {
            FiModelFull fiModel = FiModelFullHelper.toModel(fi, langId);
            if (fiModel.getRegionId() != null && fiModel.getRegionId() > 0) {
                fiModel.setRegion(regionCacheManager.getRegion(fiModel.getRegionId(), langId));
            }
            return fiModel;
        };

        return getHistory(result, convertFunction);
    }

    @Override
    public List<EntityHistoryModel<FiBranchMetaModel>> loadFiBranchHistory(long fiId, int offset, int limit) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(FiBranch.class, false, true).
                add(AuditEntity.property("bankId").eq(fiId))
                .addOrder(AuditEntity.revisionProperty("timestamp").desc());

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }
        long langId = ThreadLocalHolder.getLanguage().getId();

        return getHistory(query.getResultList(), (Function<FiBranch, Object>) fiBranch -> FiBranchMetaModelHelper.toModel(fiBranch, langId));
    }

    @Override
    public List<EntityHistoryModel<FiBranchMetaModel>> loadBranchHistory(long branchId, int offset, int limit) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(FiBranch.class, false, true)
                .add(AuditEntity.id().eq(branchId))
                .addOrder(AuditEntity.revisionProperty("timestamp").desc());

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }
        long langId = ThreadLocalHolder.getLanguage().getId();

        return getHistory(query.getResultList(), (Function<FiBranch, Object>) fiBranch -> FiBranchMetaModelHelper.toModel(fiBranch, langId));
    }

    @Override
    public long countFiHistory(long fiId) {
        return countAuditRecord(Fi.class, fiId);
    }

    @Override
    public long countFiBranchesHistory(long fiId) {
        return countAuditRecord(FiBranch.class, AuditEntity.property("bankId").eq(fiId));
    }

    @Override
    public long countBranchHistory(long branchId) {
        return countAuditRecord(FiBranch.class, branchId);
    }

    @Override
    public List<EntityHistoryModel<FiManagementMetaModel>> loadFiManagementHistory(long fiId, long managementId, int offset, int limit) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(FiManagement.class, false, true)
                .add(AuditEntity.id().eq(managementId))
                .add(AuditEntity.property("fiId").eq(fiId))
                .addOrder(AuditEntity.revisionProperty("timestamp").desc());

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        long langId = ThreadLocalHolder.getLanguage().getId();

        return getHistory(query.getResultList(), (Function<FiManagement, Object>) management -> FiManagementMetaModelHelper.toModel(management, langId));
    }

    @Override
    public List<EntityHistoryModel<BeneficiaryMetaModel>> loadFiBeneficiaryHistory(long beneficiaryId, int offset, int limit) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(Beneficiary.class, false, true)
                .add(AuditEntity.id().eq(beneficiaryId))
                .addOrder(AuditEntity.revisionProperty("timestamp").desc());

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        long langID = ThreadLocalHolder.getLanguage().getId();
        return getHistory(query.getResultList(), (Function<Beneficiary, Object>) beneficiary -> BeneficiaryModelHelper.toModel(beneficiary, langID));
    }

    @Override
    public long countManagementHistory(long fiId, long managementId) {
        return countAuditRecord(FiManagement.class, AuditEntity.property("fiId").eq(fiId), AuditEntity.id().eq(managementId));
    }

    @Override
    public long countBeneficiaryHistory(long beneficiaryId) {
        return countAuditRecord(Beneficiary.class, AuditEntity.id().eq(beneficiaryId));
    }

    @Override
    public List<EntityHistoryModel<CriminalRecordMetaModel>> loadFiCriminalRecordsHistory(long fiId, int offset, int limit) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        List<Number> fiRevision = auditReader.getRevisions(Fi.class, fiId);
        if (fiRevision.isEmpty()) {
            return new ArrayList<>();
        }

        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(CriminalRecord.class, false, true)
                .add(AuditEntity.revisionNumber().in(fiRevision))
                .addOrder(AuditEntity.revisionProperty("timestamp").desc());

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        List<Object[]> result = query.getResultList();
        long langId = ThreadLocalHolder.getLanguage().getId();

        Function<CriminalRecord, Object> convertFunction = cr -> CriminalRecordModelHelper.toModel(cr, langId);


        return getHistory(result, convertFunction);
    }

    @Override
    public long countFiCriminalRecordsHistory(long fiId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Number> fiRevision = auditReader.getRevisions(Fi.class, fiId);
        if (fiRevision.isEmpty()) {
            return 0;
        }

        return countAuditRecord(FiManagement.class, AuditEntity.revisionNumber().in(fiRevision));
    }

    @Override
    public List<EntityHistoryModel<LicenseMetaModel>> loadFiLicenseHistory(long licenseId, int offset, int limit) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(Licence.class, false, true)
                .add(AuditEntity.id().eq(licenseId))
                .addOrder(AuditEntity.revisionProperty("timestamp").desc());

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        long langId = ThreadLocalHolder.getLanguage().getId();

        return getHistory(query.getResultList(), (Function<Licence, Object>) lic -> LicenseMetaModelHelper.toModel(lic, langId));
    }

    @Override
    public long countFiLicenseHistory(long licenseId) {
        return countAuditRecord(Licence.class, licenseId);
    }

    @Override
    public List<EntityHistoryModel<CriminalRecordMetaModel>> loadCriminalRecordHistory(long criminalRecordId, int offset, int limit) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(CriminalRecord.class, false, true)
                .add(AuditEntity.id().eq(criminalRecordId))
                .addOrder(AuditEntity.revisionProperty("timestamp").desc());

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        List<Object[]> result = query.getResultList();
        long langId = ThreadLocalHolder.getLanguage().getId();

        Function<CriminalRecord, Object> convertFunction = cr -> CriminalRecordModelHelper.toModel(cr, langId);


        return getHistory(result, convertFunction);
    }

    @Override
    public long countCriminalRecordHistory(long criminalRecordId) {
        return countAuditRecord(CriminalRecord.class, AuditEntity.id().eq(criminalRecordId));
    }

    private long countAuditRecord(Class recordClass, AuditCriterion... auditCriterions) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(recordClass, false, true)
                .addProjection(AuditEntity.revisionNumber().count());
        if (auditCriterions != null) {
            for (AuditCriterion auditCriterion : auditCriterions) {
                query.add(auditCriterion);
            }
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    private long countAuditRecord(Class recordClass, long id) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(recordClass, false, true)
                .addProjection(AuditEntity.revisionNumber().count());
        if (id >= 0) {

            query.add(AuditEntity.id().eq(id));
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    private <T, K> List<EntityHistoryModel<T>> getHistory(List<Object[]> list, Function<K, Object> function) {
        List<EntityHistoryModel<T>> historyModels = new ArrayList<>();
        list.forEach(r -> {
            K entity = (K) r[0];
            T model = (T) function.apply(entity);
            RevInfo revInfo = (RevInfo) r[1];
            RevisionType revisionType = (RevisionType) r[2];
            historyModels.add(new EntityHistoryModel<T>(model, new Date(revInfo.getTimestamp()), revInfo.getId(), revInfo.getUsername(), revisionType));
        });

        return historyModels;
    }

}
