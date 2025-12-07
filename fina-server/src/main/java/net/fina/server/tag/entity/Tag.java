package net.fina.server.tag.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "IN_TAGS")
@Table(name = "IN_TAGS")
public class Tag implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "tag_sequence", sequenceName = "tag_sequence", allocationSize = 1)
    @GeneratedValue(generator = "tag_sequence", strategy = GenerationType.SEQUENCE)
    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
