package net.fina.server.dcs.mail.model;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * vamekh on 9/7/16.
 */
public class MailMessageIdListMetaModel extends MailMessageIdListWrapper<String> {

    private static final int DEFAULT_PAGE_SIZE = 1000;

    public MailMessageIdListMetaModel() {
        setPageSize(DEFAULT_PAGE_SIZE);
        setTotalResults(0);
        setCurrentPage(0);
        setSortDirections("");
        setSortFields("");
        setList(new ArrayList<>());
    }

    @Override
    public void setList(List<String> list) {
        super.setList(list);
    }

    @Override
    @XmlElement
    public List<String> getList() {
        return super.getList();
    }
}
