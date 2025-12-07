package net.fina.server.cems.proxy;


import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.server.cems.api.CEMSRecommendationLocal;
import net.fina.server.cems.entity.CEMSRecommendation;
import net.fina.server.cems.entity.CEMSRecommendationStatusHistory;
import net.fina.server.cems.model.CEMSRecommendationFilterType;
import net.fina.server.cems.model.CEMSRecommendationModel;
import net.fina.server.cems.model.CEMSRecommendationStatusHistoryModel;
import net.fina.server.cems.model.helper.CEMSRecommendationModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class CEMSRecommendationProxySession {

    @Inject
    private CEMSRecommendationLocal recommendationLocal;

    public PaginatedListWrapper<CEMSRecommendationModel> load(String inspectionId, int offset, int limit, Map<CEMSRecommendationFilterType, Object> filter, SortField sortField) {
        List<CEMSRecommendation> result = recommendationLocal.loadRecommendations(inspectionId, offset, limit, filter, sortField);
        List<CEMSRecommendationModel> models = CEMSRecommendationModelHelper.toModels(result);

        long count = recommendationLocal.countRecommendations(inspectionId, filter);

        return new PaginatedListWrapper<>(models, offset, count);
    }

    public CEMSRecommendationModel save(String inspectionId, CEMSRecommendationModel model) throws FinATypeException {
        if (model.getStatus() == null) {
            throw new FinATypeException("Status is required");
        }

        CEMSRecommendation saved = recommendationLocal.save(inspectionId, CEMSRecommendationModelHelper.toEntity(model));

        return CEMSRecommendationModelHelper.toModel(saved);
    }

    public void delete(List<String> recommendationIds) {
        recommendationLocal.delete(recommendationIds);
    }

    public CEMSRecommendationModel getRecommendation(String recommendationId) throws FinATypeException {
        return CEMSRecommendationModelHelper.toModel(recommendationLocal.findById(recommendationId));
    }

    public List<CEMSRecommendationStatusHistoryModel> loadStatusHistory(String recommendationId) {
        List<CEMSRecommendationStatusHistory> history = recommendationLocal.loadStatusHistory(recommendationId);
        return CEMSRecommendationModelHelper.toStatusModels(history);
    }
}
