package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserInfoDto;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtFilter;
import com.example.demo.service.UserService;

//UserService의 메소드를 호출할 UserController 
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = {"/api"}, method = {RequestMethod.POST, RequestMethod.GET})
public class UserController {
	private final UserService userService;
	private final HttpSession httpSession;
	
	public UserController(UserService userService, HttpSession httpSession) {
		this.userService = userService;
		this.httpSession = httpSession;
		
	}
	
	
	@PostMapping("/signup") // /api/signup
	public ResponseEntity<User> signup(@Valid@RequestBody   UserDto userDto)//@RequestBody
	{ //userservice의 signup 메소드 호
		return ResponseEntity.ok(userService.signup(userDto));
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<UserInfoDto> getMyUserInfo(){
		UserInfoDto userinfodto = new UserInfoDto(
			userService.getUserWithAuthorities().get().getUserId(),
			userService.getUserWithAuthorities().get().getUsername(),
			userService.getUserWithAuthorities().get().getNickname()
			);
		return ResponseEntity.ok(userinfodto);
	}//두가지 권한 모두 허용, 
	
	@GetMapping("/user/{username}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<User> getUserInfo(@PathVariable String username){
		
		return ResponseEntity.ok(userService.getUserWithAuthorities(username).get());
	}//admin만 허용 //username 파라미터를 기준으로 유저정보와 권한 정보를 리턴하는 api 
	
}
