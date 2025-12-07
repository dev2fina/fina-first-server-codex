package net.fina.server.faq.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.faq.entity.FaqCategory;
import net.fina.server.faq.entity.FaqItem;

import java.util.List;

public interface FaqLocal {

    List<FaqItem> loadFaqItems(long categoryId, int start, int limit, String sort, String searchValue);

    List<FaqCategory> loadCategories(long parentId);

    FaqCategory saveCategory(FaqCategory faqCategory) throws FinATypeException;

    FaqItem saveFaqItem(FaqItem faqItem);

    void moveFaqItem(long faqItem, boolean moveUp);

    void deleteCategory(long id) throws FinATypeException;

    void deleteFaq(long id);

    int totalFaqByCategory(long categoryId, String searchValue);

    List<FaqItem> loadAllQuestions();
}
