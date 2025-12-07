package net.fina.server.i18n.impl;

import net.fina.server.i18n.api.DescriptionLocal;
import net.fina.server.i18n.api.SysStringLocal;
import net.fina.server.i18n.cache.SysStringCacheManager;
import net.fina.server.i18n.entity.SysStringId;
import net.fina.server.i18n.helper.Description;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class DescriptionBean implements DescriptionLocal {

    @EJB
    private SysStringCacheManager cacheManager;

    @EJB
    private SysStringLocal sysStringLocal;

    @EJB
    private LanguageListSingleton languagesList;

    @Override
    public Description getDescription(long nameStrId) {
        Description result = new Description();

        List<Long> langIds = languagesList.getLanguageIds();
        for (long langId : langIds) {

            SysStringId sysStringId = new SysStringId();
            sysStringId.setId(nameStrId);
            sysStringId.setLangId(langId);

            Object descriptionObject = cacheManager.getDescription(sysStringId);

            String description = descriptionObject == null ? "NONAME" : descriptionObject.toString();
            result.addDescription(langId, description);
            result.setNameStrId(nameStrId);
        }
        return result;
    }

    @Override
    public long getNameStrId(String value, long langId) {
        long nameStrId = sysStringLocal.add(langId, value);
        return nameStrId;
    }

    @Override
    public void updateSysString(long nameStrId, long langId, Object value) {
        if (value != null) {
            sysStringLocal.update(nameStrId, langId, value.toString());
        }
    }

    @Override
    public void removeSysString(long nameStrId) {
        if(nameStrId>0){
            sysStringLocal.delete(nameStrId);
        }
    }

}
