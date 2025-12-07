package net.fina.server.cems.entity.sanction;

public enum CEMSSanctionMeasureCatalog {
    CODE_1_1("net.fina.cems.sanction.measure.classifier.1.1"),
    CODE_1_2("net.fina.cems.sanction.measure.classifier.1.2"),
    CODE_1_3("net.fina.cems.sanction.measure.classifier.1.3"),
    CODE_2_1("net.fina.cems.sanction.measure.classifier.2.1"),
    CODE_2_2("net.fina.cems.sanction.measure.classifier.2.2"),
    CODE_2_3("net.fina.cems.sanction.measure.classifier.2.3"),
    CODE_2_4("net.fina.cems.sanction.measure.classifier.2.4"),
    CODE_2_5("net.fina.cems.sanction.measure.classifier.2.5"),
    CODE_2_6("net.fina.cems.sanction.measure.classifier.2.6"),
    CODE_2_7("net.fina.cems.sanction.measure.classifier.2.7"),
    CODE_2_8("net.fina.cems.sanction.measure.classifier.2.8"),
    CODE_2_9("net.fina.cems.sanction.measure.classifier.2.9"),
    CODE_2_10("net.fina.cems.sanction.measure.classifier.2.10"),
    CODE_2_11("net.fina.cems.sanction.measure.classifier.2.11"),
    CODE_2_12("net.fina.cems.sanction.measure.classifier.2.12"),
    CODE_2_13("net.fina.cems.sanction.measure.classifier.2.13"),
    CODE_2_14("net.fina.cems.sanction.measure.classifier.2.14"),
    CODE_2_15("net.fina.cems.sanction.measure.classifier.2.15"),
    CODE_2_16("net.fina.cems.sanction.measure.classifier.2.16"),
    CODE_2_17("net.fina.cems.sanction.measure.classifier.2.17"),
    CODE_3_1("net.fina.cems.sanction.measure.classifier.3.1"),
    CODE_3_2("net.fina.cems.sanction.measure.classifier.3.2"),
    CODE_4_1("net.fina.cems.sanction.measure.classifier.4.1"),
    CODE_4_2("net.fina.cems.sanction.measure.classifier.4.2"),
    CODE_4_3("net.fina.cems.sanction.measure.classifier.4.3"),
    CODE_4_4("net.fina.cems.sanction.measure.classifier.4.4"),
    CODE_4_5("net.fina.cems.sanction.measure.classifier.4.5");

    private final String code;

    CEMSSanctionMeasureCatalog(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
