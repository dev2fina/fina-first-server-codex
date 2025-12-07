package net.fina.server.reports.model;

import net.fina.report.core.ReportUtil;
import net.fina.reporting.model.ReportInfoModelHelper;
import net.fina.reporting.model.ReportMetaModel;
import net.fina.server.reports.entity.Report;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class ReportingMetaModelHelper {
    private static final Logger log = Logger.getLogger(ReportingMetaModelHelper.class.getName());

    public static ReportMetaModel toModel(Report report, long langId) {

        ReportMetaModel result = new ReportMetaModel();

        try {

            if (report != null) {
                result.setId(report.getId());
                result.setId(report.getId());
                result.setParentId(report.getParentId());
                result.setDescription(report.getDescription().getDescription(langId));
                result.setNameStrId(report.getDescription().getNameStrId());
                result.setCode(report.getCode());
                result.setType(report.getType());
                result.setLangId(langId);
                result.setReportType(report.getReportType());
                result.setSequence(report.getSequence());
                result.setVersion(report.getVersion());
                if (report.getInfo() != null) {
                    result.setInfo(ReportInfoModelHelper.get(ReportUtil.getReportInfo(report.getInfo())));
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return result;
    }


    public static List<ReportMetaModel> toModels(List<Report> reports, long langId) {
        List<ReportMetaModel> resultList = new ArrayList<>();
        if (reports != null) {
            reports.forEach(report -> resultList.add(toModel(report, langId)));
        }

        return resultList;
    }
}
