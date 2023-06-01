package me.minhee.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreatedAccessTokenResponse {
    private String accessToken;
}
