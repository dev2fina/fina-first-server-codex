package net.fina.server.dcs.uploadfile.impl.reader.excel;

import net.fina.server.matrix.entity.Matrix;

public class MatrixOption extends MatrixOptionBase {

    private final long matrixId;
    public MatrixOption(Matrix entity, long langId,long matrixId) {
        this.setFiType(entity.getFiType().getCode());
        this.matrixId=matrixId;
        this.setPattern(entity.getPattern().trim());
        this.setPeriod(entity.getPeriodType().getCode());
        this.setPeriodTypeLabel(entity.getPeriodType().getDescription().getDescription(langId));
        this.setVersion(entity.getReturnVersion().getCode());
        this.setWorkBookPassword(entity.getPassword());
        this.setDigitalSignatureCheckEnabled(entity.isDigitalSignatureCheckEnabled());
        this.setProcessEngine(entity.getProcessEngine());
        this.setRegAdvancedFileType(entity.getRegFileType());
    }

    public long getMatrixId() {
        return matrixId;
    }
}
