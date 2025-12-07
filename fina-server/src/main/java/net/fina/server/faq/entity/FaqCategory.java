package net.fina.server.faq.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "IN_FAQ_CATEGORIES")
public class FaqCategory implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "faq_categories_sequence", sequenceName = "faq_categories_sequence", allocationSize = 1)
    @GeneratedValue(generator = "faq_categories_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    private long parentId;

    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    @Column(name = "NAMESTRID")
    private Description name;

    private boolean leaf;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
}
