package net.fina.first.model.enums;

import lombok.Getter;

/**
 * Controller decision options during registration review.
 */
@Getter
public enum ControllerDecision {

    ACCEPT("Accept", "Application accepted, proceed with registration"),
    DECLINE("Decline", "Application declined, requires correction"),
    REQUEST_INFO("Request Info", "Additional information requested from FI");

    private final String displayName;
    private final String description;

    ControllerDecision(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
