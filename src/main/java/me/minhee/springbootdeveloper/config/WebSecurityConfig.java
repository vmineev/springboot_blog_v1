package me.minhee.springbootdeveloper.config;

//import lombok.RequiredArgsConstructor;
//import me.minhee.springbootdeveloper.service.UserDetailService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
////실제 인증을 처리하는 시큐리티 설정 부분
//@RequiredArgsConstructor
//@Configuration
//public class WebSecurityConfig {
//
//    private final UserDetailService userService;
//
//    //스프링 시큐리티 기능 비활성화 : 정적 리소스(이미지, html 파일)에 설정
//    @Bean
//    public WebSecurityCustomizer configure(){
//        return (web)->web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers("/static/**");
//    }
//
//    //특정 HTTP 요청에 대한 웹 기반 보안 구성
//    //인증, 인가 및 로그인, 로그아웃 관련 설정
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        return http
//                .authorizeHttpRequests( (authorize)->authorize//특정 요청과 일치하는 url에 대한 액세스 설정
//                    .requestMatchers("/login","/signup","/user").permitAll() // 누구나 접근가능
//                    .anyRequest().authenticated()) // 위에 설정한 url 이외의 요청에대해 인가는 필요하지 않지만 인증이 접근
//                .formLogin((form)->form //폼 기반 로그인 설정
//                    .loginPage("/login") // 로그인 페이지 경로 설정
//                    .defaultSuccessUrl("/articles")) // 로그인 완료되었을 때 이동할 경로 설정
//                .logout(logout->logout //로그아웃 설정
//                    .logoutSuccessUrl("/login") //로그아웃 완료 되었을 때 이동할 경로
//                    .invalidateHttpSession(true)) //로그아웃 이후에 세션을 전제 삭제할지 여부
//                .csrf(csrf->csrf.disable())
//                .build();
//    }
//
//    //인증 관리자 관련 설정
//
//
//    //패스워드 인코더로 사용할 빈 등록
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//}
