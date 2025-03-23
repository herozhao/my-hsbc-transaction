package com.zhao.trans.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhao.trans.dto.dto.GuideIndexDTO;
import com.zhao.trans.dto.dto.TransDTO;
import com.zhao.trans.dto.request.TransCreateRequest;
import com.zhao.trans.dto.request.TransUpdateRequest;
import com.zhao.trans.dto.response.PageData;
import com.zhao.trans.entity.PageResult;
import com.zhao.trans.entity.Transaction;
import com.zhao.trans.service.ITransService;
import com.zhao.trans.utils.BeanConverter;
import com.zhao.trans.utils.TransactionUtils;
import com.zhao.trans.validator.TransValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TransController.class)
public class TransControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ITransService transactionService;

    @MockitoBean
    private BeanConverter beanConverter;

    @MockitoBean
    private TransValidator transactionValidator;

    @Test
    void createTransaction() throws Exception {
        // 准备测试数据
        String serialNum = "1";
        TransCreateRequest request = TransactionUtils.createRequest(serialNum);
        // 设置其他必要字段...

        Transaction transaction = TransactionUtils.createTransaction();
        Long transactionNo = transaction.getTransactionNo();
        TransDTO transDTO = new TransDTO();

        transDTO.setTransactionNo(transactionNo);
        // 模拟依赖行为
        Mockito.doNothing().when(transactionValidator).validate(request);
        Mockito.when(beanConverter.toTransEntity(request)).thenReturn(transaction);
        Mockito.when(transactionService.createTrans(request.outBizNo(), transaction)).thenReturn(transactionNo);
        Mockito.when(beanConverter.toTransDto(transaction)).thenReturn(transDTO);

        // 执行请求并验证
        mockMvc.perform(post("/api/v1/trans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/trans/" + transactionNo))
                .andExpect(jsonPath("$.data.transDTO.transactionNo").value(transactionNo));

        // 验证依赖调用
        Mockito.verify(transactionValidator).validate(request);
        Mockito.verify(transactionService).createTrans(serialNum, transaction);
    }

    @Test
    void updateTransaction() throws Exception {
        // 准备测试数据
        Long transactionNo = 1001L;
        TransUpdateRequest request = TransactionUtils.updateRequest(transactionNo);

        Transaction transaction = TransactionUtils.createTransaction();
        transaction.setTransactionNo(transactionNo);
        TransDTO transDTO = TransactionUtils.createTransactionDTO();
        transDTO.setTransactionNo(transactionNo);
        // 模拟依赖行为
        Mockito.doNothing().when(transactionValidator).validate(transactionNo);
        Mockito.when(beanConverter.toTransEntity(request)).thenReturn(transaction);
        Mockito.when(beanConverter.toTransDto(transaction)).thenReturn(transDTO);

        // 执行请求并验证
        mockMvc.perform(put("/api/v1/trans/{transactionNo}", transactionNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.transDTO.transactionNo").value(transactionNo));

        // 验证依赖调用
        Mockito.verify(transactionService).updateTrans(transaction);
    }

    @Test
    void getAllTransactions() throws Exception {
        // 准备分页数据
        Long cursorString = 123L;

        int pageSize = 20;
        PageResult<Transaction> pageResult = new PageResult<>();
        Transaction transaction = TransactionUtils.createTransaction();
        pageResult.setItems(List.of(transaction));
        Long nextCursorString = 234L;
        pageResult.setNextCursor(nextCursorString);
        pageResult.setHasNext(true);
        PageData<TransDTO> pageData = new PageData<>();
        TransDTO transDTO = TransactionUtils.createTransactionDTO();
        pageData.setItems(List.of(transDTO));
        GuideIndexDTO guideIndexDTO = new GuideIndexDTO();
        guideIndexDTO.setHasNext(pageResult.isHasNext());
        guideIndexDTO.setNextCursor(pageResult.getNextCursor());
        pageData.setIndex(guideIndexDTO);
        // 模拟依赖行为

        Mockito.when(transactionService.listAllTrans(cursorString, pageSize)).thenReturn(pageResult);
        Mockito.when(beanConverter.toPages(pageResult)).thenReturn(pageData);

        // 执行请求并验证
        mockMvc.perform(get("/api/v1/trans")
                        .param("pageSize", "20")
                        .param("cursor", String.valueOf(cursorString)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.index.nextCursor").value(nextCursorString));
    }

    @Test
    void deleteTransaction() throws Exception {
        Long transactionNo = 100L;

        mockMvc.perform(delete("/api/v1/trans/{transactionNo}", transactionNo))
                .andExpect(status().isNoContent());
        Mockito.verify(transactionValidator).validate(transactionNo);
        Mockito.verify(transactionService).deleteTrans(transactionNo);
    }
}