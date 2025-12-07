package net.fina.first.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fina.first.model.base.BaseEntity;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;

/**
 * Regional structure entity for hierarchical region classification.
 * Regions can have parent-child relationships forming a tree structure.
 */
@Entity
@Table(name = "first_regions")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region extends BaseEntity {

    @NotBlank(message = "Region code is required")
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Region name is required")
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_local")
    private String nameLocal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Region parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Region> children = new ArrayList<>();

    @Column(name = "level")
    private Integer level;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean deleted = false;

    /**
     * Adds a child region to this region.
     *
     * @param child the child region to add
     */
    public void addChild(Region child) {
        children.add(child);
        child.setParent(this);
        child.setLevel(this.level != null ? this.level + 1 : 1);
    }

    /**
     * Removes a child region from this region.
     *
     * @param child the child region to remove
     */
    public void removeChild(Region child) {
        children.remove(child);
        child.setParent(null);
    }
}
