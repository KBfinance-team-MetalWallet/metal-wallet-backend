package com.kb.wallet.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";

    private String secret;
    private long tokenValidityInMilliseconds;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${jwt.token.validity}")
    public void setTokenValidityInMilliseconds(String tokenValidityInMilliseconds) {
        this.tokenValidityInMilliseconds = Long.parseLong(tokenValidityInMilliseconds);
    }

    private Key key;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String email, String role) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliseconds);

        String accessToken = Jwts.builder().setSubject(email).claim("role", role)
                .signWith(key, SignatureAlgorithm.HS512).setExpiration(validity).compact();

        return accessToken;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"));

        // TODO ADMIN 기능 추가될 때 수정 필요
//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get("role").toString().split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .toList();

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
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