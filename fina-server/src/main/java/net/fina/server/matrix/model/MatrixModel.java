package net.fina.server.matrix.model;

import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.common.client.returns.PeriodTypeModel;
import net.fina.common.shared.ReturnVersionModel;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.dcs.uploadfile.model.RegAdvancedFileType;

import java.io.Serializable;

public class MatrixModel implements Serializable {

    private long id;

    private FiTypeSimpleModel fiTypeModel;

    private String pattern;

    private PeriodTypeModel periodType;


    private ReturnVersionModel returnVersion;

    private String password;

    private boolean digitalSignatureCheckEnabled;

    private ProcessEngine processEngine;

    private RegAdvancedFileType regFileType;

    private boolean enable;

    public MatrixModel() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FiTypeSimpleModel getFiTypeModel() {
        return fiTypeModel;
    }

    public void setFiTypeModel(FiTypeSimpleModel fiTypeModel) {
        this.fiTypeModel = fiTypeModel;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public PeriodTypeModel getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodTypeModel periodType) {
        this.periodType = periodType;
    }

    public ReturnVersionModel getReturnVersion() {
        return returnVersion;
    }

    public void setReturnVersion(ReturnVersionModel returnVersion) {
        this.returnVersion = returnVersion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDigitalSignatureCheckEnabled() {
        return digitalSignatureCheckEnabled;
    }

    public void setDigitalSignatureCheckEnabled(boolean digitalSignatureCheckEnabled) {
        this.digitalSignatureCheckEnabled = digitalSignatureCheckEnabled;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public RegAdvancedFileType getRegFileType() {
        return regFileType;
    }

    public void setRegFileType(RegAdvancedFileType regFileType) {
        this.regFileType = regFileType;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
