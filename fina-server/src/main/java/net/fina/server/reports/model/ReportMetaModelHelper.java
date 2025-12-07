package net.fina.server.reports.model;

import net.fina.server.reports.entity.Report;

import java.util.List;
import java.util.stream.Collectors;

public class ReportMetaModelHelper {
    public static List<ReportMetaModel> toModels(List<Report> reports) {
        return reports.stream().map(report -> new ReportMetaModel().setEntity(report)).collect(Collectors.toList());
    }
}
