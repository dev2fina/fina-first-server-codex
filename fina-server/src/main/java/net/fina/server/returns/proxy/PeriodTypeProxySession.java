package net.fina.server.returns.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.returns.PeriodTypeModel;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.entity.PeriodType;
import net.fina.server.returns.model.helper.PeriodModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed({PermissionIdNames.FINA_PERIODS_REVIEW})
public class PeriodTypeProxySession {

    @Inject
    private PeriodLocal periodLocal;

    @RolesAllowed({PermissionIdNames.FINA_PERIODS_REVIEW, PermissionIdNames.FINA_RETURNS_SCHEDULE_REVIEW, PermissionIdNames.FINA_REPORT_GENERATE})
    public List<PeriodTypeModel> loadPeriodTypes() {
        return PeriodModelHelper.toModels(periodLocal.loadPeriodTypes(), ThreadLocalHolder.getLanguage().getId());
    }

    @RolesAllowed({PermissionIdNames.FINA_PERIODS_AMEND})
    public PeriodTypeModel create(PeriodTypeModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        PeriodType saved = periodLocal.save(PeriodModelHelper.toEntity(model, langId));
        return PeriodModelHelper.toModel(saved, langId);
    }

    @RolesAllowed({PermissionIdNames.FINA_PERIODS_AMEND, PermissionIdNames.FINA_PERIODS_DELETE})
    public void delete(long periodId) throws FinATypeException {
        periodLocal.deletePeriodType(periodId);
    }
}
