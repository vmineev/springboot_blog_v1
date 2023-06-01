package me.minhee.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Jwts;
import me.minhee.springbootdeveloper.domain.User;
import me.minhee.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken():유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken(){
        //given :토큰에 유저 정보를 추가하기 위한 테스트 유저 만들기
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test").build());

        //when: 토큰제공자의 메서드 호출해 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        //then: 토큰 복호화, 토큰 만들 때 클레임으로 넣어둔 id 값이 given 절에서 만든 유저 id 와 동일한지 확인
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validToken(): 만료된 토큰일 때에 유효성 검증에 실패한다.") //검증 실패 확인하기
    @Test
    void validToken_invalidToken(){
        //given : 라이브러리로 토큰 생성, 이미 만료된 토큰으로 생성
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        //when : 유효한 토큰인지 검증한 뒤 결과값 가져오기
        boolean result = tokenProvider.validToken(token);

        //then: 결과값이 유효한 토큰이 아닌 지 확인
        assertThat(result).isFalse();
    }

    @DisplayName("validToken(): 유표한 토큰일 때에 유효성 검증에 성공한다")
    @Test
    void validToken_validToken(){
        //given: 현재 시간으로 14일 뒤로 만료되지 않은 토큰 생성
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        //when : 유효한 토큰인지 검증한 뒤 결과값 반환
        boolean result = tokenProvider.validToken(token);

        //then : 결과값이 유효한 토큰인지 확인
        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져 올 수 있다.")
    @Test
    void getAuthentication(){ // 토큰을 전달 받아 인증 정보를 담은 객체 Authentication 를 반환하는 메서드
        //given : 토큰 생성
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        //when: 인증 객체 반환
        Authentication authentication = tokenProvider.getAuthentication(token);
        //then: 인증 객체의 유저 이름을 가져와 given절에서 설정한 subject값과 같은지 확인
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId(){// 토큰 기반으로 유저 ID를 가져오는 메서드
        //given : 토큰 생성, 클레임 추가
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id",userId))
                .build()
                .createToken(jwtProperties);

        //when : 유저 ID를 반환
        Long userIdByToken = tokenProvider.getUserId(token);

        //then : 반환받은 유저 ID가 given절의 유저 ID 값인 1과 같은ㅅ
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
