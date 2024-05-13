package com.agileactors.fintech.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Data
public class ResponseResult<T> {
    private T data;
    private HttpStatus status;
    private String message;
}
