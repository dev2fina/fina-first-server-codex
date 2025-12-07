package net.fina.server.util;

import jakarta.persistence.criteria.From;
import net.fina.common.shared.SortField;

public class SortUtil {

    public static SortField constructSortField(String sortField, String sortDir) {
        if (sortField == null || sortField.isBlank() || sortDir == null || sortDir.isBlank()) {
            return null;
        }
        return new SortField(sortField, sortDir);
    }

    public static boolean isPropertyExistOnEntity(From<?,?> entity, String property) {
        try {
            entity.get(property);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
