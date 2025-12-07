package net.fina.common.shared;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class FileSignerException extends Exception {

    public FileSignerException() {
        super();
    }

    public FileSignerException(String message) {
        super(message);
    }

    public FileSignerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSignerException(Throwable cause) {
        super(cause);
    }

}
