package net.fina.server.cems.proxy;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.cems.api.CEMSInspectionLocal;
import net.fina.server.cems.entity.CEMSInspection;
import net.fina.server.cems.model.CEMSInspectionFilterType;
import net.fina.server.cems.model.CEMSInspectionModel;
import net.fina.server.cems.model.helper.CEMSInspectionModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class CEMSInspectionProxySession {

    @Inject
    private CEMSInspectionLocal inspectionLocal;

    public PaginatedListWrapper<CEMSInspectionModel> load(int offset, int limit, Map<CEMSInspectionFilterType, Object> filter, SortField sortField) {
        List<CEMSInspection> result = inspectionLocal.loadInspections(offset, limit, filter, sortField);
        List<CEMSInspectionModel> models = CEMSInspectionModelHelper.toModels(result, ThreadLocalHolder.getLanguage().getId());

        long count = inspectionLocal.countInspections(filter);

        return new PaginatedListWrapper<>(models, offset, count);
    }

    public CEMSInspectionModel save(CEMSInspectionModel model) throws FinATypeException {
        CEMSInspection saveResult = inspectionLocal.save(CEMSInspectionModelHelper.toEntity(model));

        return CEMSInspectionModelHelper.toModel(saveResult, ThreadLocalHolder.getLanguage().getId());
    }

    public void delete(List<String> ids) throws FinATypeException {
        inspectionLocal.delete(ids);
    }

    public CEMSInspectionModel getInspection(String inspectionId) throws FinATypeException {
        return CEMSInspectionModelHelper.toModel(inspectionLocal.findById(inspectionId), ThreadLocalHolder.getLanguage().getId());
    }
}
