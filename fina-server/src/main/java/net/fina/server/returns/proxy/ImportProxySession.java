package net.fina.server.returns.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.dcs.UploadType;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ImportFilter;
import net.fina.common.client.returns.ImportModel;
import net.fina.common.shared.AttachmentModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dcs.uploadfile.api.UploadFileLocal;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.returns.api.ImportLocal;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class ImportProxySession {

    @Inject
    private ImportLocal importLocal;
    @Inject
    private UploadFileLocal uploadFileLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private UserLocal userLocal;

    @RolesAllowed(PermissionIdNames.FINA_IMPORT_REVIEW)
    public PaginatedListWrapper<ImportModel> loadPackages(Map<ImportFilter, Object> filterMap) {
        List<ImportModel> result;

        List<String> fiCodes = (List<String>) filterMap.getOrDefault(ImportFilter.FI_CODES, new ArrayList<>());

        result = importLocal.loadPackagesV2(filterMap, fiCodes);

        return new PaginatedListWrapper<>(result, (Integer) filterMap.getOrDefault(ImportFilter.LIMIT, 0), 0);
    }

    @RolesAllowed(PermissionIdNames.FINA_IMPORT_REVIEW)
    public List<ImportModel> load(Map<ImportFilter, Object> filterMap) {
        return importLocal.load(filterMap);
    }

    public String getImportMessage(long importedReturnId) {
        return importLocal.getImportMessage(importedReturnId);
    }

    @RolesAllowed(PermissionIdNames.FINA_IMPORT_REVIEW)
    public String loadReturnXML(int importedReturnId) {
        return importLocal.getReturnXML(importedReturnId);
    }

    public UploadFile downloadImportedFile(long fileId) throws FinATypeException {
        return uploadFileLocal.loadUploadFileContent(fileId);
    }


    public byte[] getImportedReturnXml(int returnId, String dataTypeParameter) {
        switch (dataTypeParameter) {
            case "content":
                return loadReturnXML(returnId).getBytes(StandardCharsets.UTF_8);
            case "header":
                return getImportdReturnHeader(importLocal.findById(returnId)).getBytes(Charset.forName("UTF-8"));
        }

        return " ".getBytes();
    }

    private String getImportdReturnHeader(ImportedReturn importedReturn) {
        StringBuilder sb = new StringBuilder()
                .append("   ")
                .append(importedReturn.getBankCode())
                .append(" -> ")
                .append(importedReturn.getReturnCode())
                .append(" -> ")
                .append(importedReturn.getPeriodStart())
                .append(" _ ")
                .append(importedReturn.getPeriodEnd())
                .append("(Imported:")
                .append(importedReturn.getImportEnd())
                .append(")");
        return sb.toString();
    }
}
