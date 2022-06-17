package com.example.demo.jwt;

import java.net.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);
	@Autowired
	private TokenProvider tokenProvider;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		logger.info("PermissionInterceptor - {}", "호출완료");
		// URL 뽑아내고
		// http://localhost:8080/index/userpage
		String reqUrl = request.getRequestURI().toString();
		String url = reqUrl.substring(reqUrl.indexOf(":") + 4);
		System.out.println(url);

		String token = request.getHeader("token"); // client에서 요청할 때 header에 넣어둔 "jwt-auth-token"이라는 키 값을 확인

		HttpSession session = request.getSession();
		if (url.contains("singup") || url.contains("login") ) { // 토큰이 없고
			if (session.getAttribute("userinfo") == null) { //login x null
				logger.info("PermissionInterceptor - {}", "login X " + url);
				session.invalidate(); // 정보삭제
				return true;
			} else { //login O 해당 주소로 접속하고 session이 있다면
				
				response.sendRedirect("/index/test");
				return false;
			}

		} else {
			if (token != null && token.length() > 0) {
				tokenProvider.validateToken(token);
				return true;
			} else { // 유효한 인증토큰이 아닐 경우
				response.sendRedirect("/index/test");
				return false;
				//throw new Exception("유효한 인증토큰이 존재하지 않습니다.");
			}
		}

	}
}
