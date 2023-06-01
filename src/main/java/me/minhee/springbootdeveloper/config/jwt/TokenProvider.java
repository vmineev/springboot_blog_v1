package me.minhee.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

// 토큰 생성하고 올바른 토큰인지 유효성 검사를 하고, 트큰에 필요한 정보를 가져오는 클래스
@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt){
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    //JWT 토큰 생성 메서드
    private String makeToken(Date expiry, User user){
        Date now = new Date();

        //JWT 구조 헤더.내용.서명
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ: jwt
                .setIssuer(jwtProperties.getIssuer()) // 내용 iss : yml 파일에서 설정한 값
                .setIssuedAt(now) //내용 iat : 현재 시간
                .setExpiration(expiry) // 내용 exp : expiry 멤버 변수값
                .setSubject(user.getEmail()) //내용 sub: 유저의 이메일
                .claim("id", user.getId()) //클레임 id : 유저 Id
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 : 비밀값과 함께 암호화
                .compact();
        }
    //JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token){
        try{
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) //비밀값으로 복호화
                    .parseClaimsJws(token);
            return true;
        }catch (Exception e){ //복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    //토큰 기반으로 인증 정보를 가져오는 메서드: 클레임 정보를 반환 받아 토큰 제목(email)과 토큰 기반으로 인증 정보 생성
    //return 값의 User 는 스프링 시큐리티가 제공하는 User 클래스를 임포트
    public Authentication getAuthentication(String token){
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(
                claims.getSubject(), "",authorities), token, authorities);
    }

    //토큰 기반으로 유저 ID 가져오는 메서드
    public Long getUserId(String token){
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token){ //클레임 가져오기
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey()) //비밀키 복호화
                .parseClaimsJws(token)
                .getBody();}
}
