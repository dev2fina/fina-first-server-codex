package net.fina.server.legalperson.entity;

import jakarta.persistence.*;

@Entity(name = "IN_LEGAL_PERSON_CONNECTIONS")
@Table(name = "IN_LEGAL_PERSON_CONNECTIONS")
public class FiLegalPersonConnection {

    @Id
    @SequenceGenerator(name = "lp_connection_sequence", sequenceName = "lp_connection_sequence", allocationSize = 1)
    @GeneratedValue(generator = "lp_connection_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated
    @Column(name = "CONNECTION_TYPE")
    private FiLegalPersonConnectionType connectionType;

    public FiLegalPersonConnection() {
    }

    public FiLegalPersonConnection(FiLegalPersonConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FiLegalPersonConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(FiLegalPersonConnectionType connectionType) {
        this.connectionType = connectionType;
    }
}
