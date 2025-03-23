package com.zhao.trans.exception;

import com.zhao.trans.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zhao.trans.enums.ErrorEnum.ARGUMENT_EXCEPTION;

/**
 * 异常处理拦截器
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandler {


    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception ex) {
        log.error("System error: ", ex);
        return ResponseEntity.internalServerError().body(
                new ErrorResponse(
                        5000,
                        "System busy,try",
                        null
                )
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(TransException.class)
    public ResponseEntity<ErrorResponse> handleTransactionException(TransException ex) {
        return ResponseEntity
                .ok(new ErrorResponse(
                        ex.getCode(),
                        ex.getMessage(),
                        null
                ));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, String> details = fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        ARGUMENT_EXCEPTION.getCode(),
                        ARGUMENT_EXCEPTION.getMessage(),
                        details
                )
        );
    }


}
