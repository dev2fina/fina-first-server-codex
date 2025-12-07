package net.fina.server;

import net.fina.common.client.constants.PasswordChangeStatus;
import net.fina.common.shared.FinaPasswordPolicy;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static net.fina.common.client.util.PasswordValidationUtil.isValidPassword;

public class PasswordPolicyTest {

    @Test
    public void testShouldContainNumbers() {

        FinaPasswordPolicy policy = new FinaPasswordPolicy("4", null, "1", null, null);
        List<PasswordChangeStatus> passed = isValidPassword(policy, "123QWE");
        List<PasswordChangeStatus> notPassed = isValidPassword(policy, "qweqr");

        Assert.assertFalse(passed.contains(PasswordChangeStatus.WITH_NUMS));
        Assert.assertTrue(notPassed.contains(PasswordChangeStatus.WITH_NUMS));

    }

    @Test
    public void testShouldContainLetters() {

        FinaPasswordPolicy policy = new FinaPasswordPolicy("4", "1", null, null, null);
        List<PasswordChangeStatus> passed = isValidPassword(policy, "123qEe");
        List<PasswordChangeStatus> notPassed = isValidPassword(policy, "!@#!$%^&");

        Assert.assertFalse(passed.contains(PasswordChangeStatus.WITH_CHARS));
        Assert.assertTrue(notPassed.contains(PasswordChangeStatus.WITH_CHARS));

    }

    @Test
    public void testShouldContainLettersUpperCase() {

        FinaPasswordPolicy policy = new FinaPasswordPolicy("4", null, null, "1", null);
        List<PasswordChangeStatus> passed = isValidPassword(policy, "123qWwe");
        List<PasswordChangeStatus> notPassed = isValidPassword(policy, "abc");

        Assert.assertFalse(passed.contains(PasswordChangeStatus.WITH_CHARS_UPPER));
        Assert.assertTrue(notPassed.contains(PasswordChangeStatus.WITH_CHARS_UPPER));

    }

    @Test
    public void testShouldContainSpecialCharacters() {

        FinaPasswordPolicy policy = new FinaPasswordPolicy("4", null, null, null, "1");
        List<PasswordChangeStatus> passed = isValidPassword(policy, "123qW@#!we");
        List<PasswordChangeStatus> notPassed = isValidPassword(policy, "ab123c");

        Assert.assertFalse(passed.contains(PasswordChangeStatus.WITH_SPECIAL_CHARACTERS));
        Assert.assertTrue(notPassed.contains(PasswordChangeStatus.WITH_SPECIAL_CHARACTERS));

    }


}
