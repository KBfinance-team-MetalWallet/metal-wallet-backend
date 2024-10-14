package com.kb.wallet.jwt;

import com.kb.wallet.member.constant.RoleType;
import com.kb.wallet.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {

  private static final String AUTHORITIES_KEY = "auth";

  private String secret;
  private long tokenValidityInMilliseconds;
  private Key key;

  @Value("${jwt.secret}")
  public void setSecret(String secret) {
    this.secret = secret;
  }

  @Value("${jwt.token.validity}")
  public void setTokenValidityInMilliseconds(String tokenValidityInMilliseconds) {
    this.tokenValidityInMilliseconds = Long.parseLong(tokenValidityInMilliseconds);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  private String generateToken(Object subject, Map<String, Object> claims) {
    long now = (new Date()).getTime();
    Date validity = new Date(now + tokenValidityInMilliseconds);

    String subjectStr = subject.toString();

    JwtBuilder builder = Jwts.builder()
        .setSubject(subjectStr)
        .setExpiration(validity)
        .signWith(key, SignatureAlgorithm.HS512);

    if (claims != null) {
      builder.addClaims(claims);
    }

    return builder.compact();
  }

  public String createToken(Long ticketId) {
    return generateToken(ticketId, null);  // 추가 클레임 없이 subject만 전달
  }

  // 이메일과 역할을 포함하는 토큰 생성
  public String createToken(String email, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    return generateToken(email, claims);  // 클레임을 전달
  }


  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

    log.debug("JWT Claims: " + claims);

    // 이메일과 역할 정보를 추출
    String email = claims.getSubject();
    String roleString = claims.get("role", String.class);
    RoleType role = RoleType.valueOf(roleString);

    // 권한 목록 생성
    Collection<? extends GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority(roleString));

    // Member 객체 생성 (예: 생성자를 통해 모든 멤버 정보 설정, 필요에 따라 수정)
    Member member = new Member();
    member.setEmail(email);
    member.setRole(role);

    // Authentication 객체 생성 및 반환
    return new UsernamePasswordAuthenticationToken(member, token, authorities);
  }

  public HashMap<String, String> validateToken(String token) {
    HashMap<String, String> map = new HashMap<>();

    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      map.put("result", "SUCCESS");
      map.put("msg", "인증성공");
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      map.put("result", "FAIL");
      map.put("errorCode", "900");
      map.put("msg", "잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      map.put("result", "FAIL");
      map.put("msg", "만료된 JWT 토큰입니다");
    } catch (UnsupportedJwtException e) {
      map.put("result", "FAIL");
      map.put("msg", "지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      map.put("result", "FAIL");
      map.put("msg", "JWT 토큰이 잘못되었습니다.");
    }
    return map;
  }

}
