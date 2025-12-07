package net.fina.server.fi.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity(name = "IN_BANK_PERSON_CONNECTIONS")
@Table(name = "IN_BANK_PERSON_CONNECTIONS")
public class FiPersonConnection {
    @Id
    @SequenceGenerator(name = "bank_person_connection_sequence", sequenceName = "bank_person_connection_sequence", allocationSize = 1)
    @GeneratedValue(generator = "bank_person_connection_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated
    @Column(name = "CONNECTION_TYPE")
    private FiPersonConnectionType connectionType;

    public FiPersonConnection() {
    }

    public FiPersonConnection(FiPersonConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FiPersonConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(FiPersonConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiPersonConnection that = (FiPersonConnection) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
