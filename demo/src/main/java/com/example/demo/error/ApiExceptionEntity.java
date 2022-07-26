package com.example.demo.error;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiExceptionEntity {
    private String code;
    private String message;


    @Builder
    public ApiExceptionEntity(HttpStatus status,String code, String message) {
        this.code = code;
        this.message=message;
    }
}
