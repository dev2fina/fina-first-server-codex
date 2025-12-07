package net.fina.server.i18n.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "SYS_LANGUAGES")
@Table(name = "SYS_LANGUAGES")
@NamedQueries({
        @NamedQuery(name = "Language.findAll", query = "select l from SYS_LANGUAGES as l where l.deleted=:deleted order by l.code "),
        @NamedQuery(name = "Language.findByCode", query = "select l from SYS_LANGUAGES as l where trim(l.code)=:code"),
        @NamedQuery(name = "Language.codeName", query = "select trim(l.code), trim(l.name) from SYS_LANGUAGES l where l.deleted=:deleted order by l.code "),
        @NamedQuery(name = "Language.codeId", query = "select trim(l.code), l.id from SYS_LANGUAGES l where l.deleted=:deleted order by l.code "),
        @NamedQuery(name = "Language.nameById", query = "select l.id, trim(l.name) from SYS_LANGUAGES l where l.id in(:id) "),
        @NamedQuery(name = "Language.findIds", query = "select distinct l.id from SYS_LANGUAGES l where l.deleted=:deleted"),
        @NamedQuery(name = "Language.findCodeById", query = "select trim(l.code) from SYS_LANGUAGES l where l.id=:id "),
        @NamedQuery(name = "Language.sameCodeLanguages", query = "select l from SYS_LANGUAGES as l where trim(l.code)=:code and l.id<>:id ")
})
public class Language implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "sys_languages_sequence", sequenceName = "sys_languages_sequence", allocationSize = 1)
    @GeneratedValue(generator = "sys_languages_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @NotNull
    @Column(name = "CODE", length = 12, unique = true, nullable = false)
    private String code;

    @NotNull
    @Column(name = "NAME", length = 24)
    private String name;

    @Column(name = "DATEFORMAT", length = 24)
    private String dateFormat;

    @Column(name = "DATETIMEFORMAT", length = 24)
    private String dateTimeFormat;

    @Column(name = "NUMBERFORMAT", length = 24)
    private String numberFormat;

    @Column(name = "FONTFACE", length = 24)
    private String fontFace;

    @Column(name = "FONTSIZE")
    private int fontSize;

    @Column(name = "HTMLCHARSET", length = 20)
    private String htmlCharSet;

    @Column(name = "XMLENCODING", length = 20)
    private String xmlEncoding;

    @Column(name = "DELETED")
    private boolean deleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateFormat() {
        return dateFormat != null ? dateFormat.trim() : null;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat != null ? dateTimeFormat.trim() : null;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public String getFontFace() {
        return fontFace;
    }

    public void setFontFace(String fontFace) {
        this.fontFace = fontFace;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getHtmlCharSet() {
        return htmlCharSet;
    }

    public void setHtmlCharSet(String htmlCharSet) {
        this.htmlCharSet = htmlCharSet;
    }

    public String getXmlEncoding() {
        return xmlEncoding;
    }

    public void setXmlEncoding(String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Language other = (Language) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Language [id=" + id + ", version=" + version + ", code=" + code + ", name=" + name + ", dateFormat=" + dateFormat
                + ", numberFormat=" + numberFormat + ", dateTimeFormat=" + dateTimeFormat + ", fontFace=" + fontFace + ", fontSize=" + fontSize
                + ", htmlCharSet=" + htmlCharSet + ", xmlEncoding=" + xmlEncoding + ", deleted=" + deleted + "]";
    }
}
