package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Type of legal entity for beneficiaries that are organizations.
 */
@Getter
@RequiredArgsConstructor
public enum LegalEntityType {

    FUND("Fund", "Investment fund or similar"),
    UNION("Union", "Professional or trade union"),
    CORPORATION("Corporation", "Corporation"),
    LLC("LLC", "Limited Liability Company"),
    PARTNERSHIP("Partnership", "Partnership"),
    OTHER("Other", "Other legal entity type");

    private final String displayName;
    private final String description;
}
