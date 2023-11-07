package com.kekeke.kekekebackend.common.socialPlatform;

import com.google.gson.*;
import com.kekeke.kekekebackend.common.exception.BusinessException;
import com.kekeke.kekekebackend.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AppleClient
{
    public SocialPlatformUserInfo getUserInfo(String idToken)
    {
        StringBuffer result = new StringBuffer();
        try
        {
            URL url = new URL("https://appleid.apple.com/auth/keys");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";

            while ((line = br.readLine()) != null)
            {
                result.append(line);
            }
        }
        catch (IOException e)
        {
            throw new BusinessException(ErrorCode.APPLE_LOGIN_FAIL);
        }

        JsonParser parser = new JsonParser();
        JsonObject keys = (JsonObject) parser.parse(result.toString());
        JsonArray keyArray = (JsonArray) keys.get("keys");


        //클라이언트로부터 가져온 identity token String decode
        String[] decodeArray = idToken.split("\\.");
        String header = new String(Base64.getDecoder().decode(decodeArray[0]));

        //apple에서 제공해주는 kid값과 일치하는지 알기 위해
        JsonElement kid = ((JsonObject) parser.parse(header)).get("kid");
        JsonElement alg = ((JsonObject) parser.parse(header)).get("alg");

        //써야하는 Element (kid, alg 일치하는 element)
        JsonObject avaliableObject = null;
        for (int i = 0; i < keyArray.size(); i++)
        {
            JsonObject appleObject = (JsonObject) keyArray.get(i);
            JsonElement appleKid = appleObject.get("kid");
            JsonElement appleAlg = appleObject.get("alg");

            if (Objects.equals(appleKid, kid) && Objects.equals(appleAlg, alg))
            {
                avaliableObject = appleObject;
                break;
            }
        }

        if (ObjectUtils.isEmpty(avaliableObject))
        {
            throw new BusinessException(ErrorCode.APPLE_LOGIN_FAIL);
        }

        PublicKey publicKey = this.getPublicKey(avaliableObject);

        Claims userInfo = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(idToken).getBody();
        JsonObject userInfoObject = (JsonObject) parser.parse(new Gson().toJson(userInfo));

        JsonElement idElement = userInfoObject.get("sub");
        JsonElement emailElement = userInfoObject.get("email");

        String userId = idElement.getAsString();
        String email = emailElement.getAsString();

        return new SocialPlatformUserInfo(userId, email);
    }

    private PublicKey getPublicKey(JsonObject object)
    {
        String nStr = object.get("n").toString();
        String eStr = object.get("e").toString();

        byte[] nBytes = Base64.getUrlDecoder().decode(nStr.substring(1, nStr.length() - 1));
        byte[] eBytes = Base64.getUrlDecoder().decode(eStr.substring(1, eStr.length() - 1));

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        PublicKey publicKey = null;
        try
        {
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(publicKeySpec);

        }
        catch (Exception exception)
        {
            throw new BusinessException(ErrorCode.APPLE_LOGIN_FAIL);
        }
        return publicKey;
    }
}
