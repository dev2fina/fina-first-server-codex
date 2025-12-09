package net.fina.ecm.alfresco.api.core.model.representation;

public enum RenditionStatusEnum {
    CREATED("CREATED"),

    NOT_CREATED("NOT_CREATED");

    private String value;

    RenditionStatusEnum(String value) {
        this.value = value;
    }

    public static RenditionStatusEnum fromString(String text) {
        for (RenditionStatusEnum b : RenditionStatusEnum.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
