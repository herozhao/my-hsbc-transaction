package com.zhao.trans.enums;

public enum ErrorEnum {


    CREATE_DUPLICATE_EXCEPTION(1001, "重复创建"),
    TRANSACTION_NOT_EXISTS_EXCEPTION(1002, "交易不存在"),

    ARGUMENT_EXCEPTION(4000, "参数异常"),

    SYSTEM_EXCEPTION(5000, "系统异常"),
    CONCURRENCY_EXCEPTION(6000, "并发冲突"),

    ;

    private final int code;
    private final String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
