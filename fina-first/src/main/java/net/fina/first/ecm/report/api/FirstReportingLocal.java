package net.fina.first.ecm.report.api;

public interface FirstReportingLocal {
    byte[] generateReport(String languageCode, String templateNodeId, String query, String filter) throws Exception;

    byte[] generateFiReport(String languageCode, String templateNodeId, String fiCode) throws Exception;
}
