package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Type of beneficial owner (Complex Structure).
 * PHYSICAL = Natural person, LEGAL = Legal entity.
 */
@Getter
@RequiredArgsConstructor
public enum BeneficiaryType {

    PHYSICAL("Physical", "Natural person"),
    LEGAL("Legal", "Legal entity/organization");

    private final String displayName;
    private final String description;
}
