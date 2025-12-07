package net.fina.common.client.constants;

public enum MessageFilter {

    TITLE("title:"),
    CONTENT("content:"),
    AUTHORS("authors:"),
    RECIPIENTS("recipients:"),
    REVERSE_AUTHORS("reverseAuthors:"),
    STATUS("status:"),
    AFTER("after:"),
    BEFORE("before:"),
    ATTACHMENT("attachment:"),
    NOREPLIES("noreplies:"),
    HAS_WORDS(""),
    FIS("fis:"),
    HIDE_AUTOMATIC("hideAutomatic:"),
    BOOKMARKED("bookmarked:"),
    NOTIFICATION_TYPE("notificationType"),
    IGNORED_REPLIES("ignoredReplies"),
    UNREAD("unread"),
    MARK_TYPE("markType:");

    private String value;

    MessageFilter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
