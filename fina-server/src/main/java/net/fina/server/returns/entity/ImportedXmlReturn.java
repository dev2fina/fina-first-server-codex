package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.returns.ImportedXmlReturnStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * User: nikoloz
 * Date: 11/8/13
 * Time: 1:54 PM
 */
@Entity(name = "IN_IMPORTED_XML_RETURN")
@Table(name = "IN_IMPORTED_XML_RETURN")
@IdClass(ImportedXmlReturnId.class)
public class ImportedXmlReturn implements Serializable, Audited {

    @Id
    @Column(name = "ImportedReturnId")
    private int importedReturnId;

    @Id
    @Column(name = "returnId")
    private long returnId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "importTime")
    private Date importTime;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private ImportedXmlReturnStatus status;

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

    public Date getImportTime() {
        return importTime;
    }

    public void setImportTime(Date processTime) {
        this.importTime = processTime;
    }

    public ImportedXmlReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ImportedXmlReturnStatus status) {
        this.status = status;
    }
}
