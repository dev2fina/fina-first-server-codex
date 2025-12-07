package net.fina.server.util;

import net.fina.server.i18n.helper.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 7/6/13
 * Time: 3:21 PM
 */
public class DescriptionResultFilter implements ResultFilter<Description, Description> {

    @Override
    public List<Description> filter(List<Description> result, Description value) {
        List<Description> filtered = new ArrayList<>();
        for (Description description : result) {
            boolean contains = false;
            for (Map.Entry<Long, String> e : value.getDescriptions().entrySet()) {
                String source = description.getDescription(e.getKey());
                if (contains(source, e.getValue())) {
                    contains = true;
                }
            }
            if (contains) {
                description.setEnableFilter(true);
                filtered.add(description);
            }
        }
        return filtered;
    }

    private boolean contains(String source, String value) {
        return source != null && value != null && source.toLowerCase().contains(value.toLowerCase());
    }
}
