package net.fina.server.cems.entity.sanction;

public enum CEMSDecisionMakingBodyCatalog {
    PN("net.fina.cems.sanction.decision.making.body.classifier.PN"),
    KPN("net.fina.cems.sanction.decision.making.body.classifier.KPN"),
    ZP("net.fina.cems.sanction.decision.making.body.classifier.ZP"),
    NU1("net.fina.cems.sanction.decision.making.body.classifier.NU1"),
    NU2("net.fina.cems.sanction.decision.making.body.classifier.NU2"),
    NM("net.fina.cems.sanction.decision.making.body.classifier.NM"),
    NZ("net.fina.cems.sanction.decision.making.body.classifier.NZ");

    private final String code;

    public String getCode() {
        return code;
    }

    CEMSDecisionMakingBodyCatalog(String code) {
        this.code = code;
    }
}
