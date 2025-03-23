package com.zhao.trans.exception;

import com.zhao.trans.enums.ErrorEnum;
import lombok.Getter;

@Getter
public class TransException extends RuntimeException {
    private final Integer code;
    private final String message;

    public TransException(ErrorEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.message = errorEnum.getMessage();
    }

    public TransException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
