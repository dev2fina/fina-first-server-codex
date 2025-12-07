package net.fina.server.returns.entity;

import java.io.Serializable;

/**
 * User: nikoloz
 * Date: 11/8/13
 * Time: 2:03 PM
 */
public class ImportedXmlReturnId implements Serializable {

    private int importedReturnId;
    private long returnId;

    public int getImportedReturnId() {
        return importedReturnId;
    }

    public void setImportedReturnId(int importedReturnId) {
        this.importedReturnId = importedReturnId;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportedXmlReturnId that = (ImportedXmlReturnId) o;

        return importedReturnId == that.importedReturnId && returnId == that.returnId;
    }

    @Override
    public int hashCode() {
        int result = importedReturnId;
        result = 31 * result + (int) (returnId ^ (returnId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ImportedXmlReturnId{" +
                "importedReturnId=" + importedReturnId +
                ", returnId=" + returnId +
                '}';
    }
}
