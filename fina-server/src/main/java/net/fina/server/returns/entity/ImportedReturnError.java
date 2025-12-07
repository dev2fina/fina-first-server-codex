package net.fina.server.returns.entity;

import jakarta.persistence.*;

@Entity(name = "IN_IMPORTED_RETURN_ERRORS")
@Table(name = "IN_IMPORTED_RETURN_ERRORS")
public class ImportedReturnError {
    @Id
    @SequenceGenerator(name = "imported_return_error_sequence", sequenceName = "imported_return_error_sequence", allocationSize = 1)
    @GeneratedValue(generator = "imported_return_error_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "IMPORTED_RETURN_ID")
    private long importedReturnId;

    @Basic(fetch = FetchType.EAGER)
    @Column(name = "ERROR_MESSAGE")
    private byte[] errorContent;

    public ImportedReturnError() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getImportedReturnId() {
        return importedReturnId;
    }

    public void setImportedReturnId(long importedReturnId) {
        this.importedReturnId = importedReturnId;
    }

    public byte[] getErrorContent() {
        return errorContent;
    }

    public void setErrorContent(byte[] errorContent) {
        this.errorContent = errorContent;
    }
}
