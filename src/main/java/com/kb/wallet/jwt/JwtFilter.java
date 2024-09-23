package com.kb.wallet.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI.contains("/api/members/register") ||
                requestURI.contains("/api/members/login") ||
                requestURI.startsWith("/api/test") ||
                requestURI.startsWith("/static") ||
                requestURI.startsWith("/resources") ||
                requestURI.startsWith("/public") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.contains("/swagger-ui.html") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/swagger-config") ||
                requestURI.startsWith("/api-docs") ||
                requestURI.contains("/v3/api-docs") ||
                requestURI.startsWith("/images") ||
                requestURI.contains("/favicon.ico") ||
                requestURI.contains("/error") ||
                requestURI.contains("/api/home")) {
            filterChain.doFilter(request, response);
            return;
        }

        // /api/musicals/**로 들어오는 요청
        if (requestURI.startsWith("/api/musicals")) {
            if (requestURI.contains("/seat-availability/") ||
                    requestURI.contains("/booking/queue") ||
                    requestURI.contains("/seats/reserve") ||
                    requestURI.contains("/tickets")) {
                throw new AuthenticationException("Invalid or missing token") {
                };
            }
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "FAIL");
        try {
            String jwt = resolveToken(request);
            resultMap = tokenProvider.validateToken(jwt);
            if (jwt == null || !resultMap.get("result").equals("SUCCESS")) {

                throw new AuthenticationException("Invalid or missing token") {
                };
            }

            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("msg", authentication.getName());

            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            request.setAttribute("tokenexception", resultMap);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            responseData.put("message", "Unauthorized: " + ex.getMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(responseData));
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
