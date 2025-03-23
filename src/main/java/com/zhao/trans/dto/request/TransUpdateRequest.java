package com.zhao.trans.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 修改请求
 * @param fromAccountId
 * @param toAccountId
 * @param amount
 * @param currency
 * @param remark
 * @param status
 * @param type
 * @param creator
 * @param createTime
 * @param updater
 * @param updateTime
 */
public record TransUpdateRequest(
        @NotNull Long fromAccountId,
        @NotNull Long toAccountId,
        @NotNull @Positive Long amount,
        @NotBlank @Size(max = 3) String currency,

        String remark,
        @NotNull Integer status,
        @NotNull Integer type,

        @NotNull Long creator,

        Long createTime,

        @NotNull Long updater,

        Long updateTime
) {}
