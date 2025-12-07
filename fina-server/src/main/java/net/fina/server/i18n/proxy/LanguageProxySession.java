package net.fina.server.i18n.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.shared.LanguageMetaModel;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.LanguageModelHelper;
import net.fina.server.i18n.impl.LanguageListSingleton;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class LanguageProxySession {

    @Inject
    private LanguageLocal languageLocal;
    @Inject
    private LanguageListSingleton languageListSingleton;
    @Inject
    private PropertyLocal propertyLocal;


    public Map<String, Long> loadLanguageCodeIdMap() {
        return languageLocal.loadLanguageCodeIdMap();
    }


    public List<LanguageMetaModel> loadLanguagesFull() {
        List<LanguageMetaModel> models = LanguageModelHelper.toModelsFull(languageLocal.loadLanguages());
        String defaultLangCode = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE);

        return models.stream().peek(m -> m.setDefault(m.getCode().equals(defaultLangCode))).collect(Collectors.toList());
    }


    public List<LanguageSampleModel> loadLanguages() {
        List<Language> languages = languageLocal.loadLanguages();
        String defaultLangCode = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE);
        return LanguageModelHelper.toModels(languages, defaultLangCode);
    }

    public List<LanguageMetaModel> loadLanguagesMetaModel() {
        return LanguageModelHelper.toModelsFull(languageLocal.loadLanguages());
    }

    public LanguageSampleModel getLanguageByCode(String code) {
        return LanguageModelHelper.toModel(languageLocal.getLanguageByCode(code));
    }

    public long getLanguageIdByCode(String code) {
        Long id = languageListSingleton.getLanguageIdMap().get(code);
        return id == null ? getDefaultLanguage() : id;
    }

    public long getDefaultLanguage() {
        return languageLocal.getDefaultLanguage().getId();
    }

    @RolesAllowed(PermissionIdNames.FINA_LANGUAGE_AMEND)
    public LanguageMetaModel saveLanguage(LanguageMetaModel model) throws FinATypeException {
        Language language = languageLocal.save(LanguageModelHelper.toEntity(model));
        return LanguageModelHelper.toModelFull(language);
    }

    @RolesAllowed(PermissionIdNames.FINA_LANGUAGE_DELETE)
    public void deleteLanguage(long langId) throws FinATypeException {
        languageLocal.delete(langId);
    }

    public LanguageMetaModel getLanguageById(long langId) {
        return LanguageModelHelper.toModelFull(languageLocal.getLanguageById(langId));
    }


    public LanguageMetaModel activateDeletedLanguage(long langId) throws FinATypeException {
        Language language = languageLocal.getLanguageById(langId);
        return LanguageModelHelper.toModelFull(languageLocal.activateDeletedLanguage(language));
    }

}
