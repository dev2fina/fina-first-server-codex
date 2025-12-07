package net.fina.server.processing;

import java.util.Iterator;
import java.util.Set;

public class ListElementUtil {

    public static String truncateListElementMessage(Set<String> listElementValues) {
        if (listElementValues.isEmpty()) {
            return "[]";
        }

        //show only 3 element so error message will not grow
        StringBuilder shortenedMessage = new StringBuilder(" (");
        int maxElementSize = Math.min(listElementValues.size(), 3);
        Iterator<String> iterator = listElementValues.iterator();
        int counter = 0;
        while (iterator.hasNext() && counter < maxElementSize) {
            counter++;
            if (counter > 1) {
                shortenedMessage.append(", ");
            }
            Object value = iterator.next();
            if (value != null) {
                shortenedMessage.append(value);
            }
        }
        if (counter > 3) {
            shortenedMessage.append("... )");
        } else {
            shortenedMessage.append(")");
        }

        return shortenedMessage.toString();
    }
}
