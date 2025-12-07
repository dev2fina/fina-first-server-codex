package net.fina.common.shared;

import java.io.Serializable;

public class SortField implements Serializable {
    String property;
    String direction;

    private boolean asc;

    public SortField() {
    }

    public SortField(String property, String direction) {
        this.property = property;
        this.direction = direction;
        this.asc = ("asc".equalsIgnoreCase(direction));
    }

    public SortField(String property, boolean asc) {
        this.property = property;
        this.asc = asc;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDirection() {
        return direction == null ? "asc" : direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
        this.asc = ("asc".equalsIgnoreCase(direction));
    }

    public boolean isAsc() {
        return asc;
    }

    @Override
    public String toString() {
        return this.property + " " + this.direction;
    }
}
