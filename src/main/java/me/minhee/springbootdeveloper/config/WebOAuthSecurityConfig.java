package me.minhee.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.config.jwt.TokenProvider;
import me.minhee.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.minhee.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import me.minhee.springbootdeveloper.config.oauth.OAuth2UserCustomService;
import me.minhee.springbootdeveloper.repository.RefreshTokenRepository;
import me.minhee.springbootdeveloper.service.UserDetailService;
import me.minhee.springbootdeveloper.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

/**
 * OAuth2 와 JWT를 함게 사용하기 위한 설정파일
 */

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;


    @Bean
    public WebSecurityCustomizer configure(){//스프링 시큐리티 기능 비활성화
        return (web)-> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**","/css/**","/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //토큰 방식으로 인증하기 때문에 폼로그인, 세션 비활성화
        http.csrf(CsrfConfigurer::disable)
                .httpBasic(HttpBasicConfigurer :: disable)
                .formLogin(FormLoginConfigurer::disable)
                .logout(LogoutConfigurer::disable);

        http.sessionManagement((sessionManagement)->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //헤더를 확인할 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        //토믄 재발급 URL은 인증 없이 접근 가능하도록 설정, 나머지 API URL은 인증 필요
        http.authorizeHttpRequests((authorize)->authorize
                .requestMatchers("/api/token").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());

        http.oauth2Login((oauth2)->oauth2
                .loginPage("/login")
                .authorizationEndpoint((authorizationEndpointConfig)->authorizationEndpointConfig
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint((userInfoEndpointConfig)->userInfoEndpointConfig
                        .userService(oAuth2UserCustomService)));

        http.logout((httpSecurityLogoutConfigurer)->httpSecurityLogoutConfigurer
                .logoutSuccessUrl("/login"));

        // /api 로 시작하는 url 인 경우 401 상태 코드를 반환하도록 예외 처리
        http.exceptionHandling((httpSecurityExceptionHandlingConfigurer)->httpSecurityExceptionHandlingConfigurer
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")));
        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(){
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(){
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository(){
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
