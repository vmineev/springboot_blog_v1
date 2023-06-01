package me.minhee.springbootdeveloper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.config.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 토큰필터 만들기, 필터는 실제로 각종 요청이 요청을 처리하기 위한 로직으로 전달되기 전후에 URL 패턴에 맞는 모든 요청을 처리하는 기능 제공
 * 요청이 오면 헤더값을 비교해서 토큰이 있는지 확인하고 유효 토큰이라면 security context holder 에 인증정보 저장
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter { //모든 요청은 OncePerRequestFilter 로 모인다.
    //OncePerRequestFilter 는 로그인이 되어있는지 확인하는 필터

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        //가져온 값에서 접두사 Bearer 제거
        String token = getAccessToken(authorizationHeader);
        //가져온 토큰이 유효한지 확인하고, 유효한 때는 인증 정보를 설정
        if (tokenProvider.validToken(token)){
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);

    }

    private String getAccessToken(String authorizationHeader){
        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
