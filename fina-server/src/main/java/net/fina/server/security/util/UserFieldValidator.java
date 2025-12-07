package net.fina.server.security.util;

import net.fina.common.client.exception.FinATypeException;

public class UserFieldValidator {

    public static void validateLogin(String login) throws FinATypeException {
        if (login == null || login.isBlank()) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "User Login Is Required...");
        }
        login = login.trim();
        String userLoginRegex = "^(?=.{1,50}$)(?![_.-])(?!.*[_.-]{2})[a-zA-Z0-9][a-zA-Z0-9_.-]*[a-zA-Z0-9]$";

        if (!login.matches(userLoginRegex)) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Login Contains invalid characters...");

        }
    }

}
