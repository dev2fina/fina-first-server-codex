package net.fina.first.ecm.fi.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.model.FiTypeMetaModel;

import java.util.List;

public interface FiTypeLocal {
    PaginatedListWrapper<FiTypeMetaModel> loadFiTypes(int start, int pageSize);

    FiTypeMetaModel saveFiType(FiTypeMetaModel model) throws NodeException;

    void deleteFiType(String id) throws NodeException;

    String getAssociatedFiTypeCode(NodeRepresentation registryNode);

    FiTypeMetaModel getFiTypeById(String registryId);

    List<FiTypeMetaModel> loadTypes();
}
