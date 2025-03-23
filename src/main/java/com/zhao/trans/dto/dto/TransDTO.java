package com.zhao.trans.dto.dto;

import lombok.Data;

/**
 * 对外dto
 */
@Data
public class TransDTO {
    //编号
    private Long transactionNo;
    //转出Id
    private Long fromAccountId;
    //转入ID
    private Long toAccountId;

    private Long createTime;
    //创建人
    private Long creator;

    private Long updateTime;
    //修改人
    private Long updater;
    //资金金额
    private Long amount;

    //类型
    private Integer type;
    //状态
    private Integer status;
    //币种
    private String currency;
    //备注
    private String remark;

}
