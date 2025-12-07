package net.fina.server.matrix.entity;


import jakarta.persistence.*;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.dcs.uploadfile.model.RegAdvancedFileType;
import net.fina.server.fi.entity.FiType;
import net.fina.server.returns.entity.PeriodType;
import net.fina.server.returns.entity.ReturnVersion;

@Entity(name = "SYS_MATRIX")
@Table(name = "SYS_MATRIX")
public class Matrix {

    @Id
    @SequenceGenerator(name = "matrix_sequence", sequenceName = "matrix_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "matrix_sequence")
    private long id;

    @OneToOne
    @JoinColumn(name = "FI_TYPE_ID")
    private FiType fiType;

    @Column(name = "PATTERN", unique = true)
    private String pattern;

    @OneToOne
    @JoinColumn(name = "PERIOD_TYPE_ID")
    private PeriodType periodType;


    @OneToOne
    @JoinColumn(name = "VERSION_ID")
    private ReturnVersion returnVersion;


    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SIGNATURE")
    private boolean digitalSignatureCheckEnabled;

    @Column(name = "ENGINE")
    @Enumerated(EnumType.ORDINAL)
    private ProcessEngine processEngine;

    @Column(name = "REG_FILE_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private RegAdvancedFileType regFileType;

    @Column(name = "ENABLED")
    private boolean enable;

    public Matrix() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FiType getFiType() {
        return fiType;
    }

    public void setFiType(FiType fiType) {
        this.fiType = fiType;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public ReturnVersion getReturnVersion() {
        return returnVersion;
    }

    public void setReturnVersion(ReturnVersion returnVersion) {
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
