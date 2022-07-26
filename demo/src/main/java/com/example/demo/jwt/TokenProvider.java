package com.example.demo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.example.demo.dto.OAuthAttributes;
import com.example.demo.dto.TokenDto;
import com.example.demo.entity.Authority;
import com.example.demo.entity.Token;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.RedisUtil;
import com.nimbusds.oauth2.sdk.token.RefreshToken;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

   private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

   private static final String AUTHORITIES_KEY = "auth";

   private final RefreshTokenRepository refreshTokenRepository;
   private final String secret;
   private final long tokenValidityInMilliseconds;
   private final RedisUtil redisUtil;
   private Key key;

   // access token 유효시간 = 1분해.. // 1시간
   private final long accessTokenValidTime = 30* 60 * 1000L; // 60 * 60 * 1000L;
   // refresh token 유효시간 = 2주
   private final long refreshTokenValidTime = 2 * 7 * 24 * 60 * 60 * 1000L;

   public TokenProvider(RedisUtil redisUtil
      ,RefreshTokenRepository refreshTokenRepository,
         @Value("${jwt.secret}") String secret,
         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
      this.secret = secret;
      this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
      this.refreshTokenRepository = refreshTokenRepository; // 이게 되나
      this.redisUtil = redisUtil;
   }

   // --------------------------------------------------------------------------------------
   // 빈이 생성되고 주입을 받은 후에 시크릿값을 디코드에서 key값에 할당
   @Override
   public void afterPropertiesSet() {
      byte[] keyBytes = Decoders.BASE64.decode(secret);
      this.key = Keys.hmacShaKeyFor(keyBytes);
   }

   // --------------------------------------------------------------------------------------
   // authentication 객체의 권한정보를 이용해서 토큰을 생성하는 createToken 메소드 추가
   // 여기를 두개로 만들어야돼요.
   public TokenDto createToken(Authentication authentication) {
      // 권한들
      String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

      // 만료시간 부여해서
      long now = (new Date()).getTime();
      // Date validity = new Date(now + this.tokenValidityInMilliseconds);

      Date accessValidity = new Date(now + accessTokenValidTime);
      Date refreshValidity = new Date(now + refreshTokenValidTime);

      // 토큰 생성
      String accessToken = Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(accessValidity)
            .compact();

      String refreshToken = Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(refreshValidity)
            .compact();

      Optional<Token> token = refreshTokenRepository.findByUsername(authentication.getName());
      // 없으면 삽입
      if (!token.isPresent()) {
         Token newRefreshtoken = Token.builder()
               .username(authentication.getName())
               .refreshToken(refreshToken)
               .token_type("Bearer")
               .build();
         refreshTokenRepository.save(newRefreshtoken);
      }
      
      // refreshToken만 DB에 저장.있으면 업데이트
      token.ifPresent(selectToken -> {
         selectToken.setRefreshToken(refreshToken);
         Token newRefreshtoken = refreshTokenRepository.save(selectToken);
      });

      return TokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
   }

   // --------------------------------------------------------------------------------------
  
   //OAuthToken 어차피 관리자 계정이 소셜로그인 할리는 없고
   public TokenDto createOAuthToken(OAuthAttributes attributes,Authority authority) {
      // 권한들
      String authorities =authority.getAuthorityName();

      // 만료시간 부여해서
      long now = (new Date()).getTime();
      // Date validity = new Date(now + this.tokenValidityInMilliseconds);

      Date accessValidity = new Date(now + accessTokenValidTime);
      Date refreshValidity = new Date(now + refreshTokenValidTime);

      // 토큰 생성
      String accessToken = Jwts.builder()
            .setSubject(attributes.getEmail())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(accessValidity)
            .compact();

      String refreshToken = Jwts.builder()
            .setSubject(attributes.getEmail())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(refreshValidity)
            .compact();

      Optional<Token> token = refreshTokenRepository.findByUsername(attributes.getEmail());
      // 없으면 삽입
      if (!token.isPresent()) {
         Token newRefreshtoken = Token.builder()
               .username(attributes.getEmail())
               .refreshToken(refreshToken)
               .token_type("Bearer")
               .build();
         refreshTokenRepository.save(newRefreshtoken);
      }
      
      // refreshToken만 DB에 저장.있으면 업데이트
      token.ifPresent(selectToken -> {
         selectToken.setRefreshToken(refreshToken);
         Token newRefreshtoken = refreshTokenRepository.save(selectToken);
      });

      return TokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
   }



   // --------------------------------------------------------------------------------------
  
  
  
   // 토큰을 받아서 토큰의 정보를 꺼내는
   // 토큰으로 클레임 만들고 이를 이용해서 유저객체를 만들ㅇ어서 최종적으로 authentication 객체를 리턴
   public Authentication getAuthentication(String token) {
      Claims claims = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

      Collection<? extends GrantedAuthority> authorities = Arrays
            .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

      User principal = new User(claims.getSubject(), "", authorities);

      return new UsernamePasswordAuthenticationToken(principal, token, authorities);
   }

   // --------------------------------------------------------------------------------------
   // 토큰의 유효성검사 
   public boolean validateToken(String token) {
      try {
         Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
         //블랙리스트 조회
         if(redisUtil.hasKeyBlackList(token)){
            return false;
         }
         return true;
      } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
         logger.info("잘못된 ACCESS_TOKEN 서명입니다.");
      } catch (ExpiredJwtException e) {
         logger.info("만료된 ACCESS_TOKEN 토큰입니다.");
         
      } catch (UnsupportedJwtException e) {
         logger.info("지원되지 않는 ACCESS_TOKEN 토큰입니다.");
      } catch (IllegalArgumentException e) {
         logger.info("ACCESS_TOKEN 토큰이 잘못되었습니다.");
      }
      return false;
   }

   // 수정>>>>>>>>>>>>>>>
   public String validateRefreshToken(String refreshToken) {
      try {
         // 검증 //여기가 문제같은데
         Claims claims = Jwts
               .parserBuilder()
               .setSigningKey(key)
               .build()
               .parseClaimsJws(refreshToken)
               .getBody();
      
         // refresh 토큰의 만료시간이 지나지 않았을 경우 새로운 access 토큰을 생성.
         if (!claims.getExpiration().before(new Date())) {
            Collection<? extends GrantedAuthority> authorities = Arrays
                  .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                  .map(SimpleGrantedAuthority::new)
                  .collect(Collectors.toList());

            // 테스트
            System.out.println(authorities.toString());

            return recreationAccessToken(claims.getSubject(), authorities.toString());
         }
      } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
         logger.info("잘못된 REFRESH_TOKEN 서명입니다.");
      } catch (ExpiredJwtException e) {
         logger.info("만료된 REFRESH_TOKEN 토큰입니다.");
      } catch (UnsupportedJwtException e) {
         logger.info("지원되지 않는 REFRESH_TOKEN 토큰입니다.");
      } catch (IllegalArgumentException e) {
         logger.info("REFRESH_TOKEN 토큰이 잘못되었습니다.");
      }
      return null;
   }

   public String recreationAccessToken(String username, String authorities) {
      long now = (new Date()).getTime();
      Date accessValidity = new Date(now + accessTokenValidTime);

      String accessToken = Jwts.builder()
            .setSubject(username)
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(accessValidity)
            .compact();

      return accessToken;
   }

   //기한 계산
   public Long getExpiration(String accessToken) {
      Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
      Long now = new Date().getTime();
      return (expiration.getTime() - now);
   }

}