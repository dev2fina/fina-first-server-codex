package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class NodeBodyLock implements BaseRepresentation {

    /**
     * Gets or Sets type
     */
    public enum TypeEnum {
        @JsonProperty("FULL")
        FULL("FULL"),

        @JsonProperty("ALLOW_ADD_CHILDREN")
        ALLOW_ADD_CHILDREN("ALLOW_ADD_CHILDREN"),

        @JsonProperty("ALLOW_OWNER_CHANGES")
        ALLOW_OWNER_CHANGES("ALLOW_OWNER_CHANGES");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    /**
     * Gets or Sets lifetime
     */
    public enum LifetimeEnum {
        @JsonProperty("PERSISTENT")
        PERSISTENT("PERSISTENT"),

        @JsonProperty("EPHEMERAL")
        EPHEMERAL("EPHEMERAL");

        private String value;

        LifetimeEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    @JsonProperty("timeToExpire")
    private final Integer timeToExpire;

    @JsonProperty("type")
    private final TypeEnum type;

    @JsonProperty("lifetime")
    private final LifetimeEnum lifetime;

    public NodeBodyLock() {
        this.timeToExpire = null;
        this.type = null;
        this.lifetime = null;
    }

    public NodeBodyLock(Integer timeToExpire, TypeEnum type, LifetimeEnum lifetime) {
        this.timeToExpire = timeToExpire;
        this.type = type != null ? type : TypeEnum.ALLOW_OWNER_CHANGES;
        this.lifetime = lifetime != null ? lifetime : LifetimeEnum.PERSISTENT;
    }
}
