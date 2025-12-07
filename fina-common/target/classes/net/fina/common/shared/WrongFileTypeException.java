package net.fina.common.shared;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class WrongFileTypeException extends Exception {

    public WrongFileTypeException() {
    }

    public WrongFileTypeException(String message) {
        super(message);
    }

    public WrongFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
