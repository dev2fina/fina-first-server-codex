package net.fina.server.dcs.uploadfile.impl.reader.excel;

import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.dcs.uploadfile.model.RegAdvancedFileType;
import org.jboss.logging.Logger;

public abstract class MatrixOptionBase {
    protected final Logger log = Logger.getLogger(getClass());
    protected String fiType;
    protected String matrixForEachType;
    protected String pattern;
    protected String period;
    protected String periodTypeLabel;
    protected String version;
    protected String workBookPassword;
    protected boolean digitalSignatureCheckEnabled;
    protected ProcessEngine processEngine;
    private RegAdvancedFileType regAdvancedFileType;
    protected boolean encryptEnabled;

    public String getFIType() {
        return fiType;
    }

    public void setFiType(String fiType) {
        this.fiType = fiType;
    }

    public String getMatrixForEachType() {
        return matrixForEachType;
    }

    public void setMatrixForEachType(String matrixForEachType) {
        this.matrixForEachType = matrixForEachType;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriodTypeLabel() {
        return periodTypeLabel;
    }

    public void setPeriodTypeLabel(String periodTypeLabel) {
        this.periodTypeLabel = periodTypeLabel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWorkBookPassword() {
        return workBookPassword;
    }

    public void setWorkBookPassword(String workBookPassword) {
        this.workBookPassword = workBookPassword;
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

    public RegAdvancedFileType getRegAdvancedFileType() {
        return regAdvancedFileType;
    }

    public void setRegAdvancedFileType(RegAdvancedFileType regAdvancedFileType) {
        this.regAdvancedFileType = regAdvancedFileType;
    }

    public boolean isEncryptEnabled() {
        return encryptEnabled;
    }

    @Override
    public String toString() {
        return "Option [fiType=" + fiType + ", matrixForEachType=" + matrixForEachType + ", pattern=" + pattern + ", period=" + period + ", periodTypeLabel=" + periodTypeLabel + ", version=" + version + "]";
    }
}
