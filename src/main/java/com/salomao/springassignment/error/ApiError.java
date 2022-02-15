package com.salomao.springassignment.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiError {
    private Integer status;
    private String error;
    private String message;
}
