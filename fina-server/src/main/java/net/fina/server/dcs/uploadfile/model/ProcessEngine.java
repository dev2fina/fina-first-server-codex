package net.fina.server.dcs.uploadfile.model;

public enum ProcessEngine {
    FINA("fina2.dcs.upload.process.type.fina"),
    REG("fina2.dcs.upload.process.type.reg"),
    REG_ADVANCED("fina2.dcs.upload.process.type.regAdvanced");

    private String code;

    private ProcessEngine(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
