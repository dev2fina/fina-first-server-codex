package net.fina.server.legalperson.entity.metainfo;
import net.fina.server.i18n.helper.Description;

import jakarta.persistence.*;

@Entity(name = "IN_MANAGEMENT_FORM_TYPE")
@Table(name = "IN_MANAGEMENT_FORM_TYPE")
public class ManagementFormType extends BaseLegalEntityType {
    @Id
    @SequenceGenerator(name = "management_form_sequence", sequenceName = "management_form_sequence", allocationSize = 1)
    @GeneratedValue(generator = "management_form_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    public ManagementFormType() {
    }

    public ManagementFormType(long id, String code, Description description) {
        super(code, description);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
