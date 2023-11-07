package com.kekeke.kekekebackend.common.socialPlatform;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GoogleClient {
    private static final String USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v1/userinfo";
    private final RestTemplate restTemplate;

    public SocialPlatformUserInfo getUserInfo(String accessToken)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                USERINFO_ENDPOINT,
                HttpMethod.GET,
                request,
                String.class
        );

        JSONObject responseBody = new JSONObject(response.getBody());

        return new SocialPlatformUserInfo(responseBody.getString("id"), responseBody.getString("email"));
    }
}
