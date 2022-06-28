package com.example.demo.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
	//2개의 필드 
	//
	@NonNull
	@Size(min=3, max = 50)
	private String username;
	
	@NonNull
	@Size(min =3 , max = 100)
	private String password;
	
}
 