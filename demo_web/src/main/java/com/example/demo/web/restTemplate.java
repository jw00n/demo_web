package com.example.demo.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class restTemplate {

	@Autowired ObjectMapper objectMapper;
	
	private void restTemplateTest() throws Exception {
		RestTemplate restTemplate=new RestTemplate();
		String url="http://localhost:8081/api/signup";
		
		//헤더
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		//바디
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", "test");
		body.add("password", "1234");
		body.add("nickname", "momo");

		//컴바인 메시지
		HttpEntity<?> requestMessage = new HttpEntity<>(body,httpHeaders);
		
		//리퀘스트 and getResponse
		HttpEntity<String> response = restTemplate.postForEntity(url, requestMessage, String.class);
		
		//response body 파싱
		objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
	//			)
		
		
		
		
	}
}
