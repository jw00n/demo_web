package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserInfoDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

//회원가입 컨트롤러

@Controller
@RequiredArgsConstructor
@RequestMapping(value = { "/index" })
public class IndexController {
	private final HttpSession httpSession;
	private ResponseEntity<User> userInfo;

	@GetMapping("/signup") // 회원가입
	public String index() throws IOException {
		
		return "/index.html";
	}

	@GetMapping("/login") // 로그인
	public String login() throws IOException {
		return "login";
	}

	// 테스트 단 ==================

	@GetMapping("/test")
	public String test(Model model) throws IOException {
		
		if(httpSession.getAttribute("userinfo")!=null) {
		userInfo=
				(ResponseEntity<User>) httpSession.getAttribute("userinfo");
		
		String nickname=userInfo.getBody().getNickname();
		model.addAttribute("nickname", nickname); // 여기서 머스테치로 넘어가줌.
		}
		return "test";
	}

	//에러페이지 ㅎㅎ..
	@GetMapping("/home")
	public String errorPage() throws IOException{
		return "home";
	}

	//에러페이지 ㅎㅎ..
	@GetMapping("/logout")
	public String logout() throws IOException{
		httpSession.invalidate(); //로그아웃 접근할때 세션정보 삭제
		return "test";
	}
	
	@GetMapping("/userpage")
	public String userpage(Model model) throws IOException{
		
		if(httpSession.getAttribute("userinfo")!=null) {
			ResponseEntity<User> userInfo=
					(ResponseEntity<User>) httpSession.getAttribute("userinfo");
			
			//이런 소소한 정보 테이블은 따로 있어야겟다
			String username=userInfo.getBody().getUsername();
			String nickname=userInfo.getBody().getNickname();
			
			model.addAttribute("username", username);
			model.addAttribute("nickname", nickname);
			return "userpage";
		}else {
			return "test";
		}
		
	}
}

