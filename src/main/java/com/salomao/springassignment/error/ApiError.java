package com.salomao.springassignment.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;
@Data
@ToString
@AllArgsConstructor
public class ApiError {
    private Integer status;
    private String error;
    private String message;
}
