package com.example.demo.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserInfoDto;
import com.example.demo.entity.Authority;
import com.example.demo.entity.User;
import com.example.demo.error.ApiException;
import com.example.demo.error.ErrorType;

import com.example.demo.jwt.TokenProvider;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.RedisUtil;
import com.example.demo.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

//회원가입, 유저정보조회등의 메소드를 위한
//어노테이션으로 mvc하는거 놀랍
@RequiredArgsConstructor
@Service 
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisUtil redisUtil;

	
	//General 회원가입 로직 수행 메소드
	@Transactional
	public User signup(UserDto userDto) {
	
		if(userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) !=null) {
			throw new RuntimeException("이미 가입되어있는 유저.");
		}
		//DB에 존재하지 않는 username일 경우 aurthority와 user정보를 생성해서 
		Authority authority = Authority.builder()
				.authorityName("ROLE_USER")
				.build();
		User user = User.builder()
				.username(userDto.getUsername())
				.password(passwordEncoder.encode(userDto.getPassword()))
				.nickname(userDto.getNickname())
				.authorities(Collections.singleton(authority))
				.activated(true)
				.build();
		
		//save메소드를 통해 저장
		
		return userRepository.save(user);
		
		//signup 메소드를 통해 가입한 회원은 user Role을 가지고 있고 
		//admin계정은 자동생성되서 user,admin 둘다 가지고 있음. 
		//-> 이를 통해 권한 검증 테스트 가능.
		
	}
	
	
	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthorities(String username){
		return userRepository.findOneWithAuthoritiesByUsername(username);
		
	}
	
	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthorities(){
		return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
		//이름으로 권한 검색하는 
	}
	
	
	/////////////////////////////////////////////////////////////
	@Transactional(readOnly = true)
	public UserInfoDto findByUserId(Long id){
		User user = userRepository.findByUserId(id).orElseThrow(()-> new IllegalArgumentException("해당 사용자가 없습니다. id= "+id));
		
			return new UserInfoDto(user.getUserId(), user.getUsername(), user.getNickname());
				
	}

	//============= 로그아웃 ================
	@Transactional
	public String logout(String accessToken,String refreshToken){
		
		if(!tokenProvider.validateToken(accessToken)){
			throw new ApiException(ErrorType.ACCESS_TOKEN_EXCEPTION);
		}

		//accessToken에서 authentication
		Authentication authentication = tokenProvider.getAuthentication(accessToken);
		
		//DB에서 삭제
		String username= authentication.getName();
		System.out.println("Check username : " + username);
		refreshTokenRepository.deleteByUsername(username);

		//블랙리스트에 추가
		Long expiration = tokenProvider.getExpiration(refreshToken);
		redisUtil.setBlackList(accessToken, "accessToken",expiration);

		return "success";
	}
}
