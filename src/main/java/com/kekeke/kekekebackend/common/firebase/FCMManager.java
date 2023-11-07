package com.kekeke.kekekebackend.common.firebase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class FCMManager
{
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/{aa}/messages:send";
    private final ObjectMapper _objectMapper;

    public FCMManager(ObjectMapper objectMapper)
    {
        _objectMapper = objectMapper;
    }

    public void sendMessage(String fcmDeviceToken, String title, String context) throws Exception {
        String message = makeMessage(fcmDeviceToken, title, context);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                String.class);

        System.out.println(response.getBody());
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException
    {
        FCMMessage fcmMessage = FCMMessage.builder()
                .message(FCMMessage.Message.builder()
                        .token(targetToken)
                        .notification(FCMMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return _objectMapper.writeValueAsString(fcmMessage);
    }

    // 외부 멤버 변수로 토큰과 유효기간 저장
    private String cachedToken;
    private Instant tokenExpiryTime;

    private static final String CREDENTIALS_PATH = "aa.json";
    private static final String SCOPE = "https://www.googleapis.com/auth/cloud-platform";

    public String getAccessToken() throws IOException {
        if (isTokenValid()) {
            return cachedToken;
        }

        ClassPathResource resource = new ClassPathResource(CREDENTIALS_PATH);
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(resource.getInputStream())
                .createScoped(List.of(SCOPE));

        googleCredentials.refreshIfExpired();
        AccessToken token = googleCredentials.getAccessToken();

        // 토큰과 유효기간을 멤버 변수에 저장
        cachedToken = token.getTokenValue();
        tokenExpiryTime = token.getExpirationTime().toInstant();

        return cachedToken;
    }

    // 토큰의 유효성 확인 함수
    private boolean isTokenValid() {
        if (cachedToken == null || tokenExpiryTime == null) {
            return false;
        }
        // 현재 시간이 유효기간 이전이면 true (아직 토큰이 유효), 그렇지 않으면 false 반환
        return Instant.now().isBefore(tokenExpiryTime);
    }
}
