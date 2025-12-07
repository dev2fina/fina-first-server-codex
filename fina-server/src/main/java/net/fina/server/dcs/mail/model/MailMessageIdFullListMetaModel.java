package net.fina.server.dcs.mail.model;

import java.util.ArrayList;
import java.util.List;

public class MailMessageIdFullListMetaModel {
    private static final int DEFAULT_PAGE_SIZE = 1000;

    public MailMessageIdFullListMetaModel() {
        setPageSize(DEFAULT_PAGE_SIZE);
        setTotalResults(0);
        setCurrentPage(0);
        setSortDirections("");
        setSortFields("");
        setList(new ArrayList<>());
    }

    private Integer currentPage;

    private Integer pageSize;

    private Integer totalResults;

    private String sortFields;
    private String sortDirections;

    private List<String> list;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public String getSortFields() {
        return sortFields;
    }

    public void setSortFields(String sortFields) {
        this.sortFields = sortFields;
    }

    public String getSortDirections() {
        return sortDirections;
    }

    public void setSortDirections(String sortDirections) {
        this.sortDirections = sortDirections;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
