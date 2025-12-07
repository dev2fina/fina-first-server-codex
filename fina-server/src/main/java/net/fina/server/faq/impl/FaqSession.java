package net.fina.server.faq.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.faq.api.FaqLocal;
import net.fina.server.faq.entity.FaqCategory;
import net.fina.server.faq.entity.FaqItem;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.UserLocal;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@Stateless
@Local(FaqLocal.class)
@Interceptors(RecordingAuditor.class)
public class FaqSession implements FaqLocal {
    private final Logger log = Logger.getLogger(FaqSession.class);
    @Inject
    private EntityManager em;
    @Inject
    private UserLocal userLocal;

    @Override
    public List<FaqItem> loadFaqItems(long categoryId, int start, int limit, String sort, String searchValue) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        if (categoryId < 0) categoryId = 0;
        String sortString = "";
        SortField sortField = sortFieldFromSort(sort);

        if (sortField != null) {
            sortString = " order by f." + sortField.getProperty() + " " + sortField.getDirection();
        }

        Query query;
        if (categoryId > 0) {
            List<Long> childCategoryIds = getChildrenCategoryIds(categoryId);
            query = em.createQuery("select f from FaqItem f where f.category.id in (:categoryIds) " + sortString, FaqItem.class)
                    .setParameter("categoryIds", childCategoryIds);
        } else {
            if (searchValue != null && !searchValue.isBlank()) {
                query = em.createNativeQuery(
                                "select f.* from IN_FAQ_ITEMS f " +
                                        "inner join SYS_STRINGS ssq on ssq.ID = f.QUESTIONSTRID and ssq.LANGID = :langId " +
                                        "inner join SYS_STRINGS ssa on ssa.ID = f.ANSWERSTRID and ssa.LANGID = :langId " +
                                        "where lower(ssq.VALUE) like :searchValue or lower(ssa.VALUE) like :searchValue", FaqItem.class)
                        .setParameter("searchValue", "%" + searchValue.toLowerCase() + "%")
                        .setParameter("langId", langId);
            } else {
                query = em.createQuery("select f from FaqItem f " + sortString, FaqItem.class);
            }
        }

        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }


    @Override
    public List<FaqCategory> loadCategories(long parentId) {
        if (parentId < 0) parentId = 0;
        TypedQuery<FaqCategory> query = em.createQuery("select f from FaqCategory f where f.parentId=:parentId", FaqCategory.class)
                .setParameter("parentId", parentId);
        return query.getResultList();
    }


    @Override
    public FaqCategory saveCategory(FaqCategory faqCategory) throws FinATypeException {
        if (faqCategory.getName().getDescription(ThreadLocalHolder.getLanguage().getId()).trim().isEmpty()) {
            throw new FinATypeException("Category name can not be empty");
        }
        if (faqCategory.getId() > 0) {
            FaqCategory exiting = em.find(FaqCategory.class, faqCategory.getId());
            if (exiting.isLeaf() && !faqCategory.isLeaf()) {
                boolean hasItems = !em.createQuery("select fi.id from FaqItem fi where fi.category.id=:categoryId")
                        .setParameter("categoryId", faqCategory.getId())
                        .getResultList().isEmpty();

                if (hasItems) {
                    throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Leaf Items Is Not Empty, cannot change from leaf to folder...");
                }
            }
            if (!exiting.isLeaf() && faqCategory.isLeaf()) {
                boolean hasChildren = !em.createQuery("select c from FaqCategory c where c.parentId=:id")
                        .setParameter("id", exiting.getId())
                        .getResultList()
                        .isEmpty();
                if (hasChildren) {
                    throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Category Is Not Empty, cannot change from folder to leaf...");
                }
            }
            faqCategory = em.merge(faqCategory);
        } else {
            if (faqCategory.getParentId() < 0) faqCategory.setParentId(0);
            em.persist(faqCategory);
        }
        return faqCategory;
    }

    @Override
    public FaqItem saveFaqItem(FaqItem faqItem) {
        faqItem.setUser(userLocal.getCurrentUser());

        if (faqItem.getId() > 0) {
            faqItem = em.merge(faqItem);
        } else {
            int maxItemSequence = getMaxFaqItemSequenceByCategory(faqItem.getCategory().getId());
            faqItem.setSequence(maxItemSequence + 1);
            em.persist(faqItem);
        }
        return faqItem;
    }

    @Override
    public void deleteCategory(long id) throws FinATypeException {
        FaqCategory faqCategory = em.find(FaqCategory.class, id);
        if (faqCategory != null) {
            List<Long> itemIds = em.createQuery("select i.id from FaqItem i where i.category.id=:categoryId", Long.class).setParameter("categoryId", id).getResultList();
            List<Long> childrenIds = em.createQuery("select fc.id from FaqCategory fc where fc.parentId=:id", Long.class).setParameter("id", id).getResultList();

            if (!itemIds.isEmpty() || !childrenIds.isEmpty()) {
                throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
            }
            //delete FAQ items in category
            em.createQuery("delete from FaqItem f where f.category.id=:categoryId").setParameter("categoryId", id).executeUpdate();
            em.remove(faqCategory);
        }
    }

    @Override
    public void deleteFaq(long id) {
        FaqItem faqItem = em.find(FaqItem.class, id);
        if (faqItem != null) {
            long faqItemCategoryId = faqItem.getCategory().getId();
            int faqItemSequence = faqItem.getSequence();

            em.remove(faqItem);
            updateFaqItemSequenceAfterDelete(faqItemCategoryId, faqItemSequence);
        }
    }

    @Override
    public int totalFaqByCategory(long categoryId, String searchValue) {
        List<Long> childCategoryIds = getChildrenCategoryIds(categoryId);
        long langId = ThreadLocalHolder.getLanguage().getId();

        Query query;
        if (categoryId > 0) {
            query = em.createQuery(
                            "select count(f) from FaqItem f where f.category.id in (:categoryIds)", Long.class)
                    .setParameter("categoryIds", childCategoryIds);
        } else {
            if (searchValue != null && !searchValue.isBlank()) {
                query = em.createNativeQuery(
                                "select count(f.id) from IN_FAQ_ITEMS f " +
                                        "inner join SYS_STRINGS ssq on ssq.ID = f.QUESTIONSTRID and ssq.LANGID = :langId " +
                                        "inner join SYS_STRINGS ssa on ssa.ID = f.ANSWERSTRID and ssa.LANGID = :langId " +
                                        "where lower(ssq.VALUE) like :searchValue or lower(ssa.VALUE) like :searchValue", Long.class)
                        .setParameter("searchValue", "%" + searchValue.toLowerCase() + "%")
                        .setParameter("langId", langId);
            } else {
                query = em.createQuery("select count(f) from FaqItem f", Long.class);
            }
        }

        return ((Long) query.getSingleResult()).intValue();
    }


    @Override
    public List<FaqItem> loadAllQuestions() {
        return em.createQuery("select f from FaqItem f", FaqItem.class).getResultList();
    }

    public void moveFaqItem(long faqItemId, boolean moveUp) {
        FaqItem faqItem = em.find(FaqItem.class, faqItemId);
        if (faqItem == null) {
            return;
        }

        List<FaqItem> categoryItems = em.createQuery("select fi from FaqItem fi where fi.category.id = :categoryId order by fi.sequence ", FaqItem.class)
                .setParameter("categoryId", faqItem.getCategory().getId())
                .getResultList();


        int targetItemIndex = moveUp ? categoryItems.indexOf(faqItem) - 1 : categoryItems.indexOf(faqItem) + 1;
        if (targetItemIndex < 0 || targetItemIndex >= categoryItems.size()) {
            return;
        }

        int currSequence = faqItem.getSequence();
        FaqItem toSwap = categoryItems.get(targetItemIndex);

        faqItem.setSequence(toSwap.getSequence());
        toSwap.setSequence(currSequence);

    }

    private SortField sortFieldFromSort(String sort) {
        try {
            if (sort != null && !sort.trim().isEmpty()) {
                sort = sort.substring(1, sort.length() - 1);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(sort, SortField.class);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    private int getMaxFaqItemSequenceByCategory(long categoryId) {
        TypedQuery<Integer> typedQuery = em.createQuery("select max(f.sequence) from FaqItem f where f.category.id=:categoryId", Integer.class)
                .setParameter("categoryId", categoryId);

        Integer result = typedQuery.getSingleResult();
        return result != null ? result : 0;
    }

    private void updateFaqItemSequenceAfterDelete(long categoryId, int currentSequence) {
        List<FaqItem> faqItems = em.createQuery("select f from FaqItem f where f.sequence >:sequence and f.category.id=:categoryId", FaqItem.class)
                .setParameter("sequence", currentSequence)
                .setParameter("categoryId", categoryId).getResultList();

        for (FaqItem faqItem : faqItems) {
            int faqItemSequence = faqItem.getSequence();
            faqItem.setSequence(faqItemSequence - 1);
        }
    }

    private List<Long> getChildrenCategoryIds(long categoryId) {
        List<Long> result = new ArrayList<>();
        result.add(categoryId);
        loadChildCategoryIdsRecursive(categoryId, result);
        return result;
    }

    private void loadChildCategoryIdsRecursive(long categoryId, List<Long> result) {
        List<FaqCategory> categories = loadCategories(categoryId);

        for (FaqCategory category : categories) {
            result.add(category.getId());
            loadChildCategoryIdsRecursive(category.getId(), result);
        }
    }
}
