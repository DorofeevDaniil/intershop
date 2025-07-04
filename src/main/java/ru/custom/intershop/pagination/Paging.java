package ru.custom.intershop.pagination;

import ru.custom.intershop.model.Item;

import java.util.List;

public class Paging {
    private final List<List<Item>> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;

    public Paging(List<List<Item>> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public Paging(List<List<Item>> content, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = 1;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public List<List<Item>> getContent() {
        return content;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public int pageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
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