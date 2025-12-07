package net.fina.server.i18n.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.i18n.entity.Language;

import java.util.List;
import java.util.Map;

public interface LanguageLocal {
    List<Language> loadLanguages();

    List<Language> loadAllLanguages();

    /**
     * @return Language Entity
     */
    Language getLanguageByCode(String code);

    void delete(long langId) throws FinATypeException;

    Language save(Language language) throws FinATypeException;

    Map<Long, String> getLanguageNameByIds(List<Long> langIds);

    Map<String, String> getLanguagesCodeNameMap();

    Map<String, Long> getLanguagesCodeIdMap();

    List<Long> getLanguageIds();

    String getLanguageCodeById(long langId);

    Language getLanguageById(long id);

    Language activateDeletedLanguage(Language language) throws FinATypeException;

    Map<String, Long> loadLanguageCodeIdMap();

    Language getLanguageByCodeOrDefault(String code);

    Language getDefaultLanguage();

    Map<Long, String> getLanguageIdCodeMap();
}
