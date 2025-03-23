package com.zhao.trans.repo;

import com.zhao.trans.utils.TransIdGen;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TransIdGenTest {

    @Test
    public void createTransactionNo_WithSameOutBizNo_ReturnsSameId() {
        TransIdGen idGen = new TransIdGen();
        String outBizNo = "obz123788";

        Long id1 = idGen.genTransId(outBizNo);
        Long id2 = idGen.genTransId(outBizNo);

        assertEquals(id1, id2);
    }



    @Test
    public void createTransactionNo_ConcurrentCase() throws InterruptedException {
        TransIdGen idGen = new TransIdGen();
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        int taskNum = threadCount * 10;
        CountDownLatch latch = new CountDownLatch(taskNum);
        String outBizNo = "outBizNo";


        for (int i = 0; i < taskNum; i++) {
            executor.submit(() -> {
                idGen.genTransId(outBizNo);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        Long expectedId = 10001L;
        assertEquals(expectedId, idGen.genTransId(outBizNo));
    }

    @Test
    void createTransactionNo_ConcurrentCase_DifferentOutBizNo() throws InterruptedException {
        TransIdGen idGen = new TransIdGen();
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        ConcurrentHashMap<Long, String> transactionNoSerialNoMap = new ConcurrentHashMap<>();
        AtomicBoolean isDuplicate = new AtomicBoolean(false);
        //请求不同 outBizNo
        for (int i = 0; i < threadCount; i++) {
            String outBizNo = "outBizNo_" + i;
            executor.submit(() -> {
                Long transactionNo = idGen.genTransId(outBizNo);
                String old = transactionNoSerialNoMap.putIfAbsent(transactionNo, outBizNo);
                if (Objects.nonNull(old)) {
                    isDuplicate.set(true);
                    log.error("isDuplicate, new:{}, old:{}", old, transactionNo);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
        assertFalse(isDuplicate.get());
    }


    @Test
    public void createTransactionNo_Null() {
        TransIdGen idGen = new TransIdGen();
        String outBizNo = "";
        assertNull(idGen.genTransId(outBizNo));
    }

    @Test
    public void createTransactionNo_GenDifferentIds() {
        TransIdGen idGen = new TransIdGen();
        Long id1 = idGen.genTransId("obz1");
        Long id2 = idGen.genTransId("obz2");

        assertNotEquals(id1, id2);
    }
}