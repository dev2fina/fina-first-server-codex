package net.fina.server.legalperson.entity.metainfo;

import net.fina.server.i18n.helper.Description;
import jakarta.persistence.*;

@Entity(name = "IN_BUSINESS_ENTITY_TYPE")
@Table(name = "IN_BUSINESS_ENTITY_TYPE")
public class BusinessEntityType extends BaseLegalEntityType {

    @Id
    @SequenceGenerator(name = "business_entity_sequence", sequenceName = "business_entity_sequence", allocationSize = 1)
    @GeneratedValue(generator = "business_entity_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    public BusinessEntityType() {
    }

    public BusinessEntityType(long id, String code, Description description) {
        super(code,description);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
