package net.fina.server.i18n.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.PropertyLocal;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(LanguageLocal.class)
@Interceptors(RecordingAuditor.class)
public class LanguageSession implements LanguageLocal {

    @Inject
    private EntityManager em;

    @EJB
    private LanguageListSingleton languageListSingleton;
    @Inject
    private PropertyLocal propertyLocal;

    @Override
    public List<Language> loadLanguages() {
        return em.createNamedQuery("Language.findAll", Language.class).setParameter("deleted", false).getResultList();
    }

    @Override
    public List<Language> loadAllLanguages() {
        return em.createQuery("select l from SYS_LANGUAGES l", Language.class).getResultList();
    }

    @Override
    public Language getLanguageByCode(String code) {
        return em.createNamedQuery("Language.findByCode", Language.class).setParameter("code", code.trim()).getSingleResult();
    }

    @Override
    public void delete(long langId) throws FinATypeException {
        Language language = em.find(Language.class, langId);

        if (language != null) {
            if (hasLanguageDependency(langId)) {
                // programmatically delete
                language.setDeleted(true);
                em.merge(language);
                return;
            }
            /** Remove Language */
            em.remove(language);

            /**
             * Reload languages list
             */
            languageListSingleton.reload();
        }
    }

    private boolean hasLanguageDependency(long langId) {
        return em.createNativeQuery("SELECT S.id FROM SYS_STRINGS S WHERE S.langId=:langId ")
                .setParameter("langId", langId)
                .getResultList().size() > 0;
    }

    @Override
    public Language save(Language language) throws FinATypeException {
        List<Language> sameCodeLanguages = em.createNamedQuery("Language.sameCodeLanguages", Language.class)
                .setParameter("code", language.getCode())
                .setParameter("id", language.getId())
                .getResultList();

        if (!sameCodeLanguages.isEmpty()) {
            if (sameCodeLanguages.get(0).isDeleted()) {
                throw new FinATypeException(FinATypeException.Type.LANGUAGE_PROGRAMMATICALLY_DELETED,
                        new String[]{String.valueOf(sameCodeLanguages.get(0).getId())});
            }
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }

        language.setCode(language.getCode().trim());

        if (language.getId() > 0) {
            language = em.merge(language);
        } else {
            em.persist(language);
        }

        /**
         * Reload languages List
         */
        languageListSingleton.reload();

        return language;
    }

    @Override
    public Map<Long, String> getLanguageNameByIds(List<Long> langIds) {
        Map<Long, String> languages = new HashMap<>();
        List<Object[]> langNames = em.createNamedQuery("Language.nameById").setParameter("id", langIds).getResultList();
        for (Object[] object : langNames) {
            languages.put((Long) object[0], (String) object[1]);
        }
        return languages;
    }

    @Override
    public Map<String, String> getLanguagesCodeNameMap() {
        Map<String, String> result = new TreeMap<String, String>();
        List<Object[]> languageObjects = em.createNamedQuery("Language.codeName").setParameter("deleted", false).getResultList();
        for (Object[] objects : languageObjects) {
            result.put(objects[0].toString(), objects[1].toString());
        }
        return result;
    }

    @Override
    public Map<String, Long> getLanguagesCodeIdMap() {
        Map<String, Long> result = new TreeMap<>();
        List<Object[]> languageObjects = em.createNamedQuery("Language.codeId").setParameter("deleted", false).getResultList();
        for (Object[] objects : languageObjects) {
            result.put(objects[0].toString(), (Long) objects[1]);
        }
        return result;
    }

    @Override
    public List<Long> getLanguageIds() {
        return em.createNamedQuery("Language.findIds", Long.class).setParameter("deleted", false).getResultList();
    }

    @Override
    public String getLanguageCodeById(long langId) {
        return em.createNamedQuery("Language.findCodeById", String.class).setParameter("id", langId).getSingleResult();
    }

    @Override
    public Language getLanguageById(long id) {
        return em.find(Language.class, id);
    }

    @Override
    public Language activateDeletedLanguage(Language language) throws FinATypeException {
        Language existingLanguage = em.find(Language.class, language.getId());
        language.setDeleted(false);
        language.setVersion(existingLanguage.getVersion());
        return save(language);
    }

    @Override
    public Map<String, Long> loadLanguageCodeIdMap() {
        return em.createQuery("select code,id  from SYS_LANGUAGES where deleted=false ", Tuple.class)
                .getResultStream().collect(
                        Collectors.toMap(
                                tuple -> ((String) tuple.get(0)).trim(),
                                tuple -> ((Number) tuple.get(1)).longValue()
                        )
                );
    }

    @Override
    public Language getLanguageByCodeOrDefault(String code) {
        code = (code != null ? code.trim() : null);
        List<Language> languages = new ArrayList<>();
        if (code != null) {
            languages = em.createNamedQuery("Language.findByCode", Language.class).setParameter("code", code).getResultList();
        }

        if (languages.isEmpty()) {
            return getDefaultLanguage();
        }

        return languages.get(0);
    }

    @Override
    public Language getDefaultLanguage() {
        String defaultLangCode = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE);
        if (defaultLangCode != null && !defaultLangCode.isEmpty()) {
            return getLanguageByCode(defaultLangCode);
        }
        return em.createQuery("select  l from SYS_LANGUAGES as l order by l.id asc", Language.class)
                .getResultList().get(0);
    }

    @Override
    public Map<Long, String> getLanguageIdCodeMap() {
        return languageListSingleton.getLanguageIdCodeMap();
    }
}
