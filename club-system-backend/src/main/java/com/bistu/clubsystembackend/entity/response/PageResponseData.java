package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponseData<T> {
    private List<T> records;
    private long total;
    private Integer pageNum;
    private Integer pageSize;

    public PageResponseData(List<T> records, long total) {
        this.records = records;
        this.total = total;
    }

    public PageResponseData(List<T> records, long total, Integer pageNum, Integer pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
