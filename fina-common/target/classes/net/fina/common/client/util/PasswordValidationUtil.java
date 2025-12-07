package net.fina.common.client.util;

import net.fina.common.client.constants.PasswordChangeStatus;
import net.fina.common.shared.FinaPasswordPolicy;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidationUtil {

    private PasswordValidationUtil() {
    }

    public static List<PasswordChangeStatus> isValidPassword(FinaPasswordPolicy policy, String password) {
        List<PasswordChangeStatus> statuses = new ArrayList<>();

        if (password.length() < policy.getMinLength()) {
            statuses.add(PasswordChangeStatus.MINIMUM_LENGTH);
        }
        if (policy.isNumbers() && password.chars().noneMatch(Character::isDigit)) {
            statuses.add(PasswordChangeStatus.WITH_NUMS);
        }
        if (policy.isLetters() && password.chars().noneMatch(Character::isLetter)) {
            statuses.add(PasswordChangeStatus.WITH_CHARS);
        }
        if (policy.isUpperCase() && password.chars().noneMatch(Character::isUpperCase)) {
            statuses.add(PasswordChangeStatus.WITH_CHARS_UPPER);
        }
        if (policy.isSpecialCharacters() && password.matches("[a-zA-Z0-9]*")) {
            statuses.add(PasswordChangeStatus.WITH_SPECIAL_CHARACTERS);
        }

        return statuses;
    }

}
