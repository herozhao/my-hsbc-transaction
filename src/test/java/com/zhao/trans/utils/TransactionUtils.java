package com.zhao.trans.utils;


import com.zhao.trans.dto.dto.TransDTO;
import com.zhao.trans.dto.request.TransCreateRequest;
import com.zhao.trans.dto.request.TransUpdateRequest;
import com.zhao.trans.entity.Transaction;

import java.util.Date;

public class TransactionUtils {


    public static TransCreateRequest createRequest(String outBizNo) {
        return new TransCreateRequest(8000L, 9000L, 100L, "CNY", "trans", 0, 1, 1L, new Date().getTime(), 1L, new Date().getTime(), outBizNo);
    }

    public static TransUpdateRequest updateRequest(Long transactionNo) {
        return new TransUpdateRequest(8000L, 9000L, 500L, "CNY", "trans", 0, 1, 1L, new Date().getTime(), 1L, new Date().getTime());
    }

    public static Transaction createTransaction() {
        Transaction transaction = new Transaction();

        transaction.setTransactionNo(10001L);
        transaction.setFromAccountId(100L);


        transaction.setStatus(0);
        transaction.setCreateTime(new Date());
        transaction.setCreator(5000L);

        transaction.setCurrency("CNY");
        transaction.setRemark("trans");
        transaction.setType(1);

        transaction.setUpdater(6000L);
        transaction.setUpdateTime(new Date());

        transaction.setToAccountId(200L);
        transaction.setAmount(9000L);
        return transaction;
    }


    public static TransDTO createTransactionDTO() {
        TransDTO transaction = new TransDTO();
        transaction.setTransactionNo(10001L);
        transaction.setFromAccountId(9000L);

        transaction.setRemark("trans");

        transaction.setCreator(9000L);
        transaction.setUpdater(8000L);
        transaction.setUpdateTime(new Date().getTime());

        transaction.setType(1);
        transaction.setStatus(0);
        transaction.setCreateTime(new Date().getTime());

        transaction.setToAccountId(8000L);
        transaction.setAmount(1000L);
        transaction.setCurrency("CNY");
        return transaction;
    }


}
