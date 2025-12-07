package net.fina.server.processing.dependency;

import java.util.ArrayList;
import java.util.List;

public class DependencyItem implements Comparable<DependencyItem> {

    public enum Status {
        UNKNOWN, IN_PROGRESS, FINISHED
    }

    public List<Long> dependentIds = new ArrayList<>();
    public long id;
    public long externalId;
    public String code;
    public Status status;
    public Integer start;
    public Integer end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DependencyItem item = (DependencyItem) o;

        if (id != item.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Item{" +
                "dependentIds=" + dependentIds +
                ", id=" + id +
                ", code='" + code + '\'' +
                ", status=" + status +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    @Override
    public int compareTo(DependencyItem o) {
        return Integer.compare(end, o.end);
    }
}