package net.fina.first.model.enums;

import lombok.Getter;

/**
 * Type of beneficial owner (Complex Structure).
 * PHYSICAL = Natural person, LEGAL = Legal entity.
 */
@Getter
public enum BeneficiaryType {

    PHYSICAL("Physical", "Natural person"),
    LEGAL("Legal", "Legal entity/organization");

    private final String displayName;
    private final String description;

    BeneficiaryType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
