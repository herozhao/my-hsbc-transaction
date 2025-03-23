package com.zhao.trans.service;

import com.zhao.trans.entity.PageResult;
import com.zhao.trans.entity.Transaction;
import com.zhao.trans.exception.TransException;
import com.zhao.trans.utils.TransIdGen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.zhao.trans.enums.ErrorEnum.*;

/**
 * 交易服务接口实现
 */
@Service
@Slf4j
public class TransServiceImpl implements ITransService {

    //交易Id生成器
    @Autowired
    private TransIdGen transIdGen;

    //跳表模拟DB存储
    private final ConcurrentSkipListMap<Long, Transaction> db = new ConcurrentSkipListMap<>();

    //交易ID力度锁
    private final ConcurrentMap<Long, ReentrantLock> transIdLock = new ConcurrentHashMap<>();


    @Override
    public Long createTrans(String outBizNO, Transaction transaction) {
        if (!StringUtils.hasLength(outBizNO)) {
            return null;
        }
        transaction.setTransactionNo(transIdGen.genTransId(outBizNO));
        ReentrantLock reentrantLock = transIdLock.computeIfAbsent(transaction.getTransactionNo(), a -> new ReentrantLock());
        boolean locked = false;
        try {
            locked = reentrantLock.tryLock();
            if (!locked) {
                throw new TransException(CONCURRENCY_EXCEPTION);
            }
            if (db.containsKey(transaction.getTransactionNo())) {
                throw new TransException(CONCURRENCY_EXCEPTION);
            }

            db.put(transaction.getTransactionNo(), transaction);

        } catch (TransException e) {
            throw e;
        } catch (Exception e) {
            log.error("createTrans error, transNo:{}", transaction.getTransactionNo(), e);
            throw new TransException(SYSTEM_EXCEPTION);
        } finally {

            if (locked) {
                reentrantLock.unlock();
            }
        }
        return transaction.getTransactionNo();
    }

    @Override
    public void deleteTrans(Long transactionNo) {

        ReentrantLock reentrantLock = transIdLock.computeIfAbsent(transactionNo, a -> new ReentrantLock());
        boolean locked = false;

        Transaction removed = null;

        try {
            locked = reentrantLock.tryLock();
            if (!locked) {
                throw new TransException(CONCURRENCY_EXCEPTION);
            }
            if (!db.containsKey(transactionNo)) {
                transIdLock.remove(transactionNo, reentrantLock);
                throw new TransException(TRANSACTION_NOT_EXISTS_EXCEPTION);
            }
            removed = db.remove(transactionNo);

        } catch (TransException e) {
            throw e;
        } catch (Exception e) {
            log.error("deleteTrans error, transNo:{}", transactionNo, e);
            throw new TransException(SYSTEM_EXCEPTION);
        } finally {

            if (locked) {
                if (removed != null) {
                    transIdLock.remove(transactionNo, reentrantLock);
                }
                reentrantLock.unlock();
            }
        }
    }

    @Override
    public void updateTrans(Transaction transaction) {
        ReentrantLock reentrantLock = transIdLock.computeIfAbsent(transaction.getTransactionNo(), a -> new ReentrantLock());
        boolean locked = false;
        Transaction oldTransaction = null;
        try {
            locked = reentrantLock.tryLock();
            if (!locked) {
                throw new TransException(CONCURRENCY_EXCEPTION);
            }
            oldTransaction = db.get(transaction.getTransactionNo());
            if (oldTransaction == null) {
                transIdLock.remove(transaction.getTransactionNo(), reentrantLock);
                throw new TransException(TRANSACTION_NOT_EXISTS_EXCEPTION);
            }
            db.put(transaction.getTransactionNo(), transaction);

        } catch (TransException e) {
            throw e;
        } catch (Exception e) {
            log.error("updateTrans error, transactionNo:{}", transaction.getTransactionNo(), e);
            throw new TransException(SYSTEM_EXCEPTION);
        } finally {

            if (locked) {
                reentrantLock.unlock();
            }
        }
    }

    @Override
    public PageResult<Transaction> listAllTrans(Long cursor, int pageSize) {

        NavigableMap<Long, Transaction> subMap;

        if (cursor == null) {
            subMap = db;
        } else {
            subMap = db.tailMap(cursor, false);
        }

        List<Transaction> transactions = new ArrayList<>(pageSize);

        for (Map.Entry<Long, Transaction> entry : subMap.entrySet()) {
            if (transactions.size() > pageSize) {
                break;
            }
            Transaction tx = entry.getValue();
            transactions.add(tx);
        }
        PageResult<Transaction> result = new PageResult<>();
        List<Transaction> items = transactions.subList(0, Math.min(transactions.size(), pageSize));
        result.setItems(items);
        boolean hasNext = transactions.size() > pageSize;
        result.setHasNext(hasNext);
        if (hasNext) {
            result.setNextCursor(items.getLast().getTransactionNo());
        }
        return result;

    }


    @Override
    public boolean existByOutBizNo(String outBizNo) {
        if (!StringUtils.hasLength(outBizNo)) {
            return false;
        }
        return db.containsKey(transIdGen.genTransId(outBizNo));
    }

    @Override
    public boolean existByTransactionNo(Long transactionNo) {
        return db.containsKey(transactionNo);
    }

}
