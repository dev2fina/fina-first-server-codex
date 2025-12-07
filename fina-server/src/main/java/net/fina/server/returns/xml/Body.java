package net.fina.server.returns.xml;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Body object of xml file to be constructed
 * <BODY>
 * <ITEM>
 * ...
 * </ITEM>
 * </BODY>
 *
 * @author dato.java
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BODY", propOrder = {"items"})
public class Body {
    @XmlElement(name = "ITEM")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }


}
