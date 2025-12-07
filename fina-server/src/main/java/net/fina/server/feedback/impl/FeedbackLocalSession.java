package net.fina.server.feedback.impl;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.feedback.api.FeedbackLocal;
import net.fina.server.feedback.entity.Feedback;
import net.fina.server.feedback.entity.FeedbackCategory;
import net.fina.server.interceptors.RecordingAuditor;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;


@Stateless
@Local(FeedbackLocal.class)
@Interceptors(RecordingAuditor.class)
public class FeedbackLocalSession implements FeedbackLocal {
    @Inject
    private EntityManager em;

    @Override
    public List<Feedback> load(int start, int limit) {
        TypedQuery<Feedback> query = em.createQuery("select f from IN_FEEDBACK f", Feedback.class);

        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public Feedback save(Feedback feedback) {
        if (feedback.getFeedbackCategory() != null) {
            feedback.setFeedbackCategory(this.getCategoryById(feedback.getFeedbackCategory().getId()));
        }
        return em.merge(feedback);
    }

    @Override
    public void delete(long id) {
        em.createQuery("delete from IN_FEEDBACK f where f.id=:id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public int getTotal() {
        return em.createQuery("select count(f) from IN_FEEDBACK f", Long.class)
                .getSingleResult()
                .intValue();
    }

    @Override
    public List<FeedbackCategory> getAllCategory() {
        return em.createQuery("select c from IN_FEEDBACK_CATEGORY c", FeedbackCategory.class)
                .getResultList();
    }

    @Override
    public FeedbackCategory getCategoryById(long id) {
        return em.createQuery("select c from IN_FEEDBACK_CATEGORY c where c.id=:id", FeedbackCategory.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public FeedbackCategory saveCategory(FeedbackCategory feedback) {
        if (feedback.getId() > 0) {
            feedback = em.merge(feedback);
        } else {
            feedback.setId(0);
            em.persist(feedback);
        }
        return feedback;
    }

    @Override
    public void deleteCategory(long id) throws FinATypeException {

        List<Long> feedbackIds = em.createQuery("select f.id from IN_FEEDBACK f where f.feedbackCategory.id=:id ", Long.class)
                .setParameter("id", id)
                .getResultList();

        if (!feedbackIds.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Category Has Feedbacks"});
        }

        em.createQuery("delete from IN_FEEDBACK_CATEGORY c where c.id=:id")
                .setParameter("id", id)
                .executeUpdate();
    }

}
