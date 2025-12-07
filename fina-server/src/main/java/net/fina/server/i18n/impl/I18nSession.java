package net.fina.server.i18n.impl;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.server.i18n.api.I18nLocal;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.I18nEntity;
import net.fina.server.i18n.entity.I18nEntity_;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
@Interceptors(RecordingAuditor.class)
@PermitAll
public class I18nSession implements I18nLocal {

    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private EntityManager em;

    @Inject
    private LanguageLocal languageLocal;

    @Override
    public List<I18nEntity> loadByLanguageCode(String langCode) {
        return em.createQuery("select st from SYS_TRANSLATIONS st where st.langCode=:langCode", I18nEntity.class)
                .setParameter("langCode", langCode.trim())
                .getResultList();
    }

    @Override
    public List<I18nEntity> loadAll() {
        return em.createQuery("select st from SYS_TRANSLATIONS st order by st.langCode", I18nEntity.class).getResultList();
    }

    @Override
    @RolesAllowed(PermissionIdNames.I18N_AMEND)
    public void update(String key, String value, String langCode) {
        I18nEntity existing = getTranslationByKeyAndLanguage(key, langCode);

        if (existing != null && existing.getLangCode() != null) {
            existing.setValue(value);
        } else {
            I18nEntity newEntity = new I18nEntity(key, value, langCode);
            em.persist(newEntity);
        }
    }

    @Override
    @RolesAllowed(PermissionIdNames.I18N_AMEND)
    public String create(String key, String value, String langCode) {
        List<String> languageCodes = languageLocal.loadLanguages().stream().map(Language::getCode).toList();
        I18nEntity existing = getTranslationByKeyAndLanguage(key, langCode);

        if (languageCodes.contains(langCode) && existing.getKey() == null) {
            I18nEntity entity = new I18nEntity(key.trim(), value.trim(), langCode.trim());
            em.persist(entity);
            return String.format("Translation with key (%s) and langCode (%s) created successfully", key, langCode);
        } else {
            log.warn("Cannot create translation language code does not exist - " + langCode);
        }

        return "";
    }

    @Override
    public List<I18nEntity> loadTranslations(int offset, int limit, String langCode, String key, String value) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<I18nEntity> cq = cb.createQuery(I18nEntity.class);

        Root<I18nEntity> root = cq.from(I18nEntity.class);
        List<Predicate> predicates = getFilterPredicates(cb, root, langCode, key, value);
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        TypedQuery<I18nEntity> query = em.createQuery(cq);

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public long countTranslations(String langCode, String key, String value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<I18nEntity> root = cq.from(I18nEntity.class);
        cq.select(cb.count(root));

        List<Predicate> predicates = getFilterPredicates(cb, root, langCode, key, value);

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(cq).getSingleResult();
    }


    private I18nEntity getTranslationByKeyAndLanguage(String key, String langCode) {
        try {
            return em.createQuery("select st from SYS_TRANSLATIONS st where st.key=:key and st.langCode=:langCode", I18nEntity.class)
                    .setParameter("key", key)
                    .setParameter("langCode", langCode)
                    .getSingleResult();
        } catch (Throwable ignored) {
        }
        //empty
        return new I18nEntity();
    }


    private List<Predicate> getFilterPredicates(CriteriaBuilder cb, Root<I18nEntity> root, String langCode, String key, String value) {
        List<Predicate> predicates = new ArrayList<>();

        if (!langCode.isEmpty()) {
            predicates.add(cb.equal(root.get(I18nEntity_.langCode), langCode.trim()));
        }
        if (!key.trim().isEmpty()) {
            predicates.add(cb.like(root.get(I18nEntity_.key), "%" + key.trim() + "%"));
        }
        if (!value.trim().isEmpty()) {
            predicates.add(cb.like(root.get(I18nEntity_.value), "%" + value.trim() + "%"));
        }

        return predicates;
    }
}
