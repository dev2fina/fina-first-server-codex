package net.fina.first.model.enums;

import lombok.Getter;

/**
 * Types of documents in the FIRST system.
 */
@Getter
public enum DocumentType {

    DOCUMENT("Document", "General document"),
    MEMORANDUM("Memorandum", "Memorandum of association"),
    DECREE("Decree", "Legal directive/decree"),
    REPORT_CARD("Report Card", "Compliance report card"),
    CONFIRMATION_LETTER("Confirmation Letter", "Confirmation letter"),
    GAP_LETTER("Gap Letter", "Missing information notice"),
    REFUSAL_LETTER("Refusal Letter", "Application rejection letter"),
    LICENSE_CERTIFICATE("License Certificate", "License certificate"),
    REGISTRATION_CERTIFICATE("Registration Certificate", "Registration certificate"),
    FINANCIAL_STATEMENT("Financial Statement", "Financial statement"),
    AUDIT_REPORT("Audit Report", "Audit report"),
    CHARTER("Charter", "Company charter"),
    OWNERSHIP_STRUCTURE("Ownership Structure", "Ownership structure document"),
    IDENTIFICATION("Identification", "Identification document"),
    OTHER("Other", "Other document type");

    private final String displayName;
    private final String description;

    DocumentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
