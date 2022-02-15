package com.salomao.springassignment.error;

import lombok.Data;

@Data
public class ApiError {
    private Integer status;
    private String error;
    private String message;
}
