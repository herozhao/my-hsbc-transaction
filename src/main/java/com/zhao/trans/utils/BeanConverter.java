package com.zhao.trans.utils;

import com.zhao.trans.dto.dto.GuideIndexDTO;
import com.zhao.trans.dto.dto.TransDTO;
import com.zhao.trans.dto.request.TransCreateRequest;
import com.zhao.trans.dto.request.TransUpdateRequest;
import com.zhao.trans.dto.response.PageData;
import com.zhao.trans.entity.PageResult;
import com.zhao.trans.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class BeanConverter {
    public Transaction toTransEntity(TransCreateRequest request) {
        if (request == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.fromAccountId());
        transaction.setToAccountId(request.toAccountId());
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setRemark(request.remark());
        transaction.setType(request.type());
        transaction.setStatus(request.status());
        Date date = new Date();
        if (Objects.isNull(request.createTime())) {
            transaction.setCreateTime(date);
        } else {
            transaction.setCreateTime(new Date(request.createTime()));
        }
        if (Objects.isNull(request.updateTime())) {
            transaction.setUpdateTime(date);
        } else {
            transaction.setCreateTime(new Date(request.createTime()));
        }

        transaction.setCreator(request.creator());
        transaction.setUpdater(request.updater());
        return transaction;
    }

    public Transaction toTransEntity(TransUpdateRequest request) {
        if (request == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.fromAccountId());
        transaction.setToAccountId(request.toAccountId());
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setRemark(request.remark());
        transaction.setType(request.type());
        transaction.setStatus(request.status());
        Date date = new Date();
        if (Objects.isNull(request.createTime())) {
            transaction.setCreateTime(date);
        } else {
            transaction.setCreateTime(new Date(request.createTime()));
        }
        if (Objects.isNull(request.updateTime())) {
            transaction.setUpdateTime(date);
        } else {
            transaction.setUpdateTime(new Date(request.createTime()));
        }

        transaction.setCreator(request.creator());
        transaction.setUpdater(request.updater());
        return transaction;
    }

    public TransDTO toTransDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransDTO dto = new TransDTO();
        dto.setTransactionNo(transaction.getTransactionNo());
        dto.setFromAccountId(transaction.getFromAccountId());
        dto.setToAccountId(transaction.getToAccountId());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setRemark(transaction.getRemark());
        dto.setType(transaction.getType());
        dto.setStatus(transaction.getStatus());
        dto.setCreateTime(transaction.getCreateTime().getTime());
        dto.setUpdateTime(transaction.getUpdateTime().getTime());
        dto.setCreator(transaction.getCreator());
        dto.setUpdater(transaction.getUpdater());
        return dto;
    }

    public PageData<TransDTO> toPages(PageResult<Transaction> result) {
        if (result == null) {
            return null;
        }
        PageData<TransDTO> data = new PageData<>();
        data.setIndex(toIndex(result.isHasNext(), result.getNextCursor()));
        List<TransDTO> transDTOList = result.getItems().stream().map(this::toTransDto).collect(Collectors.toList());
        data.setItems(transDTOList);
        return data;
    }

    public GuideIndexDTO toIndex(boolean hasNext, Long nextCursor) {
        GuideIndexDTO guideIndexDTO = new GuideIndexDTO();
        guideIndexDTO.setHasNext(hasNext);
        guideIndexDTO.setNextCursor(nextCursor);
        return guideIndexDTO;
    }
}
