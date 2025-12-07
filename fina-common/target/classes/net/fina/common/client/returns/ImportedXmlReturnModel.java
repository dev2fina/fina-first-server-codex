package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Chelomisha@fina2.net
 * Date: 11/25/13
 * Time: 6:50 PM
 */
public class ImportedXmlReturnModel implements Serializable {
    private ImportModel importModel;
    private long returnId;
    private Date importTime;
    private ImportedXmlReturnStatus status;

    public ImportedXmlReturnModel() {
    }

    public ImportModel getImportModel() {
        return importModel;
    }

    public void setImportModel(ImportModel importModel) {
        this.importModel = importModel;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public Date getImportTime() {
        return importTime;
    }

    public void setImportTime(Date importTime) {
        this.importTime = importTime;
    }

    public ImportedXmlReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ImportedXmlReturnStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportedXmlReturnModel that = (ImportedXmlReturnModel) o;

        if (returnId != that.returnId) return false;
        if (importModel != null ? !importModel.equals(that.importModel) : that.importModel != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = importModel != null ? importModel.hashCode() : 0;
        result = 31 * result + (int) (returnId ^ (returnId >>> 32));
        return result;
    }
}
