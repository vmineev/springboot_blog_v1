package me.minhee.springbootdeveloper.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

//해당 값들을 변수로 접근하는 데 사용
@Setter
@Getter
@Component
@ConfigurationProperties("jwt") // 자바 클래스에 properties or yml 의 property 을 가져와서 사용
public class JwtProperties {

    private String issuer;
    private String secretKey;
}
