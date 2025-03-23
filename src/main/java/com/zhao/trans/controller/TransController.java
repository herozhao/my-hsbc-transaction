package com.zhao.trans.controller;


import com.zhao.trans.dto.dto.TransDTO;
import com.zhao.trans.dto.request.TransCreateRequest;
import com.zhao.trans.dto.request.TransUpdateRequest;
import com.zhao.trans.dto.response.BaseResponse;
import com.zhao.trans.dto.response.PageData;
import com.zhao.trans.dto.response.TransCreateData;
import com.zhao.trans.entity.PageResult;
import com.zhao.trans.entity.Transaction;
import com.zhao.trans.service.ITransService;
import com.zhao.trans.utils.BeanConverter;
import com.zhao.trans.validator.TransValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/trans")
@Slf4j
public class TransController {

    @Autowired
    private BeanConverter beanConverter;
    @Autowired
    private TransValidator transValidator;
    @Autowired
    private ITransService iTransService;


    @GetMapping
    public BaseResponse<PageData<TransDTO>> getAllTransactions(@RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize, @RequestParam(required = false) Long cursor) {
        PageResult<Transaction> transactionCursorPageResult = iTransService.listAllTrans(cursor, pageSize);
        PageData<TransDTO> transactionDTOPageData = beanConverter.toPages(transactionCursorPageResult);
        BaseResponse<PageData<TransDTO>> response = new BaseResponse<>();
        response.setData(transactionDTOPageData);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse<TransCreateData>> createTransaction(@RequestBody @Valid TransCreateRequest request) {
        transValidator.validate(request);
        Transaction transaction = beanConverter.toTransEntity(request);
        Long transactionNo = iTransService.createTrans(request.outBizNo(), transaction);
        BaseResponse<TransCreateData> response = new BaseResponse<>();
        TransCreateData data = new TransCreateData();
        TransDTO transDTO = beanConverter.toTransDto(transaction);
        data.setTransDTO(transDTO);
        response.setData(data);
        return ResponseEntity.created(URI.create("/trans/" + transactionNo)).body(response);
    }

    // 删除交易
    @DeleteMapping("/{transactionNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable @NotNull Long transactionNo) {
        transValidator.validate(transactionNo);
        iTransService.deleteTrans(transactionNo);
    }


    @PutMapping("/{transactionNo}")
    public BaseResponse<TransCreateData> updateTransaction(@PathVariable Long transactionNo, @Valid @RequestBody TransUpdateRequest request) {
        transValidator.validate(transactionNo);
        Transaction transaction = beanConverter.toTransEntity(request);
        transaction.setTransactionNo(transactionNo);
        iTransService.updateTrans(transaction);
        BaseResponse<TransCreateData> response = new BaseResponse<>();
        TransCreateData data = new TransCreateData();
        TransDTO transDTO = beanConverter.toTransDto(transaction);
        data.setTransDTO(transDTO);
        response.setData(data);
        return response;
    }


}
