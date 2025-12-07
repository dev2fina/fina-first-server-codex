package net.fina.server.returns.qualifier;

import jakarta.enterprise.util.AnnotationLiteral;

public class ReturnNotificationQualifierLiteral extends AnnotationLiteral<ReturnNotificationQualifier> implements ReturnNotificationQualifier {

    private final  String type;

    public ReturnNotificationQualifierLiteral(String type) {
        this.type = type;
    }

    @Override
    public String value() {
        return type;
    }

}
