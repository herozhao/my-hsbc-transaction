package com.zhao.trans.service;

import com.zhao.trans.entity.PageResult;
import com.zhao.trans.entity.Transaction;
import com.zhao.trans.enums.ErrorEnum;
import com.zhao.trans.exception.TransException;
import com.zhao.trans.utils.TransIdGen;
import com.zhao.trans.utils.TransactionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransServiceImplTest {
    @Mock
    private TransIdGen transIdGen;

    @InjectMocks
    private TransServiceImpl transService;

    //模拟db
    private ConcurrentSkipListMap<Long, Transaction> db;

    // 交易ID锁
    private ConcurrentMap<Long, ReentrantLock> transIdLock;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        db = new ConcurrentSkipListMap<>();
        transIdLock = new ConcurrentHashMap<>();

        privateFieldSet("db", db);
        privateFieldSet("transIdLock", transIdLock);

    }

    private void privateFieldSet(String fieldName, Object value) throws Exception {
        Field field = TransServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(transService, value);
    }

    @Test
    void createTrans_Succeed() throws Exception {

        String outBizNo = "obn12345";
        Transaction tx = TransactionUtils.createTransaction();
        Long transactionNo = 1001L;
        when(transIdGen.genTransId(outBizNo)).thenReturn(transactionNo);


        Long result = transService.createTrans(outBizNo, tx);


        assertEquals(transactionNo, result);
        assertSame(tx, db.get(transactionNo));

        assertFalse(transIdLock.get(transactionNo).isLocked());
    }

    @Test
    void createTrans_MockLockFail() throws Exception {

        Transaction tx = TransactionUtils.createTransaction();
        String outBizNo = "obz1235";
        Long transactionNo = 1001L;
        when(transIdGen.genTransId(outBizNo)).thenReturn(transactionNo);


        ReentrantLock realLock = new ReentrantLock();
        ReentrantLock lockSpy = Mockito.spy(realLock);
        transIdLock.put(transactionNo, lockSpy);


        doReturn(false).when(lockSpy).tryLock();

        assertThrows(TransException.class,
                () -> transService.createTrans("obz1235", new Transaction()),
                "CONCURRENCY_EXCEPTION"
        );


        assertNull(db.get(transactionNo));

        assertFalse(transIdLock.get(transactionNo).isLocked());
    }


    @Test
    void deleteTrans_NormalSuccess() {
        // 准备数据
        String outBizNo = "obz1235";
        Transaction tx = TransactionUtils.createTransaction();
        Long transactionNo = 1001L;
        when(transIdGen.genTransId(outBizNo)).thenReturn(transactionNo);

        // 执行
        Long result = transService.createTrans(outBizNo, tx);
        assertNotNull(db.get(result));
        transService.deleteTrans(result);
        assertNull(db.get(result));

        assertFalse(transIdLock.containsKey(result));
    }

    @Test
    void deleteTrans_NotExist() {

        Transaction tx = TransactionUtils.createTransaction();
        Long transactionNo = tx.getTransactionNo();

        assertThrows(TransException.class,
                () -> transService.deleteTrans(transactionNo),
                ErrorEnum.TRANSACTION_NOT_EXISTS_EXCEPTION.getMessage()
        );
        assertNull(db.get(transactionNo));

        assertFalse(transIdLock.containsKey(transactionNo));
    }

    @Test
    void deleteTrans_LockFail() {
        String outBizNo = "obz1235";
        Transaction trans = TransactionUtils.createTransaction();
        Long transactionNo = 1001L;
        when(transIdGen.genTransId(outBizNo)).thenReturn(transactionNo);

        Long result = transService.createTrans(outBizNo, trans);
        assertNotNull(db.get(result));


        ReentrantLock realLock = new ReentrantLock();
        ReentrantLock lockSpy = Mockito.spy(realLock);
        transIdLock.put(transactionNo, lockSpy);


        doReturn(false).when(lockSpy).tryLock();

        assertThrows(TransException.class,
                () -> transService.deleteTrans(transactionNo),
                ErrorEnum.CONCURRENCY_EXCEPTION.getMessage()
        );
        assertNotNull(db.get(result));
        verify(lockSpy, never()).unlock();
    }


    @Test
    void updateTrans_Nomalsuccess() {
        // 准备数据
        String outBizNo = "obz1235";
        Transaction tx = TransactionUtils.createTransaction();
        Long transactionNo = 1001L;
        when(transIdGen.genTransId(outBizNo)).thenReturn(transactionNo);

        // 执行
        Long result = transService.createTrans(outBizNo, tx);
        assertNotNull(db.get(result));

        Transaction newTx = TransactionUtils.createTransaction();
        newTx.setTransactionNo(transactionNo);
        newTx.setCreateTime(new Date(1));

        transService.updateTrans(newTx);
        assertEquals(db.get(result), newTx);


        assertFalse(transIdLock.get(result).isLocked());
    }


    @Test
    void updateTrans_NotExist() {
        // 准备数据

        Transaction newTx = TransactionUtils.createTransaction();
        // 执行并验证异常
        assertThrows(TransException.class,
                () -> transService.updateTrans(newTx),
                ErrorEnum.TRANSACTION_NOT_EXISTS_EXCEPTION.getMessage()
        );
        assertNull(db.get(newTx.getTransactionNo()));

        assertNull(transIdLock.get(newTx.getTransactionNo()));
    }

    @Test
    void updateTrans_lock_record() {

        String outBizNo = "obz1235";
        Transaction tx = TransactionUtils.createTransaction();
        Long transactionNo = 1001L;
        when(transIdGen.genTransId(outBizNo)).thenReturn(transactionNo);

        Long result = transService.createTrans(outBizNo, tx);
        assertNotNull(db.get(result));

        Transaction newTx = TransactionUtils.createTransaction();
        newTx.setTransactionNo(transactionNo);
        newTx.setCreateTime(new Date(1));


        ReentrantLock realLock = new ReentrantLock();
        ReentrantLock lockSpy = Mockito.spy(realLock);
        transIdLock.put(transactionNo, lockSpy);

        doReturn(false).when(lockSpy).tryLock();

        assertThrows(TransException.class,
                () -> transService.updateTrans(newTx),
                ErrorEnum.CONCURRENCY_EXCEPTION.getMessage()
        );
        assertEquals(db.get(result), tx);

        assertFalse(transIdLock.get(result).isLocked());
    }

    @Test
    void listAllTrans() {
        createTransactions();

        PageResult<Transaction> result = transService.listAllTrans(null, 100);
        assertFalse(result.isHasNext());
        Transaction last = null;
        for (Transaction transaction : result.getItems()) {
            if (last == null) {
                last = transaction;
            } else {
                assertTrue(last.getTransactionNo().compareTo(transaction.getTransactionNo()) < 0);

            }
        }
    }


    @Test
    void listAllTrans_Success() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        transService.listAllTrans(null, 10);
        executorService.shutdown();
    }

    @Test
    void listAllTrans_page() {
        List<Transaction> transactions = createTransactions();
        Transaction cursorTransaction = transactions.get(4);
        PageResult<Transaction> result = transService.listAllTrans(cursorTransaction.getTransactionNo(), 10);

        assertTrue(result.isHasNext());
        Transaction last = null;
        for (Transaction transaction : result.getItems()) {
            if (last == null) {
                last = transaction;
            } else {
                assertTrue(last.getTransactionNo().compareTo(transaction.getTransactionNo()) < 0);

            }
        }
        assertTrue(last.getTransactionNo().compareTo(cursorTransaction.getTransactionNo()) > 0);

    }

    private List<Transaction> createTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Transaction transaction = TransactionUtils.createTransaction();
            transaction.setCreateTime(new Date(i * 1000));
            transactions.add(transaction);
            String serialNumber = String.valueOf(i * 2);
            when(transIdGen.genTransId(serialNumber)).thenReturn(Long.parseLong(serialNumber));
            transService.createTrans(serialNumber, transaction);

            Transaction transaction1 = TransactionUtils.createTransaction();
            transaction1.setCreateTime(transaction.getCreateTime());
            transactions.add(transaction1);
            serialNumber = String.valueOf(i * 2 + 1);
            when(transIdGen.genTransId(serialNumber)).thenReturn(Long.parseLong(serialNumber));
            transService.createTrans(serialNumber, transaction1);
        }
        return transactions;
    }


    @Test
    void existByTransactionNo() {
        Long txNo = 123L;
        db.put(txNo, TransactionUtils.createTransaction());
        assertTrue(transService.existByTransactionNo(txNo));
        assertFalse(transService.existByTransactionNo(1234L));

    }


}