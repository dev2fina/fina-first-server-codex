package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Comparator;

/**
 * User: Chelomisha@fina2.net
 * Date: 10/3/13
 * Time: 6:26 PM
 */
public class DependenceModel implements Serializable, Comparable<DependenceModel> {
    private String id;
    private String name;
    private String returnType;
    private String[] dependentId = new String[]{};

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String[] getDependentId() {
        return dependentId;
    }

    public void setDependentId(String[] dependentId) {
        this.dependentId = dependentId;
    }

    @Override
    public int compareTo(DependenceModel o) {
        return name.compareTo(o.getName());
    }
}
