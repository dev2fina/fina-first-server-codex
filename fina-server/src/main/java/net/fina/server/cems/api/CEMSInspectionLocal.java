package net.fina.server.cems.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.server.cems.entity.CEMSInspection;
import net.fina.server.cems.model.CEMSInspectionFilterType;

import java.util.List;
import java.util.Map;

public interface CEMSInspectionLocal {
    List<CEMSInspection> loadInspections(int offset, int limit, Map<CEMSInspectionFilterType, Object> filterMap, SortField sortField);

    long countInspections(Map<CEMSInspectionFilterType, Object> filterMap);

    CEMSInspection save(CEMSInspection inspection) throws FinATypeException;

    void delete(List<String> ids) throws FinATypeException;

    CEMSInspection findById(String inspectionId) throws FinATypeException;
}
