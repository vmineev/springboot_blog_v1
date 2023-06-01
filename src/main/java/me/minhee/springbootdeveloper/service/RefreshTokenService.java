package me.minhee.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.domain.RefreshToken;
import me.minhee.springbootdeveloper.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

/**
 * 리프레시 토큰을 전달받아 토큰 제공자를 사용해 새로운 액세스 토큰을 만드는 서비스 클래스
 */
@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalArgumentException("Unexpected Token"));
    }

}
