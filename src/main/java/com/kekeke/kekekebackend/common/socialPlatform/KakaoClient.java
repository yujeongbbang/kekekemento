package com.kekeke.kekekebackend.common.socialPlatform;

import com.kekeke.kekekebackend.common.exception.BusinessException;
import com.kekeke.kekekebackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
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
public class KakaoClient
{
    private static final String GET_ACCESS_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String GET_MEMBER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    //private final InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;
    private final RestTemplate restTemplate;

    public SocialPlatformUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GET_MEMBER_INFO_URL,
                HttpMethod.GET,
                request,
                String.class
        );

        JSONObject responseBody = new JSONObject(response.getBody());

        Long id = responseBody.getLong("id");
        String uid = id.toString();

        String email = null;

        try
        {

            JSONObject kakaoAccount = responseBody.getJSONObject("kakao_account");

            email = kakaoAccount.getString("email");
            boolean isEmailValid = kakaoAccount.getBoolean("is_email_valid");
            boolean is_email_verified = kakaoAccount.getBoolean("is_email_verified");

            if(isEmailValid == false || is_email_verified == false)
            {
                throw new BusinessException(ErrorCode.KAKAO_LOGIN_FAIL);
            }
        }
        catch(JSONException e)
        {
            throw new BusinessException(ErrorCode.KAKAO_LOGIN_FAIL);
        }

        return new SocialPlatformUserInfo(uid, email);
    }

//    private String getAccessToken(String authorizedCode) {
//        HttpHeaders headers = getAccessTokenRequestHeader();
//
//        ClientRegistration kakaoRegistration = inMemoryClientRegistrationRepository.findByRegistrationId("kakao");
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//
//        body.add("code", authorizedCode);
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", kakaoRegistration.getClientId());
//        body.add("redirect_uri", kakaoRegistration.getRedirectUri());
//        body.add("client_secret", kakaoRegistration.getClientSecret());
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                GET_ACCESS_TOKEN_URL,
//                HttpMethod.POST,
//                request,
//                String.class
//        );
//
//        JSONObject responseBody = new JSONObject(response.getBody());
//
//        return responseBody.getString("access_token");
//    }

    private HttpHeaders getAccessTokenRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        return headers;
    }

    //	private MemberInfo createMemberInfoFromKakaoResponse(ResponseEntity<String> response) {
//		JSONObject responseBody = new JSONObject(response.getBody());
//
//		String email = responseBody.getJSONObject("kakao_account")
//				.getString("email");
//
//		boolean hasBirthday = responseBody.getJSONObject("kakao_account")
//				.has("birthday");
//
//		String birthday = null;
//		if (hasBirthday) {
//			birthday = responseBody.getJSONObject("kakao_account")
//					.getString("birthday");
//		}
//
//		String profileUrl = responseBody.getJSONObject("kakao_account")
//				.getJSONObject("profile")
//				.getString("profile_image_url");
//
//		String nickname = responseBody.getJSONObject("kakao_account")
//				.getJSONObject("profile")
//				.getString("nickname");
//
//		return new MemberInfo(email, birthday, profileUrl, nickname);
//	}
}
