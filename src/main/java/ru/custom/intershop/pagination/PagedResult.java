package ru.custom.intershop.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class PagedResult<T> {
    @Getter
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;

    public int pageNumber() {
        return pageNumber;
    }

    public int pageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / pageSize);
    }

    public boolean hasNext() {
        return pageNumber + 1 <= getTotalPages();
    }

    public boolean hasPrevious() {
        return pageNumber > 1;
    }
}