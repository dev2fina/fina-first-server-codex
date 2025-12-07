package net.fina.server.fi.proxy;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.api.FiBranchTypeLocal;
import net.fina.server.fi.entity.FiBranchType;
import net.fina.server.fi.model.FiBranchTypeMetaModel;
import net.fina.server.fi.model.FiBranchTypeModelHelper;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.List;


@Stateless
@SecurityDomain("FinASecurityDomain")
public class FiBranchTypeProxySession {

    private Logger log = Logger.getLogger(getClass());

    @Inject
    private FiBranchTypeLocal fiBranchTypeLocal;

    @Inject
    private LanguageLocal languageLocal;

    public List<FiBranchTypeMetaModel> load(String locale) throws FinATypeException {
        try {
            Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
            List<FiBranchType> fiBranchTypes = fiBranchTypeLocal.load();
            return FiBranchTypeModelHelper.getModels(fiBranchTypes, lang.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }
    }

    public List<FiBranchTypeMetaModel> loadWithCount(long fiId, String locale, boolean includeAll) throws FinATypeException {
        try {
            Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
            List<FiBranchType> fiBranchTypes = fiBranchTypeLocal.loadWithCount(fiId, includeAll);
            return FiBranchTypeModelHelper.getModels(fiBranchTypes, lang.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }
    }

    public FiBranchTypeMetaModel save(FiBranchTypeMetaModel fiBranchTypeMetaModel, String locale) throws FinATypeException {
        try {
            Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
            FiBranchType fiBranchType = fiBranchTypeLocal.save(FiBranchTypeModelHelper.getEntity(fiBranchTypeMetaModel, lang.getId()));
            return FiBranchTypeModelHelper.getModel(fiBranchType, lang.getId());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }
    }

    public void delete(long id) throws FinATypeException {
        fiBranchTypeLocal.delete(id);
    }

}
