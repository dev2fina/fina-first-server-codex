package net.fina.server.returns.xml;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * XML file item object <ITEM> <ITEMCODE></ITEMCODE> <ROW></ROW> <VALUE></VALUE>
 * </ITEM>
 *
 * @author dato.java
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ITEM", propOrder = {"itemCode", "row", "value"})
public class Item {

    @XmlElement(name = "ITEMCODE", required = true)
    private String itemCode;
    @XmlElement(name = "ROW", required = true)
    private int row;
    @XmlElement(name = "VALUE", required = true, defaultValue = "")
    private String value;

    public Item() {

    }


    public Item(String code, int row, String value) {
        this.itemCode = code;
        this.row = row;
        this.value = value;
    }


    public String getItemCode() {
        return itemCode;
    }


    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }


    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "[itemCode=" + itemCode + ", row=" + row + ", value=" + value + "]";
    }

}
