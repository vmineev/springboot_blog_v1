package me.minhee.springbootdeveloper.controller;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.minhee.springbootdeveloper.SpringbootdeveloperApplication;
import me.minhee.springbootdeveloper.config.jwt.JwtFactory;
import me.minhee.springbootdeveloper.config.jwt.JwtProperties;
import me.minhee.springbootdeveloper.domain.RefreshToken;
import me.minhee.springbootdeveloper.domain.User;
import me.minhee.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.minhee.springbootdeveloper.repository.RefreshTokenRepository;
import me.minhee.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * given: 테스트 유저 생성, 리프레시 토큰 만들어 db에 저장, 토큰 요청 api의 요청 본문에 리프레시 토큰을 포함하여 요청 객체 생성
 * when: 토큰 추가 api에 요청을 보낸다, 요청 타입은 json, given절에서 만든 객체를 요청 본문으로 함께 보낸다
 * then: 응답코드 201 Created 인지 확인하고 응답으로 온 액세스 토큰이 비어있지 않은 지 확인
 */
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = SpringbootdeveloperApplication.class )
class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("createdNewAccessToken: 새로운 액세스 토큰을 발급한다")
    @Test
    public void createdNewAccessToken() throws Exception{
        //given
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(),refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}


