package net.fina.server.security.crypto.signature;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.FileSignerException;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;

@Stateless
@RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_FILE_SIGN)
public class FileSignerFactorySession {

    public FileSigner createFileSigner() throws FileSignerException {
        return FileSignerFactory.createFileSigner();
    }
}
