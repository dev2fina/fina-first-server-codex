package net.fina.common.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PaginatedListWrapper<T> {

    private int currentPage;

    private int pageSize;

    private int totalResults;

    private String sortFields;
    private String sortDirections;

    private List<T> list;

    public PaginatedListWrapper() {
    }

    public PaginatedListWrapper(List<T> list, int pageSize, int totalResults) {
        this.pageSize = pageSize;
        this.totalResults = totalResults;
        this.list = list;
    }

    public PaginatedListWrapper(List<T> list, int pageSize, long totalResults) {
        this.pageSize = pageSize;
        this.totalResults = ((Number) totalResults).intValue();
        this.list = list;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalResults() {
        return totalResults;
    }

    @JsonProperty
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    @JsonIgnore
    public void setTotalResults(long totalResults) {
        this.totalResults = (int) totalResults;
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

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}