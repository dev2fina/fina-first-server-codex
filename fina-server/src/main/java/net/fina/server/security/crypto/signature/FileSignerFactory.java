package net.fina.server.security.crypto.signature;

import net.fina.common.shared.FileSignerException;

public class FileSignerFactory {

    static FileSigner createFileSigner() throws FileSignerException {
        return new FileSignerByCertificate();
    }
}
