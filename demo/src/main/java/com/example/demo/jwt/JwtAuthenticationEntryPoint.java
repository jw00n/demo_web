package com.example.demo.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
      // 유효한 자격증명을 제공하지 않고 접근하려 할때 401 메시지를 보내는 <<<<<<<에러를 리턴할 클래스 
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      
      // >>  잘못된 토큰을 보냈을때 나오는 에러코드 401
      
      
   }
}