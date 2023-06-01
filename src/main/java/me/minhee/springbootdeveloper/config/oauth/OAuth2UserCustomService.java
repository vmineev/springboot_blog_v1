package me.minhee.springbootdeveloper.config.oauth;

import lombok.RequiredArgsConstructor;
import me.minhee.springbootdeveloper.domain.User;
import me.minhee.springbootdeveloper.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 리소스 서버에서 보내주는 사용자 정보를 불러오는 메서드인 loadUser()를 통해 사용자를 조회
 * 사용자가 db ( user table)에 있다면 이름 업데이트, 없다면 saveOrUpdate) 로 추가
 */
@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //OAuth 서비스에서 제공하는 정보를 기반으로 유저 객체를 만들어주는 loadUser 메서드로 사용자 객체 가져온다.
        //사용자 객체에는 식별자, 이름, 이메일, 프로필 사진 링크 등의 정보를 담고 있다.
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user;
    }

    //유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2User oAuth2User){
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email)
                .map(entity->entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build());
        return userRepository.save(user);
    }
}
