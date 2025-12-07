package net.fina.common.client.fis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FiTypeSimpleModel implements Serializable {
    private long id;
    private String code;
    private String name;
    private List<FiModel> fis;

    public FiTypeSimpleModel() {
    }

    public FiTypeSimpleModel(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

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

    public List<FiModel> getFis() {
        return fis;
    }

    public void setFis(List<FiModel> fis) {
        this.fis = fis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiTypeSimpleModel that = (FiTypeSimpleModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return 31 * ((Object) id).hashCode();
    }
}
