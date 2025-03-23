package com.zhao.trans.dto.response;


import lombok.Data;

@Data
public class BaseResponse <T> {
    private T data;
    private int code = 200;
    private String message = "success";

}
