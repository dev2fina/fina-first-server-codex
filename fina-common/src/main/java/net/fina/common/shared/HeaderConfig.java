package net.fina.common.shared;

public enum HeaderConfig {
    FINA_HEADER_IMAGE("FINA_HEADER_IMAGE"),
    FINA_HEADER_TEXT("FINA_HEADER_TEXT"),
    DCS_HEADER_IMAGE("DCS_HEADER_IMAGE"),
    DCS_HEADER_TEXT("DCS_HEADER_TEXT"),
    DCS_LOGIN_LABEL("DCS_LOGIN_LABEL"),
    LOGIN_WELCOME_LABEL("LOGIN_WELCOME_LABEL"),
    LOGIN_PUBLIC_PORTAL_LABEL("LOGIN_PUBLIC_PORTAL_LABEL");

    private String tag;

    HeaderConfig(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}