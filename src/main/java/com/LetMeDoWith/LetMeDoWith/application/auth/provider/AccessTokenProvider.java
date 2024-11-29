package com.LetMeDoWith.LetMeDoWith.application.auth.provider;

import com.LetMeDoWith.LetMeDoWith.application.auth.util.JwtUtil;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiAuthException;
import com.LetMeDoWith.LetMeDoWith.domain.auth.TokenType;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccessTokenProvider {
    
    private final SecretKey secretKey;

    @Value("${auth.jwt.atk-duration-min}")
    private Long atkDurationMin;

    @Value("${auth.jwt.signup-duration-min}")
    private Long signupDurationMin;

    @Value("${auth.jwt.issuer}")
    private String issuer;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // private final long refreshExpireTime = 1 * 60 * 1000L * 60 * 24 * 14; // 14일
    
    @Autowired
    public AccessTokenProvider(@Value("${auth.jwt.secret}") String secret) {
        
        // plain secret Base64로 인코딩
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secret.getBytes());
        
        // SecretKey 객체 생성
        this.secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());

    }
    
    /**
     * 서버 Access Token 생성
     *
     * @param memberId
     * @return
     */
    public AccessToken createAccessToken(Long memberId) {
        return AccessToken.of(memberId, issuer, atkDurationMin, secretKey);
    }

    /**
     * Token 검증 없이, payload의 memberId 추출
     *
     * @param token
     * @return
     */
    public Long getMemberIdWithoutVerify(final String token) {

        String[] parts = token.split("\\.");
        System.out.println(parts);
        if(parts.length!=3) throw new RestApiAuthException(FailResponseStatus.INVALID_JWT_TOKEN_FORMAT);

        byte[] decodeBytes = Base64.getUrlDecoder().decode(parts[1]);
        String payload = new String(decodeBytes, StandardCharsets.UTF_8);

        Map<String, String> map = null;
        try{
            map = objectMapper.readValue(payload, new TypeReference<>(){});
        }catch (JsonProcessingException e) {
            throw new RestApiAuthException(FailResponseStatus.INVALID_JWT_TOKEN_FORMAT);
        }

        return Long.valueOf(map.get("memberId"));
    }

    /**
     * Access Token 검증 및 memberId 추출
     *
     * @param token
     * @return
     */
    public Long validateAccessToken(final String token) {
        final Jws<Claims> claims = JwtUtil.parseTokenToJws(token, secretKey);

        if (claims.getBody().get("sub").equals(TokenType.ATK.getCode()) && claims.getBody()
            .get("iss")
            .equals(this.issuer)) {
            return Long.parseLong(claims.getBody().get("memberId").toString());
        } else {
            throw new RestApiAuthException(FailResponseStatus.INVALID_TOKEN);
        }

    }

    
}