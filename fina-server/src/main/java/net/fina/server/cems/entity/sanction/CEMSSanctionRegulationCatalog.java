package net.fina.server.cems.entity.sanction;

public enum CEMSSanctionRegulationCatalog {
    CODE_1("net.fina.cems.sanction.regulation.classifier.1"),
    CODE_2("net.fina.cems.sanction.regulation.classifier.2"),
    CODE_3("net.fina.cems.sanction.regulation.classifier.3"),
    CODE_4("net.fina.cems.sanction.regulation.classifier.4"),
    CODE_5("net.fina.cems.sanction.regulation.classifier.5"),
    CODE_6("net.fina.cems.sanction.regulation.classifier.6"),
    CODE_7("net.fina.cems.sanction.regulation.classifier.7"),
    CODE_8("net.fina.cems.sanction.regulation.classifier.8"),
    CODE_9("net.fina.cems.sanction.regulation.classifier.9"),
    CODE_10("net.fina.cems.sanction.regulation.classifier.10"),
    CODE_11("net.fina.cems.sanction.regulation.classifier.11"),
    CODE_12("net.fina.cems.sanction.regulation.classifier.12"),
    CODE_13("net.fina.cems.sanction.regulation.classifier.13"),
    CODE_14("net.fina.cems.sanction.regulation.classifier.14"),
    CODE_15("net.fina.cems.sanction.regulation.classifier.15"),
    CODE_16("net.fina.cems.sanction.regulation.classifier.16"),
    CODE_17("net.fina.cems.sanction.regulation.classifier.17"),
    CODE_18("net.fina.cems.sanction.regulation.classifier.18"),
    CODE_19("net.fina.cems.sanction.regulation.classifier.19"),
    CODE_20("net.fina.cems.sanction.regulation.classifier.20"),
    CODE_21("net.fina.cems.sanction.regulation.classifier.21"),
    CODE_22("net.fina.cems.sanction.regulation.classifier.22"),
    CODE_23("net.fina.cems.sanction.regulation.classifier.23"),
    CODE_24("net.fina.cems.sanction.regulation.classifier.24");

    private final String code;

    CEMSSanctionRegulationCatalog(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
