package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.jwt.JwtFilter;
import com.example.demo.jwt.TokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RefreshController { 
    private final TokenProvider tokenProvider;

    
    // vaildate refresh 토큰 해줘야함.
    @PostMapping("/refresh")
    public ResponseEntity<String> recreateToken(
            @RequestHeader(value = JwtFilter.AUTHORIZATION_HEADER) String refreshToken) {
        String accessToken = tokenProvider.validateRefreshToken(refreshToken.substring(7)); //beartoken으로 보낼때 조심.
        
        // refresh token이 유효한 상태.
        if (accessToken != null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
            // 헤더에 넣어서 새로 발급

            return new ResponseEntity<>("ACCESS_TOKEN", httpHeaders, HttpStatus.OK);
        } else {
            // 만료된 상태
            return new ResponseEntity<>("기한 만료", HttpStatus.UNAUTHORIZED);
        }

    }
}
