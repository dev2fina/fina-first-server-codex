package net.fina.server.fi.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.fis.FiGroupModel;
import net.fina.common.server.util.CommonUtil;
import net.fina.messages.MessagesUtil;
import net.fina.server.fi.api.PeerGroupLocal;
import net.fina.server.fi.entity.Criterion;
import net.fina.server.fi.entity.PeerGroup;
import net.fina.server.fi.model.FiGroupModelHelper;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.ArrayList;
import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.FINA_BANK_REVIEW)
public class PeerGroupProxySession {
    @Inject
    private PeerGroupLocal peerGroupLocal;
    @Inject
    private LanguageLocal languageLocal;

    public List<FiGroupModel> loadAllFiGroups(String locale) {
        List<FiGroupModel> models = new ArrayList<>();
        for (Criterion criterion : peerGroupLocal.loadNodes()) {
            models.addAll(loadChildren(criterion.getId(), locale));
        }
        return models;
    }

    public List<FiGroupModel> loadRoots(String locale) {
        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
        List<FiGroupModel> rootModels = new ArrayList<>();
        for (Criterion criterion : peerGroupLocal.loadNodes()) {
            FiGroupModel model = FiGroupModelHelper.toModel(criterion, lang.getId());
            model.setType(FiGroupModel.Type.PARENT);
            rootModels.add(model);
        }

        return rootModels;
    }


    public List<FiGroupModel> loadChildren(long parentId, String locale) {

        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
        List<FiGroupModel> models = new ArrayList<>();

        for (PeerGroup peerGroup : peerGroupLocal.loadChildren(parentId)) {
            FiGroupModel model = FiGroupModelHelper.toModel(peerGroup, lang.getId());
            model.setType(FiGroupModel.Type.CHILD);
            models.add(model);
        }
        return models;
    }


    public FiGroupModel saveFiGroup(FiGroupModel model, String locale) throws FinATypeException {
        Language lang = languageLocal.getLanguageByCodeOrDefault(locale);
        long langId = lang.getId();
        switch (model.getType()) {
            case CHILD: {
                PeerGroup peerGroup = FiGroupModelHelper.toPeerGroupEntity(model, langId);
                PeerGroup savedPeerGroup = peerGroupLocal.savePeerGroup(peerGroup);
                model = FiGroupModelHelper.toModel(savedPeerGroup, lang.getId());
            }
            break;

            case PARENT: {
                Criterion criterion = FiGroupModelHelper.toCriterionEntity(model, langId);
                List<String> bankCodes = null;

                if (criterion.getIsDefault()) {
                    bankCodes = peerGroupLocal.setDefault(criterion.getId());
                }

                Criterion savedCriterion = peerGroupLocal.saveCriterion(criterion);
                model = FiGroupModelHelper.toModel(savedCriterion, lang.getId());

                if (bankCodes != null && !bankCodes.isEmpty()) {
                    throw new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString(FinATypeException.Type.CRITERION_SAVED_WITH_WARNINGS.getCode()), bankCodes.toString()));
                }
            }
            break;
        }
        return model;
    }

    @Transactional(rollbackOn = FinATypeException.class)
    public void makeDefaultCriterion(long criterionId) throws FinATypeException {
        Criterion criterion = peerGroupLocal.loadCriterionById(criterionId);
        if (criterion == null) throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Criterion not found");
        List<String> bankCodes = peerGroupLocal.setDefault(criterion.getId());

        if (bankCodes != null && !bankCodes.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CANNOT_MAKE_DEFAULT_CRITERION);
        }
    }

    public void delete(long id, FiGroupModel.Type type) throws FinATypeException {
        Class className = type.equals(FiGroupModel.Type.PARENT) ? Criterion.class : PeerGroup.class;
        if (className == Criterion.class) {
            peerGroupLocal.deleteCriterion(id);
        } else {
            peerGroupLocal.deletePeerGroup(id);
        }
    }

    public Long getDefaultCriteriaId() {
        return peerGroupLocal.getDefaultCriteriaId();
    }

    public List<FiGroupModel> loadDefaultFiGroups(String locale) {
        List<FiGroupModel> models = new ArrayList<>();
        for (Criterion criterion : peerGroupLocal.loadNodes()) {
            if (criterion.getIsDefault()) {
                models = loadChildren(criterion.getId(), locale);
                break;
            }
        }
        return models;
    }
}
