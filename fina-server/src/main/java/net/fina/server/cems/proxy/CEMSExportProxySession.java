package net.fina.server.cems.proxy;

import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.AttachmentModel;
import net.fina.common.shared.SortField;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.cems.api.CEMSInspectionLocal;
import net.fina.server.cems.api.CEMSRecommendationLocal;
import net.fina.server.cems.api.CEMSSanctionLocal;
import net.fina.server.cems.entity.CEMSInspection;
import net.fina.server.cems.entity.CEMSRecommendation;
import net.fina.server.cems.entity.sanction.CEMSSanction;
import net.fina.server.cems.model.CEMSInspectionFilterType;
import net.fina.server.cems.model.CEMSRecommendationFilterType;
import net.fina.server.cems.model.CEMSSanctionFilterType;
import net.fina.server.cems.util.CEMSExportUtil;
import net.fina.server.interceptors.RecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.io.File;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
@Interceptors(RecordingAuditor.class)
public class CEMSExportProxySession {

    @Inject
    private CEMSInspectionLocal inspectionLocal;
    @Inject
    private CEMSRecommendationLocal recommendationLocal;
    @Inject
    private CEMSSanctionLocal sanctionLocal;

    public AttachmentModel exportInspections(Map<CEMSInspectionFilterType, Object> filter, SortField sortField) {
        List<CEMSInspection> inspections = inspectionLocal.loadInspections(-1, -1, filter, sortField);

        File file = new File(ConfigurationUtil.get().get("CEMS.Templates.Inspection.Path"));
        byte[] exportContent = CEMSExportUtil.exportInspections(inspections, file.getAbsolutePath(), ThreadLocalHolder.getLanguage());

        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setFileName(file.getName());
        attachmentModel.setContent(exportContent);

        return attachmentModel;
    }

    public AttachmentModel exportRecommendations(String inspectionId, Map<CEMSRecommendationFilterType, Object> filter, SortField sortField) {
        List<CEMSRecommendation> recommendations = recommendationLocal.loadRecommendations(inspectionId, -1, -1, filter, sortField);
        File file = new File(ConfigurationUtil.get().get("CEMS.Templates.Recommendation.Path"));

        byte[] exportContent = CEMSExportUtil.exportRecommendations(recommendations, file.getAbsolutePath(), ThreadLocalHolder.getLanguage());

        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setFileName("Recommendations.xlsx");
        attachmentModel.setContent(exportContent);

        return attachmentModel;
    }

    public AttachmentModel exportSanctions(String inspectionId, Map<CEMSSanctionFilterType, Object> filter, SortField sortField, List<Long> sanctionIds) {

        List<CEMSSanction> sanctions;
        if (sanctionIds == null || sanctionIds.isEmpty()) {
            sanctions = sanctionLocal.load(inspectionId, -1, -1, filter, sortField, true);
        } else {
            sanctions = sanctionLocal.loadByIds(sanctionIds);
        }

        File file = new File(ConfigurationUtil.get().get("CEMS.Templates.Sanction.Path"));

        byte[] exportContent = CEMSExportUtil.exportSanctions(sanctions, file.getAbsolutePath(), ThreadLocalHolder.getLanguage());

        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setFileName("Sanctions.xlsx");
        attachmentModel.setContent(exportContent);

        return attachmentModel;
    }
}
