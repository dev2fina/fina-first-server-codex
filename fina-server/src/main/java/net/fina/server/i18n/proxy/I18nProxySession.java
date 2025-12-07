package net.fina.server.i18n.proxy;

import net.fina.common.client.property.PropertyKeys;
import net.fina.server.i18n.api.I18nLocal;
import net.fina.server.i18n.entity.I18nEntity;
import net.fina.server.i18n.model.I18nUpdateBodyModel;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class I18nProxySession {

    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private I18nLocal i18nLocal;

    @Inject
    private PropertyLocal propertyLocal;

    public List<I18nEntity> loadAll() {
        return i18nLocal.loadAll();
    }

    public List<I18nEntity> loadByLanguageCode(String langCode) {
        return i18nLocal.loadByLanguageCode(langCode);
    }


    public void update(List<I18nUpdateBodyModel> data) {
        boolean hasUpdated = false;
        for (I18nUpdateBodyModel d : data) {
            i18nLocal.update(d.getKey(), d.getValue(), d.getLangCode());
            hasUpdated = true;
        }
        if (hasUpdated) {
            updateI18nTranslationVersion();
        }
    }

    public List<String> create(List<I18nUpdateBodyModel> models) {
        List<String> response = new ArrayList<>();
        boolean hasUpdated = false;
        for (I18nUpdateBodyModel d : models) {
            response.add(i18nLocal.create(d.getKey(), d.getValue(), d.getLangCode()));
            hasUpdated = true;
        }
        if (hasUpdated) {
            updateI18nTranslationVersion();
        }
        return response;
    }

    private void updateI18nTranslationVersion() {
        try {
            double i18nVersion = Double.parseDouble(propertyLocal.getSystemProperty(PropertyKeys.I18N_TRANSLATION_BUNDLE_VERSION, "1.0"));
            propertyLocal.setSystemProperty(PropertyKeys.I18N_TRANSLATION_BUNDLE_VERSION, String.valueOf(++i18nVersion));
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

}
