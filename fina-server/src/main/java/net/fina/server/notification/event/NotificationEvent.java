package net.fina.server.notification.event;

public class NotificationEvent {
    private final String author;

    public NotificationEvent(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }
}
