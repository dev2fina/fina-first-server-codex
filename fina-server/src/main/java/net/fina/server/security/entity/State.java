package net.fina.server.security.entity;

import java.io.Serializable;

import jakarta.persistence.*;

@Table(name = "SYS_USER_STATES")
@Entity(name = "SYS_USER_STATES")
@NamedQueries({
        @NamedQuery(name = "State.findAll", query = "select s from SYS_USER_STATES as s "),
        @NamedQuery(name = "State.findByCodeAndUserId", query = "select s from SYS_USER_STATES  s where trim(s.code)=:code and s.userId=:userId "),
        @NamedQuery(name = "State.findByUserId", query = "select s from SYS_USER_STATES as s where s.userId=:userId")
})
public class State implements Serializable {
    @Id
    @SequenceGenerator(name = "sys_user_states_sequence", sequenceName ="sys_user_states_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sys_user_states_sequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "CODE", unique = true, nullable = false, length = 100)
    private String code;

    @Lob
    @Column(name = "VALUE")
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "State [id=" + id + ", userId=" + userId + ", " + (code != null ? "code=" + code + ", " : "") + (value != null ? "value=" + value : "") + "]";
    }

}
