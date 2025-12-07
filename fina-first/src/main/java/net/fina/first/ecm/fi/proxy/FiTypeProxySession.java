package net.fina.first.ecm.fi.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.api.FiTypeLocal;
import net.fina.first.ecm.fi.model.FiTypeMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
public class FiTypeProxySession {

    @Inject
    private FiTypeLocal fiTypeLocal;

    public PaginatedListWrapper<FiTypeMetaModel> loadFiTypes(int start, int pageSize) {
        return fiTypeLocal.loadFiTypes(start, pageSize);
    }

    public List<FiTypeMetaModel> loadTypes() {
        return fiTypeLocal.loadTypes();
    }

    public FiTypeMetaModel saveFiType(FiTypeMetaModel model) throws NodeException {
        return fiTypeLocal.saveFiType(model);
    }

    public void deleteFiType(String id) throws NodeException {
        fiTypeLocal.deleteFiType(id);
    }

    public String getAssociatedFiTypeCode(NodeRepresentation registryNode) {
        return fiTypeLocal.getAssociatedFiTypeCode(registryNode);
    }

    public FiTypeMetaModel getFiTypeById(String registryId) {
        return fiTypeLocal.getFiTypeById(registryId);
    }
}
