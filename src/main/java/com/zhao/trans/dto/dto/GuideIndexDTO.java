package com.zhao.trans.dto.dto;

import lombok.Data;

/**
 * 翻页索引
 */
@Data
public class GuideIndexDTO {
    private boolean hasNext;
    private Long nextCursor;
}
