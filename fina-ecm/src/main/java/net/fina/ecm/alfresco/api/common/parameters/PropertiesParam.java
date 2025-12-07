package net.fina.ecm.alfresco.api.common.parameters;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.ArrayList;
import java.util.List;

public class PropertiesParam implements BaseRepresentation {
    private List<String> propertyValues = new ArrayList<>(0);

    public PropertiesParam(List<String> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public List<String> getValues() {
        return propertyValues;
    }

    public void setValues(List<String> propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override
    public String toString() {
        return join(propertyValues, ",");
    }

    private static String join(List<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for (String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }
}
