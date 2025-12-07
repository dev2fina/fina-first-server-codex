package net.fina.server.cems.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.server.cems.entity.CEMSRecommendation;
import net.fina.server.cems.entity.CEMSRecommendationStatusHistory;
import net.fina.server.cems.model.CEMSRecommendationFilterType;

import java.util.List;
import java.util.Map;

public interface CEMSRecommendationLocal {
    List<CEMSRecommendation> loadRecommendations(String inspectionId, int offset, int limit, Map<CEMSRecommendationFilterType, Object> filter, SortField sortField);

    long countRecommendations(String inspectionId, Map<CEMSRecommendationFilterType, Object> filter);

    CEMSRecommendation save(String inspectionId, CEMSRecommendation recommendation) throws FinATypeException;

    void delete(List<String> recommendationIds);

    CEMSRecommendation findById(String recommendationId) throws FinATypeException;

    List<CEMSRecommendationStatusHistory> loadStatusHistory(String recommendationId);
}
