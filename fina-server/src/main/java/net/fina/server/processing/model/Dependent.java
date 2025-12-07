package net.fina.server.processing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Dependent implements Serializable {

    public enum Status {
        UNKNOWN, IN_PROGRESS, FINISHED
    }
    public List<Long> dependentIds = new ArrayList<>();
    public Status status;
    public Integer start;
    public Integer end;

    @Override
    public String toString() {
        return "Dependent [start=" + start + ", end=" + end + "]";
    }
}
