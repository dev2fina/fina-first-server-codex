package net.fina.server.fi.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.FiBranchFilter;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiHistoryLocal;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.*;
import net.fina.server.fi.model.EntityHistoryModel;
import net.fina.server.fi.model.FiBranchMetaModel;
import net.fina.server.fi.model.FiBranchMetaModelHelper;
import net.fina.server.person.api.PersonLocal;
import net.fina.server.person.entity.Person;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.FINA_BANK_REVIEW)
public class FiBranchProxySession {
    @Inject
    private FiLocal fiLocal;
    @Inject
    private FiHistoryLocal fiHistoryLocal;

    @Inject
    private PersonLocal personLocal;


    public PaginatedListWrapper<FiBranchMetaModel> loadByType(long fiId, int page, int limit, long fiBranchTypeId, Map<FiBranchFilter, Object> filter) {
        long count = fiLocal.countBranches(fiId, fiBranchTypeId, filter);
        List<FiBranch> branches = fiLocal.loadFiBranchesByType(fiId, PagingUtil.getOffsetFromPage(page, limit), limit, fiBranchTypeId, filter);
        return toModel(page, branches, count);
    }

    public PaginatedListWrapper<FiBranchMetaModel> load(long fiId, int page, int limit) {
        long count = fiLocal.countBranches(fiId);
        List<FiBranch> branches = fiLocal.loadFiBranchs(fiId, PagingUtil.getOffsetFromPage(page, limit), limit);
        return toModel(page, branches, count);
    }

    private PaginatedListWrapper<FiBranchMetaModel> toModel(int page, List<FiBranch> branches, long count) {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        List<FiBranchMetaModel> models = FiBranchMetaModelHelper.toModels(branches, lang.getId());

        PaginatedListWrapper<FiBranchMetaModel> result = new PaginatedListWrapper<>();
        result.setList(models);
        result.setTotalResults(count);
        result.setCurrentPage(page);

        return result;
    }


    public FiBranchMetaModel getById(long id) {
        FiBranch fiBranch = fiLocal.findFiBranchById(id);
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        return FiBranchMetaModelHelper.toModel(fiBranch, lang.getId());
    }

    @Transactional(rollbackOn = FinATypeException.class)
    public FiBranchMetaModel save(FiBranchMetaModel fiBranchModel) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();

        Fi fi = fiLocal.getFiById(fiBranchModel.getBankId());

        FiPerson manager = null;
        FiPerson chiefAccountant = null;
        FiBranch existingBranch;

        if (fiBranchModel.getId() > 0) {
            existingBranch = fiLocal.getFiBranchById(fiBranchModel.getId());
            if (existingBranch.getChiefAccountant() != null) {
                Optional<FiPersonConnection> chiefAccountantConnection = existingBranch.getChiefAccountant().getConnections().stream().filter(c -> c.getConnectionType() == FiPersonConnectionType.BRANCH_CHIEF_ACCOUNTANT).findFirst();
                chiefAccountantConnection.ifPresent(fiPersonConnection -> existingBranch.getChiefAccountant().getConnections().remove(fiPersonConnection));
            }

            if (existingBranch.getManager() != null) {
                Optional<FiPersonConnection> managerConnection = existingBranch.getManager().getConnections().stream().filter(c -> c.getConnectionType() == FiPersonConnectionType.BRANCH_MANAGER).findFirst();
                managerConnection.ifPresent(fiPersonConnection -> existingBranch.getManager().getConnections().remove(fiPersonConnection));
            }
        }

        // manager
        if (fiBranchModel.getManager() != null) {
            Person person = personLocal.getPersonInfo(fiBranchModel.getManager().getId());
            manager = personLocal.createOrUpdateFiPersonRelation(fi.getId(), person.getId(), FiPersonConnectionType.BRANCH_MANAGER);
        }

        // chief accountant
        if (fiBranchModel.getChiefAccountant() != null) {
            Person person = personLocal.getPersonInfo(fiBranchModel.getChiefAccountant().getId());
            chiefAccountant = personLocal.createOrUpdateFiPersonRelation(fi.getId(), person.getId(), FiPersonConnectionType.BRANCH_CHIEF_ACCOUNTANT);
        }

        FiBranch fiBranch = FiBranchMetaModelHelper.toEntity(fiBranchModel, manager, chiefAccountant, langId);
        return FiBranchMetaModelHelper.toModel(fiLocal.saveFiBranch(fiBranch, langId, false), langId);
    }

    public void delete(long id) throws FinATypeException {
        fiLocal.deleteFiBranch(id);
    }

    public void deleteBranches(long fiId, List<Long> branchIds) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);

        for (Long branchId : branchIds) {
            delete(branchId);
        }
    }

    public PaginatedListWrapper<EntityHistoryModel<FiBranchMetaModel>> loadFiBranchHistory(long fiId, int offset, int limit) {
        List<EntityHistoryModel<FiBranchMetaModel>> result = fiHistoryLocal.loadFiBranchHistory(fiId, offset, limit);
        PaginatedListWrapper<EntityHistoryModel<FiBranchMetaModel>> paging = new PaginatedListWrapper<>();
        paging.setList(result);
        paging.setPageSize(limit);
        paging.setTotalResults(fiHistoryLocal.countFiBranchesHistory(fiId));

        return paging;
    }

    public PaginatedListWrapper<EntityHistoryModel<FiBranchMetaModel>> loadBranchHistory(long branchId, int offset, int limit) {
        List<EntityHistoryModel<FiBranchMetaModel>> result = fiHistoryLocal.loadBranchHistory(branchId, offset, limit);
        PaginatedListWrapper<EntityHistoryModel<FiBranchMetaModel>> paging = new PaginatedListWrapper<>();
        paging.setList(result);
        paging.setPageSize(limit);
        paging.setTotalResults(fiHistoryLocal.countBranchHistory(branchId));


        return paging;
    }


}
