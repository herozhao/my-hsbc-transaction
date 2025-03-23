package com.zhao.trans.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 交易号生成器
 */
@Component
public class TransIdGen {
    //默认从10000万号开始
    private final AtomicLong idGenerator = new AtomicLong(10000);

    private final ConcurrentHashMap<String, Long> outBizNoToTrasNoMap = new ConcurrentHashMap<>();

    public Long genTransId(String outBizNo) {
        if (!StringUtils.hasLength(outBizNo)) {
            return null;
        }
        return outBizNoToTrasNoMap.computeIfAbsent(outBizNo, a -> idGenerator.addAndGet(1));
    }
}
