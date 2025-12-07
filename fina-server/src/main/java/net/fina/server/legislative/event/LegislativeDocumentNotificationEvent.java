package net.fina.server.legislative.event;

import net.fina.server.legislative.entity.LegislativeDocument;

public class LegislativeDocumentNotificationEvent {

    private final LegislativeDocument document;

    public LegislativeDocumentNotificationEvent(LegislativeDocument document) {
        this.document = document;
    }

    public LegislativeDocument getDocument() {
        return document;
    }
}
