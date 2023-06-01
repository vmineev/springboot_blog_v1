package me.minhee.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//인증 객체 만들기
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails { // UserDetail : 사용자의 인증 정보를 담아두는 인터페이스

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true) //사용자 이름
    private String nickname;

    @Builder
    public User(String email,String password, String nickname){
        this.email = email;
        this.password =password;
        this.nickname = nickname;

    }

    //사용자 정보를 조회해 있다면 리소스 서버에서 제공해주는 이름으로 업데이트 없다면 생성
    public User update(String nickname){
        this.nickname = nickname;

        return this;
    }

    //권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    //사용자의 id를 반환(고유한 값) | 사용자를 식별할 수 있는 사용자 이름을 반환, 사용자 이름은 반드시 고유!
    @Override
    public String getUsername() {
        return email;
    }

   //사용자의 패스워드 반환 | 패스워드는 암호화해서 저장하기!
    @Override
    public String getPassword(){
        return password;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        //만료되었는지 확인하는 로직
        return true; //true면 만료되지 않았음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        //계정 잠금되었는지 확인하는 로직
        return true; //true면 잠금되지 않았음
    }

    //패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        //패스워드가 만료되었는지 확인하는 로직
        return true; // 만료되지 않았음
    }

    //계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        //계정이 사용 가능한지 확인하는 로직
        return true; //사용 가능
    }
}
