package net.fina.server.i18n.impl;

import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import net.fina.server.i18n.api.SysStringLocal;
import net.fina.server.i18n.cache.SysStringCacheManager;
import net.fina.server.i18n.entity.SysString;
import net.fina.server.i18n.entity.SysStringId;
import net.fina.server.util.DBUtil;
import org.jboss.logging.Logger;

import java.util.List;

@Stateless
@Local(SysStringLocal.class)
public class SysStringSession implements SysStringLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager entityManager;

    @EJB
    private SysStringCacheManager cacheManager;

    @EJB
    private LanguageListSingleton languageList;

    @Override
    public List<SysString> loadSysStrings() {
        List<SysString> result = null;
        try {
            TypedQuery<SysString> query = entityManager.createQuery("SELECT s FROM SYS_STRINGS s ", SysString.class);
            result = query.getResultList();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delete(long nameStrId) {
        try {
            Query deleteSysStringQuery = entityManager.createQuery("delete from SYS_STRINGS as s where s.id=:nameStrId");
            deleteSysStringQuery.setParameter("nameStrId", nameStrId);
            deleteSysStringQuery.executeUpdate();

            removeFromCache(languageList.getLanguageIds(), nameStrId);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delete(List<Long> nameStrIds) {
        try {
            if (nameStrIds == null || nameStrIds.isEmpty()) {
                return;
            }

            String sysStringIdConcatenatedString = DBUtil.get().generateConcatenatedInStatementWithIds("s.id", nameStrIds);

            entityManager.createQuery("delete from SYS_STRINGS as s where (" + sysStringIdConcatenatedString + ")")
                    .executeUpdate();

            List<Long> langIds = languageList.getLanguageIds();
            nameStrIds.forEach(id -> removeFromCache(langIds, id));

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(Long nameStrId, Long langId, String value) {
        try {
            SysStringId sysStringId = new SysStringId();
            sysStringId.setId(nameStrId);
            sysStringId.setLangId(langId);

            SysString sysString = entityManager.find(SysString.class, sysStringId);

            if (sysString != null) {
                sysString.setValue(value);
            } else {
                sysString = new SysString();
                sysString.setId(sysStringId.getId());
                sysString.setLangId(sysStringId.getLangId());
                sysString.setValue(value);

                Query query = entityManager.createNativeQuery("insert into SYS_STRINGS(id, langId, value) values(?,?,?)");
                query.setParameter(1, sysString.getId());
                query.setParameter(2, sysString.getLangId());
                query.setParameter(3, sysString.getValue());
                query.executeUpdate();
            }

            // Add cache
            cacheManager.addCache(nameStrId, langId, value);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long add(Long langId, String originalValue) {
        long nameStrId = 0;
        try {
            SysString sysString = new SysString();
            sysString.setLangId(langId);
            sysString.setValue(originalValue);

            entityManager.persist(sysString);

            nameStrId = sysString.getId();

            // Add cache
            cacheManager.addCache(nameStrId, langId, originalValue);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return nameStrId;
    }

    private void removeFromCache(List<Long> langIds, long nameStrId) {
        for (long languageId : langIds) {
            SysStringId sysStringId = new SysStringId();
            sysStringId.setId(nameStrId);
            sysStringId.setLangId(languageId);

            cacheManager.removeCache(sysStringId);
        }
    }

}
