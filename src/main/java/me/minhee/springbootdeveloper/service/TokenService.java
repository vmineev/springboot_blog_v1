package me.minhee.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.config.jwt.TokenProvider;
import me.minhee.springbootdeveloper.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 1. 전달받은 리프레시 토큰으로 토큰 유효성 검사를 하고,유효 토큰일시 리프레시 토큰으로 사용자 ID 찾는다.
 * 2. 사용자를 찾은 후에 토큰 제공자의 generateToken() 메서드로 새로운 액세스 토믄 생성한다.
 */
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken){
        //토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)){
            throw new IllegalArgumentException("Unexpected Token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }


}
