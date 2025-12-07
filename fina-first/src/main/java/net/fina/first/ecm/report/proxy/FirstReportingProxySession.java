package net.fina.first.ecm.report.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.first.ecm.report.api.FirstReportingLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class FirstReportingProxySession {

    @Inject
    private FirstReportingLocal firstReportingLocal;

    public byte[] generateReport(String languageCode, String templateId, String fiCode, String query, String filter) throws Exception {
        if (fiCode != null && !fiCode.trim().isEmpty()) {
            return firstReportingLocal.generateFiReport(languageCode, templateId, fiCode);
        }
        return firstReportingLocal.generateReport(languageCode,templateId, query, filter);
    }
}
