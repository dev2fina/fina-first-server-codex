package net.fina.common.client.rvc;

import java.util.List;

public class ReturnXmlMetaModel {

    private HeaderXmlMetaModel header;
    private List<ItemXmlMetaModel> items;

    public HeaderXmlMetaModel getHeader() {
        return header;
    }

    public void setHeader(HeaderXmlMetaModel header) {
        this.header = header;
    }

    public List<ItemXmlMetaModel> getItems() {
        return items;
    }

    public void setItems(List<ItemXmlMetaModel> items) {
        this.items = items;
    }
}
