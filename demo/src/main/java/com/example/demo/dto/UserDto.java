package com.example.demo.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto { //회원가입용
	
	//size를 사용하면 테이블이 생성이 되는건가.
	@NotNull
	@Size(min=3, max = 50)
	private String username;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotNull
	@Size(min = 3,max = 100)
	private String password;
	
	@NotNull
	@Size(min=3, max=50)
	private String nickname;

}
