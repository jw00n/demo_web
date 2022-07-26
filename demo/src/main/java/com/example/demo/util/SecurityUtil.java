package com.example.demo.util;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
	private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
	
	private SecurityUtil() {
		
	}
	
	//security context의 authentication 객체를 이용해 username을 리턴해주는 유틸성 메소드.
	public static Optional<String> getCurrentUsername() {
		
		// 객체가 저장되는 시점은 jwtfilter의 dofilter메소드에서 request가 들어올때 
		//securitycontext에 authentication객체를 저장해서 사용하게됨.
		final Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication ==null) {
			logger.debug("시큐리티 context에 인증정보가 없습니다.");
			return Optional.empty();
		}
		
		String username = null;
		if(authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
			username= springSecurityUser.getUsername();
			
		} else if(authentication.getPrincipal() instanceof String) {
			username= (String) authentication.getPrincipal();
		
		}
		
		return Optional.ofNullable(username);
		
		
		
	}
}
