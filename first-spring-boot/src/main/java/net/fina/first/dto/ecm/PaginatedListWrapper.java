package net.fina.first.dto.ecm;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedListWrapper<T> {
    private List<T> list;
    private Integer totalResults;
    private Integer pageSize;
    private Integer pageNumber;

    public static <T> PaginatedListWrapper<T> of(List<T> list, int totalResults, int pageSize, int pageNumber) {
        PaginatedListWrapper<T> wrapper = new PaginatedListWrapper<>();
        wrapper.setList(list);
        wrapper.setTotalResults(totalResults);
        wrapper.setPageSize(pageSize);
        wrapper.setPageNumber(pageNumber);
        return wrapper;
    }
}
