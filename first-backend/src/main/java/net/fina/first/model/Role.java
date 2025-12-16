package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 64)
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
