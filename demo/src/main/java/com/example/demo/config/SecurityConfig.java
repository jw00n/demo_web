package com.example.demo.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.jwt.JwtAccessDeniedHandler;
import com.example.demo.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.jwt.JwtSecurityConfig;
import com.example.demo.jwt.TokenProvider;

@EnableWebSecurity //기본 웹보안
@EnableGlobalMethodSecurity(prePostEnabled = true) // 나중에 preAuthorize 어노테이션을 메소드 단위로 추가하기위해서 사용.
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	
	//jwt패키지에서 만든거 주입
	public SecurityConfig(
			TokenProvider tokenProvider,
			JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
			JwtAccessDeniedHandler jwtAccessDeniedHandler
			) {
		this.tokenProvider =tokenProvider;
		this.jwtAuthenticationEntryPoint=jwtAuthenticationEntryPoint;
		this.jwtAccessDeniedHandler=jwtAccessDeniedHandler;
	}
	
	//패스워드 인코더
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Override
	public void configure(WebSecurity web) {
		web
			.ignoring()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
			.antMatchers(
					 "/favicon.ico"
					,"/resources/**"
					);
	}
	
	//--------------만든 jwt 를 적용해보자 
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		  http
	          // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
	          .csrf().disable()
	          
	         
	          .exceptionHandling() //예외를 핸들링할때 
	          //만든 클래스를 핸들러로 추가해준다.
	          .authenticationEntryPoint(jwtAuthenticationEntryPoint)
	          .accessDeniedHandler(jwtAccessDeniedHandler)
	
	          // enable h2-console 설정
	          .and()
	          .headers()
	          .frameOptions()
	          .sameOrigin()
	
	          // 세션을 사용하지 않기 때문에 STATELESS로 설정
	          .and()
	          .sessionManagement()
	          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	
		   
	          //토큰이 없는 상태의 요청은 permitall
	          .and()
	          .authorizeRequests()
	          
	          .antMatchers("/","/resources/**").permitAll()
	          .antMatchers("/index/**").permitAll()
	          .antMatchers("/api/**").permitAll()
	          .antMatchers("/api/user/**").permitAll()
	          .antMatchers("/api/authenticate").permitAll()
	          .antMatchers("/api/signup").permitAll()
	          .anyRequest().authenticated()
	
	          .and()
	          .apply(new JwtSecurityConfig(tokenProvider));
		  		//jwtFilter를 addFilterBefore로 등록했더 JwtSecurityConfig 클래스도 적용
		  
	}
	
}
