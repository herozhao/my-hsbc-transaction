package com.zhao.trans.validator;

import com.zhao.trans.dto.request.TransCreateRequest;
import com.zhao.trans.enums.ErrorEnum;
import com.zhao.trans.exception.TransException;
import com.zhao.trans.service.ITransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransValidator {
    @Autowired
    private ITransService iTransService;

    public void validate(TransCreateRequest request) {

        if (iTransService.existByOutBizNo(request.outBizNo())) {
            throw new TransException(ErrorEnum.CREATE_DUPLICATE_EXCEPTION);
        }
    }

    public void validate(Long transactionNo) {
        if (!iTransService.existByTransactionNo(transactionNo)) {
            throw new TransException(ErrorEnum.TRANSACTION_NOT_EXISTS_EXCEPTION);
        }
    }
}
