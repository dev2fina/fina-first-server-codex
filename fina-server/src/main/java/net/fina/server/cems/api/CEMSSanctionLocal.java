package net.fina.server.cems.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.server.cems.entity.sanction.*;
import net.fina.server.cems.model.CEMSSanctionFilterType;

import java.util.List;
import java.util.Map;

public interface CEMSSanctionLocal {
    CEMSSanction findById(long sanctionId) throws FinATypeException;

    List<CEMSSanction> load(String inspectionId, int offset, int limit, Map<CEMSSanctionFilterType, Object> filter, SortField sortField, boolean loadAllEntityProps);

    long count(String inspectionId, Map<CEMSSanctionFilterType, Object> filter);

    CEMSSanctionRegulationTable findRegulationByCode(String code);

    List<CEMSSanctionMeasureInfluence> findInfluencesByCodes(List<String> codes);
    CEMSSanctionStatus getStatusByCode(String code);

    List<CEMSSanctionMeasureReasons> findMeasuresByCodes(List<String> codes);
    List<CEMSSanctionRegulationTable> loadRegulations();
    List<CEMSSanctionMeasureInfluence> loadMeasureInfluences();
    List<CEMSSanctionMeasureReasons> loadMeasureReasons();

    List<CEMSSanctionStatus> loadStatuses();

    CEMSSanction save(CEMSSanction sanction, String inspectionId) throws FinATypeException;

    void delete(List<Long> ids);

    List<CEMSSanction> loadByIds(List<Long> sanctionIds);
}
