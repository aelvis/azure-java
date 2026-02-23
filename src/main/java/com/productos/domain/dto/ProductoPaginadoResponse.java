package com.productos.domain.dto;

import java.util.List;

public class ProductoPaginadoResponse {
    private List<ProductoResponse> data;
    private int page;
    private int size;
    private long total;
    private int totalPages;

    public ProductoPaginadoResponse() {}

    public ProductoPaginadoResponse(List<ProductoResponse> data, int page, int size, long total) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    public List<ProductoResponse> getData() { return data; }
    public void setData(List<ProductoResponse> data) { this.data = data; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}