package net.fina.server.legalperson.entity.metainfo;

import jakarta.persistence.*;
import net.fina.server.i18n.helper.Description;

@Entity(name = "IN_EQUITY_FORM_TYPE")
@Table(name = "IN_EQUITY_FORM_TYPE")
public class EquityFormType extends BaseLegalEntityType {
    @Id
    @SequenceGenerator(name = "equity_form_sequence", sequenceName = "equity_form_sequence", allocationSize = 1)
    @GeneratedValue(generator = "equity_form_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    public EquityFormType() {
    }

    public EquityFormType(long id, String code, Description description) {
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
