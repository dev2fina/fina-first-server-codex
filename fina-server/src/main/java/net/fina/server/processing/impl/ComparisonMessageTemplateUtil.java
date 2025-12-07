package net.fina.server.processing.impl;

import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.reg.model.InputMetaModel;
import org.stringtemplate.v4.ST;

public class ComparisonMessageTemplateUtil {

    public String process(ComparisonItem comparisonItem, ProcessItem item, Object... params) {
        ST st = new ST(comparisonItem.messageTemplate);
        st.add("comparison", comparisonItem);
        st.add("item", item);
        if (params.length > 2) {
            st.add("itemValue", params[2]);

            if (params.length > 4) {
                st.add("compValue", params[4]);
            }

            if (params.length > 6) {
                st.add("rowNumber", params[6]);
            }
        }
        return st.render();
    }

    public String process(ComparisonItem comparisonItem, InputMetaModel item, Object... params) {
        ST st = new ST(comparisonItem.messageTemplate);
        st.add("comparison", comparisonItem);
        st.add("item", item);
        if (params.length > 2) {
            st.add("itemValue", params[2]);

            if (params.length > 4) {
                st.add("compValue", params[4]);
            }

            if (params.length > 6) {
                st.add("rowNumber", params[6]);
            }
        }
        return st.render();
    }
}
