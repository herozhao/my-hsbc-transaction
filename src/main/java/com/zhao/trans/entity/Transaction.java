package com.zhao.trans.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Transaction {
    //交易号
    private Long transactionNo;

    //转出方ID
    private Long fromAccountId;

    //转入方id
    private Long toAccountId;

    //金额
    private Long amount;

    //币种
    private String currency;


    //交易类型
    private Integer type;

    //状态
    private Integer status;


    private Date createTime;
    //创建人
    private Long creator;

    private Date updateTime;

    //更新人
    private Long updater;

    //备注
    private String remark;
}
