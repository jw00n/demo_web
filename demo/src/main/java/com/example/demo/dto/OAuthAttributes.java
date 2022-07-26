package com.example.demo.dto;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.example.demo.entity.Authority;
import com.example.demo.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private int platform;
    

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, int platform) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.platform = platform;
        
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        
        if("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }else if("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    //구글 로그인 처리
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
       //출력용
        Set<String> set= attributes.keySet();
        for(String s : set){
            System.out.println(s +": "+attributes.get(s));
        }

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .platform(1)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    //네이버 로그인 처리
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .platform(2)
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }


    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile= ( Map<String, Object>) account.get("profile");
      
        String name=profile.get("nickname").toString();
        String email=account.get("email").toString();
     
        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .platform(3)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        Authority authority = Authority.builder()
				.authorityName("ROLE_USER")
				.build();
        return User.builder()
                .nickname(name)
                .username(email)
                .authorities(Collections.singleton(authority))
                .build();
    }

}