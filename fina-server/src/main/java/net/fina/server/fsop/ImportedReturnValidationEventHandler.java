package net.fina.server.fsop;

import org.w3c.dom.Node;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEventLocator;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImportedReturnValidationEventHandler implements ValidationEventHandler {

    private List<String> messages = new ArrayList<>();

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public boolean handleEvent(ValidationEvent event) {
        if (event == null)
            throw new IllegalArgumentException("event is null");

        // calculate the severity prefix and return value
        String severity = null;
        boolean continueParsing = false;
        switch (event.getSeverity()) {
            case ValidationEvent.WARNING:
                severity = "Warning";
                continueParsing = true; // continue after warnings
                break;
            case ValidationEvent.ERROR:
                severity = "Error";
                continueParsing = true; // terminate after errors
                break;
            case ValidationEvent.FATAL_ERROR:
                severity = "Fatal error";
                continueParsing = false; // terminate after fatal errors
                break;
            default:
                assert false : "Unknown severity.";
        }

        String location = getLocationDescription(event);
        String message = severity + " parsing " + location + " due to " + event.getMessage();
        messages.add(message);

        return continueParsing;
    }

    private String getLocationDescription(ValidationEvent event) {
        ValidationEventLocator locator = event.getLocator();
        if (locator == null) {
            return "XML with location unavailable";
        }

        StringBuilder msg = new StringBuilder();
        URL url = locator.getURL();
        Object obj = locator.getObject();
        Node node = locator.getNode();
        int line = locator.getLineNumber();

        if (url != null || line != -1) {
            msg.append("line ").append(line);
            if (url != null)
                msg.append(" of ").append(url);
        } else if (obj != null) {
            msg.append(" obj: ").append(obj.toString());
        } else if (node != null) {
            msg.append(" node: ").append(node.toString());
        }

        return msg.toString();
    }


}