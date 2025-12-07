package net.fina.server.feedback.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.feedback.entity.Feedback;
import net.fina.server.feedback.entity.FeedbackCategory;

import java.util.List;

public interface FeedbackLocal  {
    List<Feedback> load(int start, int limit);
    Feedback save(Feedback feedback);
    void delete(long id);
    int getTotal();

    List<FeedbackCategory> getAllCategory();
    FeedbackCategory getCategoryById(long id);
    FeedbackCategory saveCategory(FeedbackCategory feedback);
    void deleteCategory(long id) throws FinATypeException;
}
