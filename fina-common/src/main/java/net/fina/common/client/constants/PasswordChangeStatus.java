package net.fina.common.client.constants;

public enum PasswordChangeStatus {
    ERROR,
    SUCCESS,
    ALREADY_USED,
    MINIMUM_LENGTH,
    WITH_NUMS,
    WITH_CHARS,
    WITH_CHARS_UPPER,
    WITH_SPECIAL_CHARACTERS,
    CURRENT_PASS_NO_MATCH,// entered old password invalid
}
