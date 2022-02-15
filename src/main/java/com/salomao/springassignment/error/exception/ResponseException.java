package com.salomao.springassignment.error.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
public class ResponseException extends Exception implements Serializable {

    @Serial
    private static final long serialVersionUID = 4821234912274751946L;

    private final HttpStatus code;

    private final String message;


    public HttpStatus getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
