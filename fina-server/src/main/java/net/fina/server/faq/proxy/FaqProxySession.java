package net.fina.server.faq.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.faq.api.FaqLocal;
import net.fina.server.faq.entity.FaqItem;
import net.fina.server.faq.model.FaqCategoryMetaHelper;
import net.fina.server.faq.model.FaqCategoryMetaModel;
import net.fina.server.faq.model.FaqItemMetaModel;
import net.fina.server.faq.model.FaqItemModelHelper;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class FaqProxySession {

    @EJB
    private FaqLocal faqLocal;

    @Inject
    private LanguageLocal languageLocal;

    @RolesAllowed(PermissionIdNames.FAQ_REVIEW)
    public PaginatedListWrapper<FaqItemMetaModel> loadFaqItems(long categoryId, int start, int limit, long langId, String sort, String searchValue) {

        PaginatedListWrapper<FaqItemMetaModel> result = new PaginatedListWrapper<>();

        List<FaqItemMetaModel> faqItems = FaqItemModelHelper.getFaqModels(faqLocal.loadFaqItems(categoryId, PagingUtil.getOffsetFromPage(start, limit), limit, sort, searchValue), langId);

        result.setList(faqItems);
        result.setTotalResults(faqLocal.totalFaqByCategory(categoryId, searchValue));

        return result;
    }

    @RolesAllowed(PermissionIdNames.FAQ_REVIEW)
    public List<FaqCategoryMetaModel> loadFaqCategoriesByParentId(long parentId, long langId) {
        return FaqCategoryMetaHelper.getCategoryModels(faqLocal.loadCategories(parentId), langId);
    }

    @RolesAllowed(PermissionIdNames.FAQ_REVIEW)
    public List<FaqCategoryMetaModel> loadFaqCategoriesByParentId(long parentId, String locale) {
        Language lang = languageLocal.getLanguageByCode(locale);
        return FaqCategoryMetaHelper.getCategoryModels(faqLocal.loadCategories(parentId), lang.getId());
    }

    @RolesAllowed(PermissionIdNames.FAQ_AMEND)
    public List<FaqCategoryMetaModel> saveCategory(FaqCategoryMetaModel model, long langId) throws FinATypeException {
        return Collections.singletonList(FaqCategoryMetaHelper.get(faqLocal.saveCategory(FaqCategoryMetaHelper.get(model, langId)), langId));
    }

    @RolesAllowed(PermissionIdNames.FAQ_AMEND)
    public void deleteCategory(long categoryId) throws FinATypeException {
        faqLocal.deleteCategory(categoryId);
    }

    @RolesAllowed(PermissionIdNames.FAQ_AMEND)
    public FaqItemMetaModel saveFaqItem(FaqItemMetaModel model, long langId) {
        FaqItem saved = faqLocal.saveFaqItem(FaqItemModelHelper.get(model, langId));
        return FaqItemModelHelper.get(saved, langId);
    }

    @RolesAllowed(PermissionIdNames.FAQ_AMEND)
    public void deleteFaq(long faqId) {
        faqLocal.deleteFaq(faqId);
    }

    @RolesAllowed(PermissionIdNames.FAQ_REVIEW)
    public List<FaqItemMetaModel> loadQuestions(long categoryId, String langCode) {
        long langId = languageLocal.getLanguageByCode(langCode).getId();
        PaginatedListWrapper<FaqItemMetaModel> faqItems = loadFaqItems(categoryId, 0, 1000, langId, null, null);

        return faqItems.getList();
    }

    @RolesAllowed(PermissionIdNames.FAQ_REVIEW)
    public List<FaqItemMetaModel> searchQuestions(String query, String langCode) {
        long langId = languageLocal.getLanguageByCode(langCode).getId();
        List<FaqItem> filtered = filterFAQ(langId, query);
        return FaqItemModelHelper.getFaqModels(filtered, langId);
    }


    private List<FaqItem> filterFAQ(long langId, String query) {
        final String searchQuery = query.toLowerCase();
        List<FaqItem> faqItems = faqLocal.loadAllQuestions();
        return faqItems.stream().filter(faqItem -> faqItem.getQuestion().getDescription(langId).toLowerCase().contains(searchQuery) ||
                        faqItem.getAnswer().getDescription(langId).toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());

    }

    public void moveFaqItem(long faqItem, boolean moveUp) {
        faqLocal.moveFaqItem(faqItem, moveUp);
    }
}
