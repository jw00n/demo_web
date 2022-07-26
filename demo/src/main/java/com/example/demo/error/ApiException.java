package com.example.demo.error;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ErrorType error;

    public ApiException(ErrorType e) {
        super(e.getMessage());
        this.error = e;
    }
    
}
