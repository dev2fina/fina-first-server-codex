package net.fina.server.i18n.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "SYS_STRINGS")
@Table(name = "SYS_STRINGS")
@IdClass(SysStringId.class)
public class SysString implements Serializable {

    @Id
    @SequenceGenerator(name = "sys_string_sequence", sequenceName ="sys_string_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sys_string_sequence")
    @Column(name = "ID")
    private long id;

    @Id
    @Column(name = "LANGID")
    private long langId;

    @Column(name = "VALUE")
    private String value;

    public String getValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SysString [id=" + id + ", langId=" + langId + ", value=" + value + "]";
    }

}
