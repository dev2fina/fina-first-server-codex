package net.fina.server.i18n.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Description {

    private long nameStrId;

    private Map<Long, String> descriptions;

    private boolean enableFilter;

    public Description() {
        descriptions = new ConcurrentHashMap<Long, String>();
    }

    public Description(long langId, long nameStrId, String value) {
        descriptions = new ConcurrentHashMap<Long, String>();
        if (value != null) {
            descriptions.put(langId, value);
        }
        setNameStrId(nameStrId);
    }

    public void setDescription(Map<Long, String> descriptions) {
        this.descriptions = descriptions;
    }

    public void addDescription(long langId, String value) {
        descriptions.put(langId, value);
    }

    public String getDescription(long langId) {
        return descriptions.get(langId);
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public Map<Long, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<Long, String> descriptions) {
        this.descriptions = descriptions;
    }

    public boolean isEnableFilter() {
        return enableFilter;
    }

    public void setEnableFilter(boolean enableFilter) {
        this.enableFilter = enableFilter;
    }

    @Override
    public String toString() {
        return "Description [nameStrId=" + nameStrId + ", descriptions=" + descriptions + "]";
    }
}
