package com.LetMeDoWith.LetMeDoWith.application.auth.service;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.Token;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.OidcIdTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.RefreshTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.SignupTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.repository.RefreshTokenRepository;
import com.LetMeDoWith.LetMeDoWith.application.member.service.MemberService;
import com.LetMeDoWith.LetMeDoWith.common.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.HeaderUtil;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.domain.member.Member;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenRefreshResDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AccessTokenProvider accessTokenProvider;
    private final SignupTokenProvider signupTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;
    
    private final MemberService memberService;
    
    private final OidcIdTokenProvider oidcIdTokenProvider;

    @Value("${auth.jwt.rtk-duration-day}")
    private Long rtkDurationDay;
    
    public CreateTokenRefreshResDto createTokenRefresh(String accessToken,
                                                       String refreshToken,
                                                       String userAgent) {
        
        Long memberId = accessTokenProvider.getMemberIdWithoutVerify(accessToken);

        RefreshToken savedRefreshToken = null;
        savedRefreshToken = refreshTokenRepository.getRefreshToken(refreshToken).orElseThrow(
                () -> new RestApiException(
                    FailResponseStatus.TOKEN_EXPIRED_BY_ADMIN));
        savedRefreshToken.checkTokenOwnership(memberId, accessToken, userAgent);

        AccessToken newAccessToken = accessTokenProvider.createAccessToken(memberId);
        RefreshToken newRefreshToken = refreshTokenProvider.createRefreshToken(memberId, accessToken, userAgent);

        refreshTokenRepository.deleteRefreshToken(savedRefreshToken);

        return CreateTokenRefreshResDto.of(newAccessToken, newRefreshToken);
        
    }
    
    /**
     * 회원가입 여부를 판단하여 ATK를 발급한다.
     * <p>
     * 입력으로 받은 DTO의 정보를 통해 기 가입 여부를 판단하여, 이미 가입된 유저가 발급을 요청하는 경우 ATK를 발급하고, 아닌 경우 회원가입 프로세스를 진행한다.
     *
     * @param socialProvider
     * @param idToken
     * @return 기 가입되어 있는 경우 ATK, 아닌 경우 회원가입 프로세스로 fallback.
     */
    public Token createToken(SocialProvider socialProvider, String idToken) {
        Jws<Claims> verifiedIdToken = oidcIdTokenProvider.getVerifiedOidcIdToken(socialProvider,
                                                                                 idToken);
        
        Claims body = verifiedIdToken.getBody();
        String sub = body.get("sub", String.class);
        
        Optional<Member> optionalMember = memberService.getRegisteredMember(socialProvider, sub);
        
        // 기 가입된 유저가 있으면, 로그인(액세스 토큰을 발급)한다.
        // 가입된 유저가 없으면, 회원가입 프로세스를 진행한다.
        return optionalMember.map(this::getToken)
                             .orElseGet(() -> createTemporalMember(socialProvider, sub));
    }
    
    
    /**
     * access token과 refresh token을 발급, 즉 로그인한다.
     *
     * @param member 토큰을 발급하려는 (로그인하려는) 멤버
     * @return access token 및 refresh token.
     */
    public Token getToken(Member member) {
        
        if (member.getStatus().equals(MemberStatus.NORMAL)) {

            AccessToken accessToken = accessTokenProvider.createAccessToken(member.getId());
            RefreshToken refreshToken = refreshTokenRepository.save(RefreshToken.of(member.getId(), accessToken.getToken(), HeaderUtil.getUserAgent(), rtkDurationDay * 24 * 60 * 60));

            return Token.of(accessToken, refreshToken);

        } else {
            throw new RestApiException(member.getStatus().getApiResponseStatus());
        }
        
    }
    
    /**
     * 최초 소셜 로그인 시도시 회원가입 단계를 진행하기 위해 임시 Member를 생성한다. 생성된 임시 Member는 필요한 정보가 들어있지 않은 상태이다.
     * <p>
     * 이후 회원가입이 완료될 때 Client에서 넘어오는 정보를 가지고 임시 Member를 업데이트한다.
     */
    private Token createTemporalMember(SocialProvider provider, String subject) {
        log.info("Not registered user!");
        Member member = memberService.createSocialAuthenticatedMember(provider,
                                                                      subject);
        
        return Token.of(accessTokenProvider.createSignupToken(member.getId()));
    }
    
    
}