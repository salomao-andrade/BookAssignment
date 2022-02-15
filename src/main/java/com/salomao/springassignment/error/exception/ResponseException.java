package com.salomao.springassignment.error.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResponseException extends Exception implements Serializable {

    @Serial
    private static final long serialVersionUID = 4821234912274751946L;

    private final HttpStatus code;

    private final String message;

}
