package net.fina.ecm.alfresco.api.common.representation;

import java.io.Serializable;

public class AbstractRepresentation implements Serializable, BaseRepresentation {

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    protected String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
