package net.fina.server.security.crypto.signature;

import net.fina.common.shared.FileSignerException;

public interface FileSigner {

    byte[] sign(String fileName, byte[] file, String fileType) throws FileSignerException;

}
