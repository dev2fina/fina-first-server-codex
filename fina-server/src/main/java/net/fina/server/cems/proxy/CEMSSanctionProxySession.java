package net.fina.server.cems.proxy;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.KeyValuePair;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.cems.api.CEMSSanctionLocal;
import net.fina.server.cems.entity.sanction.*;
import net.fina.server.cems.model.CEMSSanctionFilterType;
import net.fina.server.cems.model.CEMSSanctionModel;
import net.fina.server.cems.model.helper.CEMSSanctionModelHelper;
import net.fina.server.interceptors.RecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
@Interceptors(RecordingAuditor.class)
public class CEMSSanctionProxySession {
    @Inject
    private CEMSSanctionLocal sanctionLocal;

    public PaginatedListWrapper<CEMSSanctionModel> load(String inspectionId, int offset, int limit, Map<CEMSSanctionFilterType, Object> filter, SortField sortField) {
        String langCode = ThreadLocalHolder.getLanguage().getCode();
        List<CEMSSanction> result = sanctionLocal.load(inspectionId, offset, limit, filter, sortField, false);
        List<CEMSSanctionModel> models = CEMSSanctionModelHelper.toModels(result, langCode);

        long count = sanctionLocal.count(inspectionId, filter);

        return new PaginatedListWrapper<>(models, offset, count);
    }

    public CEMSSanctionModel save(CEMSSanctionModel model, String inspectionId) throws FinATypeException {
        CEMSSanction saveResult = sanctionLocal.save(CEMSSanctionModelHelper.toEntity(model), inspectionId);

        return CEMSSanctionModelHelper.toModel(saveResult, ThreadLocalHolder.getLanguage().getCode());
    }

    public void delete(List<Long> ids) {
        sanctionLocal.delete(ids);
    }

    public CEMSSanctionModel getInspection(long sanctionId) throws FinATypeException {
        return CEMSSanctionModelHelper.toModel(sanctionLocal.findById(sanctionId), ThreadLocalHolder.getLanguage().getCode());
    }

    public List<KeyValuePair<String, String>> loadSanctionMeasures() {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<KeyValuePair<String, String>> data = new ArrayList<>();
        for (CEMSSanctionMeasureInfluence value : sanctionLocal.loadMeasureInfluences()) {
            data.add(new KeyValuePair<>(value.getCode(), value.getDescription().getDescription(langId)));
        }

        return data;
    }

    public List<KeyValuePair<String, String>> loadDecisionMakerBodyCatalog() {
        String langCOde = ThreadLocalHolder.getLanguage().getCode();
        List<KeyValuePair<String, String>> data = new ArrayList<>();
        for (CEMSDecisionMakingBodyCatalog value : CEMSDecisionMakingBodyCatalog.values()) {
            data.add(new KeyValuePair<>(value.name(), MessagesUtil.getString(value.getCode(), langCOde)));
        }

        return data;
    }

    public List<KeyValuePair<String, String>> loadMeasureReasonCatalog() {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<KeyValuePair<String, String>> data = new ArrayList<>();
        for (CEMSSanctionMeasureReasons value : sanctionLocal.loadMeasureReasons()) {
            data.add(new KeyValuePair<>(value.getCode(), value.getDescription().getDescription(langId)));
        }

        return data;
    }

    public List<KeyValuePair<String, String>> loadRegulationListCatalog() {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<KeyValuePair<String, String>> data = new ArrayList<>();
        for (CEMSSanctionRegulationTable value : sanctionLocal.loadRegulations()) {
            data.add(new KeyValuePair<>(value.getCode(), value.getDescription().getDescription(langId)));
        }

        return data;
    }

    public List<KeyValuePair<String, String>> loadSanctionStatusCatalog() {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<KeyValuePair<String, String>> data = new ArrayList<>();
        for (CEMSSanctionStatus value : sanctionLocal.loadStatuses()) {
            data.add(new KeyValuePair<>(value.getCode(), value.getDescription().getDescription(langId)));
        }

        return data;
    }

    public CEMSSanctionModel loadSanctionById(long sanctionId) throws FinATypeException {
        return CEMSSanctionModelHelper.toModel(sanctionLocal.findById(sanctionId), ThreadLocalHolder.getLanguage().getCode());
    }
}
