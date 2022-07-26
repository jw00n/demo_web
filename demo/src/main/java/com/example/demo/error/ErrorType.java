package com.example.demo.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {
    ERR_0000_SUCCESS           ("0000", "정상적으로 처리되었습니다."),
    ERR_1000_INVALID_PARAMETER ("1000", "필수 파라미터가 누락되었습니다."),
    ERR_2000_EMPTY_USER_INFO   ("2000", "회원 정보가 없습니다."),
    ERR_9901_NETWORK_TIMEOUT   ("9901", "네트워크 응답시간이 초과되었습니다."),   
    ERR_9999_UNKNOWN_ERROR     ("9999", "알 수 없는 오류가 발생하였습니다."),

    ACCESS_TOKEN_EXCEPTION (HttpStatus.UNAUTHORIZED,"CODE A 401", "Access Token Denied"),
    REFRESH_TOKEN_EXCEPTION (HttpStatus.UNAUTHORIZED,"CODE R 401", "Refresh Token Denied")
    ;
    
    private String code;
    private String message;
    private HttpStatus status;
    
    //여기 밑에 따라가는군. 
    ErrorType(String code, String message) {
    	this.code    = code;
        this.message = message;
    }

    ErrorType(HttpStatus status,String code, String message) {
    	this.status   = status;
        this.code    = code;
        this.message = message;
    }
    
  
}
