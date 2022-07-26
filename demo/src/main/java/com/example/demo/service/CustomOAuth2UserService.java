package com.example.demo.service;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OAuthAttributes;
import com.example.demo.dto.TokenDto;
import com.example.demo.entity.Authority;
import com.example.demo.entity.User;
import com.example.demo.jwt.TokenProvider;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Collections;
import java.util.Iterator;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private Authority authority;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 소셜 구별
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 필드값 구글은 sub
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        //확인 출력용
        System.out.println("registrationId=" + registrationId+"userNameAttributeName=" + userNameAttributeName);

        // attribute
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
                oAuth2User.getAttributes());

        int platformNum=macth(registrationId);
        
                //플랫폼 구별해야됨.
        if (userRepository.findByPlatformAndUsername(platformNum,attributes.getEmail()).orElse(null) != null) {
            // 가입 O 
          
            authority = userRepository.findOneWithAuthoritiesByPlatformAndUsername(
                attributes.getEmail(),attributes.getPlatform())
                .get().getAuthorities().iterator().next();

        } else { // 가입X
                  //플랫폼 확인도 필요할것같은데요.. 
            authority = Authority.builder()
                    .authorityName("ROLE_USER")
                    .build();
            User user = save(attributes, authority);

        }

        TokenDto jwt = tokenProvider.createOAuthToken(attributes, authority);
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("ACCESS_TOKEN", "Bearer " + jwt.getRefreshToken());
        httpHeaders.add("REFRESH_TOKEN", "Bearer " + jwt.getRefreshToken());

        // 두 개의 토큰을 반환하는
        new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);

        System.out.println("attributes: " + attributes.getNameAttributeKey()+ " attributes "+attributes.getAttributes());


        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(authority.getAuthorityName())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()); 
    }

    @Transactional
    private User save(OAuthAttributes attributes, Authority authority) {
        System.out.println(attributes.getPlatform() + "  <<platform: 넘버");

        User user = User.builder()
                .username(attributes.getEmail())
                .password(null)
                .nickname(attributes.getName())
                .authorities(Collections.singleton(authority))
                .platform(attributes.getPlatform())
                .activated(true)
                .build();

        return userRepository.save(user);
    }


    private int macth(String registrationId){
        int platformNum=1;

        if(registrationId.equals("naver")){
                platformNum=2;
        }else if(registrationId.equals("kakao")){
                platformNum=3;
        } 
        return platformNum;
    }
}