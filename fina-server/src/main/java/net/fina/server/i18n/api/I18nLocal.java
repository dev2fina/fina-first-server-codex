package net.fina.server.i18n.api;

import net.fina.server.i18n.entity.I18nEntity;

import java.util.List;

public interface I18nLocal {
    List<I18nEntity> loadByLanguageCode(String langCode);

    List<I18nEntity> loadAll();

    void update(String key, String value, String langCode);

    String create(String key, String value, String langCode);

    List<I18nEntity> loadTranslations(int offset, int limit, String langCode, String key, String value);

    long countTranslations(String langCode, String key, String value);
}
