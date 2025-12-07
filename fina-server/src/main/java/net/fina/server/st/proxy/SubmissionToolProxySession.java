package net.fina.server.st.proxy;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.st.api.TemplateException;
import net.fina.server.st.api.TemplateLocal;
import net.fina.server.st.crypto.SecurityManagerUtil;
import net.fina.server.st.model.SubmissionToolFileModel;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Stateless
@SecurityDomain("FinASecurityDomain")
@PermitAll
public class SubmissionToolProxySession {
    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private TemplateLocal templateLocal;

    @Inject
    private UserLocal userLocal;

    @Inject
    private FiLocal fiLocal;

    public SubmissionToolFileModel getTemplateWithSignature(String identity, String fiType) throws TemplateException {
        String zipFileName = String.format("Taxonomy.%s.%s.zip", fiType, identity);
        String templateFileName = String.format("Taxonomy.%s.%s.fsop", fiType, identity);

        try {
            byte[] template = templateLocal.loadTemplate(identity, fiType);
            validateTemplate(template);

            byte[] zipContent = createZipWithTemplateAndSignature(templateFileName, template);
            return new SubmissionToolFileModel(zipFileName, zipContent);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new TemplateException("Failed to create template with signature");
        }
    }

    public String getTemplateVersion(String identity, String fiType) {
        return templateLocal.getVersion(identity, fiType);
    }

    @RolesAllowed(PermissionIdNames.DCS_OFFLINETOOL)
    public SubmissionToolFileModel getSubmissionTool() throws IOException, TemplateException {
        String resultFileName = getSubmissionToolFileName();

        if (templateLocal.isSubmissionToolOnlineMode()) {
            byte[] submissionToolContent = templateLocal.loadSubmissionTool(resultFileName);
            return new SubmissionToolFileModel(resultFileName, submissionToolContent);
        }

        return createOfflineSubmissionTool(resultFileName);
    }

    public String getSubmissionToolVersion() throws TemplateException {
        return templateLocal.getOstVersion();
    }


    private byte[] createZipWithTemplateAndSignature(String templateFileName, byte[] templateContent) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(out)) {

            byte[] signature = Base64.getEncoder().encode(SecurityManagerUtil.sign(templateContent));

            addZipEntry(zipOut, templateFileName, templateContent);
            addZipEntry(zipOut, templateFileName + ".sig", signature);

            zipOut.finish();
            return out.toByteArray();
        }
    }

    private void addZipEntry(ZipOutputStream zipOut, String fileName, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zipOut.putNextEntry(entry);
        zipOut.write(content);
        zipOut.closeEntry();
    }

    private String getSubmissionToolFileName() {
        return Optional.ofNullable(ConfigurationUtil.get().get("SubmissionTool.FileName"))
                .filter(name -> !name.isEmpty())
                .map(name -> name.endsWith(".jar") ? name : name + ".jar")
                .orElse("SubmissionTool.jar");
    }

    private SubmissionToolFileModel createOfflineSubmissionTool(String resultFileName) throws IOException, TemplateException {
        String currentUserLogin = userLocal.getCurrentUserLogin();
        List<String> currentUserFiTypes = fiLocal.loadUserFiTypes();
        byte[] submissionToolFileContent = templateLocal.loadSubmissionTool(resultFileName);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(out)) {

            addZipEntry(zipOut, resultFileName, submissionToolFileContent);
            addTemplates(zipOut, currentUserLogin, currentUserFiTypes);

            zipOut.finish();
            return new SubmissionToolFileModel("ost-validator.zip", out.toByteArray());
        }
    }

    private void addTemplates(ZipOutputStream zipOut, String userLogin, List<String> fiTypes) throws IOException, TemplateException {
        HashSet<String> uniqueFiTypes = new HashSet<>(fiTypes);

        for (String fiType : uniqueFiTypes) {
            SubmissionToolFileModel template = getTemplate(userLogin, fiType);
            byte[] templateContent = template.getContent();

            validateTemplate(templateContent);

            byte[] signature = Base64.getEncoder().encode(SecurityManagerUtil.sign(templateContent));

            addZipEntry(zipOut, template.getFileName(), templateContent);
            addZipEntry(zipOut, template.getFileName() + ".sig", signature);
        }
    }

    private SubmissionToolFileModel getTemplate(String identity, String fiType) throws TemplateException {
        String fileName = String.format("Taxonomy.%s.%s.fsop", fiType, identity);
        return new SubmissionToolFileModel(fileName, templateLocal.loadTemplate(identity, fiType));
    }

    private void validateTemplate(byte[] template) throws TemplateException {
        if (template == null || template.length == 0) {
            throw new TemplateException("Template content is empty or null");
        }
    }
}