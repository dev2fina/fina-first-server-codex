package net.fina.server.feedback.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.feedback.api.FeedbackLocal;
import net.fina.server.feedback.model.FeedbackCategoryMetaModel;
import net.fina.server.feedback.model.FeedbackCategoryMetaModelHelper;
import net.fina.server.feedback.model.FeedbackMetaModel;
import net.fina.server.feedback.model.FeedbackMetaModelHelper;
import net.fina.server.i18n.api.LanguageLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
@PermitAll
public class FeedbackProxySession {

    @Inject
    private FeedbackLocal feedbackLocal;

    @Inject
    private LanguageLocal languageLocal;

    @RolesAllowed(PermissionIdNames.FEEDBACK_REVIEW)
    public PaginatedListWrapper<FeedbackMetaModel> load(int start, int limit, long langId) {
        PaginatedListWrapper<FeedbackMetaModel> result = new PaginatedListWrapper<>();
        List<FeedbackMetaModel> feedbackModels = FeedbackMetaModelHelper.toModel(feedbackLocal.load(PagingUtil.getOffsetFromPage(start, limit), limit), langId);

        result.setList(feedbackModels);
        result.setTotalResults(feedbackLocal.getTotal());
        return result;
    }

    @RolesAllowed(PermissionIdNames.FEEDBACK_REVIEW)
    public PaginatedListWrapper<FeedbackMetaModel> load(int start, int limit, String langCode) {
        long langId = languageLocal.getLanguageByCode(langCode).getId();
        return this.load(start, limit, langId);
    }

    public FeedbackMetaModel save(FeedbackMetaModel feedback, long langId) throws FinATypeException {
        if(feedback.getFeedbackCategory() == null || feedback.getFeedbackCategory().getId() <=0) {
            throw new FinATypeException(FinATypeException.Type.PROPERTY_MISSING_VALUE);
        }

        return FeedbackMetaModelHelper.toModel(
                feedbackLocal.save(
                        FeedbackMetaModelHelper.toEntity(feedback, langId)), langId);
    }

    public FeedbackMetaModel save(FeedbackMetaModel feedback, String langCode) throws FinATypeException {
        long langId = languageLocal.getLanguageByCode(langCode).getId();
        return this.save(feedback, langId);
    }

    @RolesAllowed(PermissionIdNames.FEEDBACK_AMEND)
    public void delete(long id) {
        feedbackLocal.delete(id);
    }

    public List<FeedbackCategoryMetaModel> getAllCategory(long langId) {
        return feedbackLocal.getAllCategory().stream()
                .map((v) -> FeedbackCategoryMetaModelHelper.toModel(v, langId))
                .collect(Collectors.toList());
    }

    public List<FeedbackCategoryMetaModel> getAllCategory(String langCode) {
        long langId = languageLocal.getLanguageByCode(langCode).getId();
        return this.getAllCategory(langId);
    }

    @RolesAllowed(PermissionIdNames.FEEDBACK_CATEGORY_AMEND)
    public FeedbackCategoryMetaModel saveCategory(FeedbackCategoryMetaModel feedbackCategory, long langId){
        return FeedbackCategoryMetaModelHelper.toModel(
                feedbackLocal.saveCategory(
                        FeedbackCategoryMetaModelHelper.toEntity(feedbackCategory, langId)), langId);
    }

    @RolesAllowed(PermissionIdNames.FEEDBACK_CATEGORY_AMEND)
    public FeedbackCategoryMetaModel saveCategory(FeedbackCategoryMetaModel feedbackCategory, String langCode){
        long langId = languageLocal.getLanguageByCode(langCode).getId();
        return this.saveCategory(feedbackCategory, langId);
    }

    @RolesAllowed(PermissionIdNames.FEEDBACK_CATEGORY_AMEND)
    public void deleteCategory(long id) throws FinATypeException {
        feedbackLocal.deleteCategory(id);
    }
}
