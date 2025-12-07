package net.fina.server.user;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.security.util.UserFieldValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class UserFieldValidationTest {
    private final String input;

    final String[] validLogins = {
            "00245",     // valid
            "123_John",     // valid
            "123-John",     // valid
            "John",     // valid
            "jane.doe",     // valid
            "5292298 "
    };

    @Parameterized.Parameters
    public static Collection<String> data() {
        return Arrays.asList(
                ".hidden",      // ❌ starts with dot
                "admin--dev",   // ❌ double dash
                "user$name",    // ❌ invalid character
                "user..",        // ❌ ends in dots
                "  ",        // ❌ minimum 1 character
                "user_user_user_user_user_user_user_user_user_user_user"        // ❌ maximum 50 character
        );
    }

    public UserFieldValidationTest(String input) {
        this.input = input;
    }

    @Test
    public void testValidUserLogin() {
        for (String u : validLogins) {
            try {
                UserFieldValidator.validateLogin(u);
            } catch (FinATypeException e) {
                org.junit.Assert.fail("Expected no exception, but got: " + e.getMessage());
            }
        }
    }


    @Test(expected = FinATypeException.class)
    public void testInvalidUserLogin() throws FinATypeException {
        UserFieldValidator.validateLogin(input);
    }
}
