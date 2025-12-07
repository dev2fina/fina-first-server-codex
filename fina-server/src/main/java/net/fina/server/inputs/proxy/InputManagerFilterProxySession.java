package net.fina.server.inputs.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.filter.ReturnDefinitionFilter;
import net.fina.common.client.returns.PeriodTypeModel;
import net.fina.common.client.returns.ReturnTypeModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.messages.MessagesUtil;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.inputs.api.InputManagerLocal;
import net.fina.server.inputs.model.*;
import net.fina.server.inputs.util.ModelUtil;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnTypeLocal;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.INPUT_MANAGER_REVIEW)
public class InputManagerFilterProxySession {
    private final Logger log = Logger.getLogger(InputManagerFilterProxySession.class.getName());


    @EJB
    private FiLocal fiLocal;

    @EJB
    private UserLocal userLocal;

    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;

    @EJB
    private InputManagerLocal inputManagerLocal;

    @EJB
    private ReturnTypeLocal returnTypeLocal;

    @EJB
    private PeriodLocal periodLocal;


    public PaginatedListWrapper<InputManagerFiMetaModel> load(int page, int pageSize, List<Long> idFilter, long langId) {
        List<InputManagerFiMetaModel> models = ModelUtil.getFiModels(idFilter != null && idFilter.size() > 0
                ? inputManagerLocal.loadFisByIds(idFilter) : fiLocal.load(new HashMap<>()), langId);

        int count = models.size();
        PaginatedListWrapper<InputManagerFiMetaModel> result = createFisPagingWrapper(page, pageSize, count, models);

        result.setCurrentPage(page);
        result.setTotalResults(count);

        return result;
    }

    public PaginatedListWrapper<InputManagerFiMetaModel> filterFis(String query, long langId, int page, int pageSize) {
        if (query == null || query.trim().isEmpty()) {
            return load(page, pageSize, new ArrayList<>(), langId);
        }
        List<InputManagerFiMetaModel> fiList = ModelUtil.getFiModels(fiLocal.load(new HashMap<>()), langId);

        List<InputManagerFiMetaModel> tmp = new ArrayList<>();

        for (InputManagerFiMetaModel fi : fiList) {
            if (filterFiModel(fi, query)) {
                tmp.add(fi);
            }
        }
        int count = tmp.size();

        PaginatedListWrapper<InputManagerFiMetaModel> result = createFisPagingWrapper(page, pageSize, count, tmp);

        result.setCurrentPage(page);
        result.setTotalResults(count);

        return result;
    }

    public List<InputManagerFiTypeMetaModel> loadFiTypes(long langId) {
        return ModelUtil.getFiTypeModels(fiLocal.loadFiTypes(), langId);
    }

    public List<InputManagerFiMetaModel> loadFisByType(long typeId, long langId) {
        return ModelUtil.getFiModels(fiLocal.loadFisByTypeId(typeId), langId);
    }

    private PaginatedListWrapper<InputManagerFiMetaModel> createFisPagingWrapper(int page, int pageSize, int count, List<InputManagerFiMetaModel> models) {
        PaginatedListWrapper<InputManagerFiMetaModel> result = new PaginatedListWrapper<>();
        if (page > 0 && pageSize > 0) {
            int start = (page - 1) * pageSize;
            int end = Math.min(page * pageSize, count);
            List<InputManagerFiMetaModel> sublist = new ArrayList<>(pageSize);
            if (start <= count) {
                sublist.addAll(models.subList(start, end));
            }
            result.setList(sublist);
        } else {
            result.setList(models);
        }

        return result;
    }

    private boolean filterFiModel(InputManagerFiMetaModel fiMetaModel, String query) {
        boolean result = false;

        result |= (fiMetaModel.getCode() != null && fiMetaModel.getCode().trim().toLowerCase().contains(query.toLowerCase()));
        result |= (fiMetaModel.getName() != null && fiMetaModel.getName().trim().toLowerCase().contains(query.toLowerCase()));
        result |= (fiMetaModel.getEmail() != null && fiMetaModel.getEmail().trim().toLowerCase().contains(query.toLowerCase()));

        return result;
    }

    public List<InputManagerUserMetaModel> loadUsers(long langId) {
        return ModelUtil.getUserModels(userLocal.loadUsers(false), langId);
    }

    public List<InputManagerReturnDefinitionMetaModel> loadReturnDefinitions(long langId) {
        Map<ReturnDefinitionFilter, Object> filter = new HashMap<>();
        filter.put(ReturnDefinitionFilter.LOAD_All, true);
        return ModelUtil.getReturnDefinitionModels(returnDefinitionLocal.load(filter), langId);
    }

    public List<InputManagerGeneralEnumMetaModel> loadEnum(Class cls) {
        List<InputManagerGeneralEnumMetaModel> models = new ArrayList<>();

        for (Object o : cls.getEnumConstants()) {
            try {
                Enum<?> en = (Enum<?>) o;
                String code = (String) en.getDeclaringClass().getMethod("getCode").invoke(en);
                models.add(new InputManagerGeneralEnumMetaModel(en.ordinal(), en.name(), MessagesUtil.getString(code)));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return models;
    }

    public List<ReturnTypeModel> loadReturnTypes(long langId) {
        return ModelUtil.getReturnTypeModels(returnTypeLocal.loadReturnTypes(), langId);
    }

    public List<PeriodTypeModel> loadPeriodTypes(long langId) {
        return ModelUtil.periodTypeEntitiesToModels(periodLocal.loadPeriodTypes(), langId);
    }
}
