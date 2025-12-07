package net.fina.first.ecm.report.impl;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.report.api.FirstReportingLocal;
import net.fina.first.ecm.report.generator.FirstReportTemplateGenerator;
import net.fina.first.ecm.report.model.FirstReportConfig;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.logging.Logger;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Stateless
@Local(FirstReportingLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class FirstReportingSession implements FirstReportingLocal {


    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Override
    public byte[] generateReport(String languageCode, String templateNodeId, String query, String filter) throws Exception {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser("*", ConfigurationUtil.get().get("ECM.adminUsername"));

        return generate(languageCode, templateNodeId, client, reportName -> client.getWebScriptApi().getReportData(reportName, query, filter));
    }

    @Override
    public byte[] generateFiReport(String languageCode, String templateNodeId, String fiCode) throws Exception {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser("*", ConfigurationUtil.get().get("ECM.adminUsername"));

        return generate(languageCode, templateNodeId, client, reportName -> client.getWebScriptApi().getFiReportData(reportName, languageCode, fiCode));
    }

    private byte[] generate(String languageCode, String templateNodeId, AlfrescoClient client, Function<String, List<Map<String, Object>>> handler) throws Exception {

        Response response = client.getNodesAPI().getNodeContent(templateNodeId, false);

        NodeRepresentation templateNode = client.getNodesAPI().getNodeCall(templateNodeId);

        if (templateNode == null) {
            String message = "Report Template Not Found";
            log.error(message);
            throw new FinATypeException(message);
        }


        String reportName = FirstUtil.getValue(templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_REPORTNAME), String.class);
        Integer startColumn = templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_STARTCOLUMN) != null ? FirstUtil.getValue(templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_STARTCOLUMN), Integer.class) : 0;
        Integer startRow = templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_STARTROW) != null ? FirstUtil.getValue(templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_STARTROW), Integer.class) : 0;
        Boolean enableRowNumbering = templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_ROW_NUMBERING_ENABLE) != null ? FirstUtil.getValue(templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_ROW_NUMBERING_ENABLE), Boolean.class) : true;
        String dateCellAddress = templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_DATE_CELL_ADDRESS) != null ? FirstUtil.getValue(templateNode.getProperties().get(EcmConstants.REPORT_TEMPLATE_PROP_DATE_CELL_ADDRESS), String.class) : null;

        List<Map<String, Object>> data = null;
        try {
            data = handler.apply(reportName);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            log.error("Cannot Generate FIRST Report Response is null! Check alfresco logs");
            return new byte[0];
        }

        for (Map<String, Object> map : data) {

            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (e.getKey().endsWith("Date")) {
                    if (e.getValue() != null && !e.getValue().toString().isEmpty() && !e.getValue().toString().contains("org.mozilla.javascript.Undefined")) {
                        long dateValue = ((Number) ((LinkedHashMap) e.getValue()).get("milliseconds")).longValue();
                        Date d = new Date(dateValue);
                        map.put(e.getKey(), d);
                    }
                }
            }
        }

        FirstReportConfig firstReportConfig = new FirstReportConfig()
                .fileName(reportName)
                .startColumn(startColumn)
                .startRow(startRow)
                .enableRowNumbering(enableRowNumbering)
                .dateCellAddress(dateCellAddress)
                .template(response.readEntity(byte[].class))
                .langCode(languageCode)
                .data(data);

        return new FirstReportTemplateGenerator().generate(firstReportConfig);
    }
}
