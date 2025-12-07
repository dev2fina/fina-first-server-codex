package net.fina.server.reg.impl;

import jakarta.ejb.ApplicationException;
import net.fina.common.client.exception.DcsTypeException;

import java.security.GeneralSecurityException;

@ApplicationException(rollback = true)
public class RegFileProcessException extends DcsTypeException {
    public RegFileProcessException() {
    }

    public RegFileProcessException(String message) {
        super(message);
    }

    public RegFileProcessException(DcsTypeException e) {
        super(e, e.getType(), e.getParams());
    }

    public RegFileProcessException(Type type) {
        super(type);
    }

}
