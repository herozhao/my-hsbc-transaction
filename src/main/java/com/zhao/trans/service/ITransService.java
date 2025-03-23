package com.zhao.trans.service;

import com.zhao.trans.entity.PageResult;
import com.zhao.trans.entity.Transaction;

/**
 * 交易服务接口
 */
public interface ITransService {

    boolean existByOutBizNo(String outBizNo);

    boolean existByTransactionNo(Long transactionNo);

    Long createTrans(String outBizNO, Transaction transaction);

    void deleteTrans(Long transaction);

     void updateTrans(Transaction transaction);

    PageResult<Transaction> listAllTrans(Long cursor, int pageSize);




}
