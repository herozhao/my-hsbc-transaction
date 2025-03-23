package com.zhao.trans.entity;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> items;
    private boolean hasNext;
    private Long nextCursor;
}
