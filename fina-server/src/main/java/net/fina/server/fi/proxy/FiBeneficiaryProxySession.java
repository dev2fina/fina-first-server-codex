package net.fina.server.fi.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiBeneficiaryLocal;
import net.fina.server.fi.util.FiBeneficiaryFilterType;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.model.BeneficiaryMetaModel;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.legalperson.model.helper.BeneficiaryModelHelper;
import net.fina.server.legalperson.model.helper.LegalPersonModelHelper;
import net.fina.server.legalperson.proxy.LegalPersonProxySession;
import net.fina.server.person.api.PersonLocal;
import net.fina.server.person.model.ShareMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.FINA_BANK_REVIEW)
public class FiBeneficiaryProxySession {

    @Inject
    private FiBeneficiaryLocal fiBeneficiaryLocal;
    @Inject
    private PersonLocal personLocal;
    @Inject
    private LegalPersonProxySession legalPersonProxySession;

    public PaginatedListWrapper<BeneficiaryMetaModel> loadFiBeneficiaries(long fiId, int page, int limit, FiBeneficiaryFilterType filterType, String filterString) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        int offset = PagingUtil.getOffsetFromPage(page, limit);

        long count = fiBeneficiaryLocal.count(fiId);

        List<Beneficiary> beneficiaries = fiBeneficiaryLocal.load(fiId, offset, limit, filterType,filterString);

        return new PaginatedListWrapper<>(BeneficiaryModelHelper.toModels(beneficiaries, langId), limit, count);
    }

    public BeneficiaryMetaModel create(BeneficiaryMetaModel model, long fiId) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Beneficiary beneficiary = fiBeneficiaryLocal.create(BeneficiaryModelHelper.toEntity(model, langId), fiId);
        model.setId(beneficiary.getId());
        return model;
    }

    public BeneficiaryMetaModel update(BeneficiaryMetaModel model, long fiId) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Beneficiary beneficiary = fiBeneficiaryLocal.update(BeneficiaryModelHelper.toEntity(model, langId), fiId);
        return BeneficiaryModelHelper.toModel(beneficiary, langId);
    }

    public void delete(long fiId, long beneficiaryId) {
        fiBeneficiaryLocal.delete(fiId, beneficiaryId);
    }

    public Map<FiBeneficiaryFilterType, Long> countByType(long fiId) {
        return fiBeneficiaryLocal.countByType(fiId);
    }

    public BeneficiaryMetaModel getBeneficiaryById(long beneficiaryId) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Beneficiary fiBeneficiary = fiBeneficiaryLocal.getById(beneficiaryId);
        BeneficiaryMetaModel result = BeneficiaryModelHelper.toModel(fiBeneficiary, langId);

        if (result.getPhysicalPerson() != null) {
            List<ShareMetaModel> personShares = personLocal.getPersonOtherShares(result.getPhysicalPerson().getId()).stream().map(share -> new ShareMetaModel(share.getId(), share.getShare(), share.getCreationDate(), LegalPersonModelHelper.toModel(share.getLegalPerson(), langId))).collect(Collectors.toList());
            result.getPhysicalPerson().setShares(personShares);
        } else if (result.getLegalPerson() != null) {
            LegalPersonMetaModel lp = legalPersonProxySession.getLegalPersonById(result.getLegalPerson().getId());
            result.setLegalPerson(lp);
        }


        return result;
    }
}
