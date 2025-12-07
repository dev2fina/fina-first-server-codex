package net.fina.common.shared;

public enum Theme {
    Gray("gray", "gray-theme-header"),
    Blue("blue", "blue-theme-header");

    private final String themeName;
    private final String headerId;

    Theme(String themeName, String headerId) {
        this.themeName = themeName;
        this.headerId = headerId;
    }

    public String getThemeName() {
        return themeName;
    }

    public String getHeaderId() {
        return headerId;
    }

    public static Theme getDefaultTheme() {
        return Theme.Gray;
    }
}
