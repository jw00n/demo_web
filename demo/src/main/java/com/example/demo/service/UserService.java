package com.example.demo.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserInfoDto;
import com.example.demo.entity.Authority;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.SecurityUtil;

//회원가입, 유저정보조회등의 메소드를 위한
//어노테이션으로 mvc하는거 놀랍
@Service 
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	//회원가입 로직 수행 메소드
	@Transactional
	public User signup(UserDto userDto) {
		/*
		  'findOneWith...' 에서 findOne / With 로 나눠서 생각을 해주시면 좋을 것 같습니다. 
		  findOne은  Returns a single entity 의 의미이고,
		   With는 @EntityGraph 어노테이션과 관계가 있습니다.
		    authorities도 함께 Fetch 하라는 의미입니다.
			@EntityGraph 어노테이션을 검색해보시면 With에 대한 느낌이 오실 것으로 생각됩니다. 
		  */
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
		
			return new UserInfoDto(user.getUserId(), user.getUsername(),user.getNickname());
				
	}
	
	
	
}
