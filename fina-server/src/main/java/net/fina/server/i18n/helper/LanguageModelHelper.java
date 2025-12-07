package net.fina.server.i18n.helper;

import net.fina.common.shared.LanguageMetaModel;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.server.i18n.entity.Language;
import net.fina.server.misc.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

public class LanguageModelHelper {


    public static Language toEntity(LanguageMetaModel language) {
        Language result = new Language();
        ObjectUtil.copyProperties(language, result);
        return result;
    }

    public static LanguageSampleModel toModel(Language language) {
        LanguageSampleModel result = new LanguageSampleModel();

        if (language != null) {
            result.setId(language.getId());
            result.setCode(language.getCode());
            result.setName(language.getName());
            result.setEncoding(language.getXmlEncoding());
        }

        return result;
    }

    public static LanguageSampleModel toModel(Language language, String defaultLangCode) {
        LanguageSampleModel result = toModel(language);
        if (defaultLangCode != null) {
            result.setDefaultLanguage(result.getCode().trim().equals(defaultLangCode.trim()));
        }
        return result;
    }

    public static List<LanguageSampleModel> toModels(List<Language> languages, String defaultLangCode) {
        List<LanguageSampleModel> resultList = new ArrayList<>();

        if (languages != null) {
            languages.forEach(language -> resultList.add(toModel(language,defaultLangCode)));
        }

        return resultList;
    }

    public static List<LanguageMetaModel> toModelsFull(List<Language> languages) {
        List<LanguageMetaModel> resultList = new ArrayList<>();

        if (languages != null) {
            languages.forEach(language -> resultList.add(toModelFull(language)));
        }

        return resultList;
    }

    public static LanguageMetaModel toModelFull(Language language) {
        LanguageMetaModel model = new LanguageMetaModel();
        ObjectUtil.copyProperties(language, model);
        return model;
    }

}
