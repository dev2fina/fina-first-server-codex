package net.fina.server.communicator.event;

public class CommunicatorEvent {
    private final String author;

    public CommunicatorEvent(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }
}
