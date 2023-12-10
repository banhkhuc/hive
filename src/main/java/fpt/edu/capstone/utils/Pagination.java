package fpt.edu.capstone.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pagination {

    @JsonProperty(value = "page")
    private long currentPage;

    @JsonProperty(value = "size")
    private long pageSize;

    @JsonProperty(value = "totalPage")
    private long totalPage;

    @JsonProperty(value = "totalRecords")
    private long totalRecords;

    public Pagination() {
    }

    public Pagination(long currentPage, long pageSize, long totalPage, long totalRecords) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalRecords = totalRecords;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public Pagination setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public long getPageSize() {
        return pageSize;
    }

    public Pagination setPageSize(long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public Pagination setTotalPage(long totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public Pagination setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
        return this;
    }

    @Override
    public String toString() {
        return "Pagination{" + "currentPage=" + currentPage + ", pageSize=" + pageSize + ", totalPage=" + totalPage + ", totalRecords=" + totalRecords + '}';
    }
}