package net.fina.server.returns.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.ReturnVersionModel;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.entity.ReturnVersion;
import net.fina.server.returns.model.helper.ReturnVersionModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class ReturnVersionProxySession {

    @Inject
    private ReturnVersionLocal returnVersionLocal;

    @RolesAllowed({PermissionIdNames.FINA_RETURNS_VERSION_REVIEW, PermissionIdNames.MENU_MATRIX})
    public List<ReturnVersionModel> loadReturnVersions(boolean loadAll) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return ReturnVersionModelHelper.toModels(returnVersionLocal.loadReturnVersions(loadAll), langId);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_VERSION_AMEND)
    public ReturnVersionModel saveVersion(ReturnVersionModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        ReturnVersion returnversion = returnVersionLocal.save(ReturnVersionModelHelper.toEntity(model, langId));
        return ReturnVersionModelHelper.toModel(returnversion, langId);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_VERSION_DELETE)
    public void delete(long versionId) throws FinATypeException {
        returnVersionLocal.delete(versionId);
    }
}
