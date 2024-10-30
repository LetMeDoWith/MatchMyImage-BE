package com.LetMeDoWith.LetMeDoWith.presentation.member.controller;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateTokenResult;
import com.LetMeDoWith.LetMeDoWith.application.auth.service.AuthService;
import com.LetMeDoWith.LetMeDoWith.application.member.dto.CreateSignupCompletedMemberCommand;
import com.LetMeDoWith.LetMeDoWith.application.member.service.MemberService;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.SuccessResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.domain.member.Member;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.CreateMemberTermAgreeReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.SignupCompleteReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;
    private final AuthService authService;
    
    /**
     * 회원가입을 완료한다. 회원가입이 완료되면 로그인한다.
     *
     * @param signupCompleteReqDto 회원가입을 완료하려는 멤버의 추가 정보 및 약관 동의 정보
     * @return 로그인 결과. 액세스 트큰과, refresh 토큰
     */
    @PutMapping("")
    public ResponseEntity completeSignup(@RequestBody SignupCompleteReqDto signupCompleteReqDto) {
        CreateSignupCompletedMemberCommand command =
            CreateSignupCompletedMemberCommand.builder()
                                              .nickname(signupCompleteReqDto.nickname())
                                              .dateOfBirth(signupCompleteReqDto.dateOfBirth())
                                              .gender(signupCompleteReqDto.gender())
                                              .isTerms(signupCompleteReqDto.agreements()
                                                                           .termsOfAgree())
                                              .isPrivacy(signupCompleteReqDto.agreements()
                                                                             .privacy())
                                              .isAdvertisement(signupCompleteReqDto.agreements()
                                                                                   .advertisement())
                                              .build();
        
        Member signupCompletedMember = memberService.createSignupCompletedMember(command);
        CreateTokenResult token = authService.getToken(signupCompletedMember);
        
        return ResponseUtil.createSuccessResponse(SuccessResponseStatus.OK, CreateTokenResDto.fromCreateTokenResult(token));
    }
    
    /**
     * 멤버의 약관 동의 사항을 생성한다.
     *
     * @param memberId                    약관동의를 생성할 멤버
     * @param createMemberTermAgreeReqDto 멤버의 약관 동의 사항. 필수 동의사항은 false일 수 없다.
     * @return 성공 메세지
     */
    @Deprecated
    @PostMapping("/{memberId}/agreement")
    public ResponseEntity createMemberTermAgree(
        @PathVariable Long memberId,
        @RequestBody CreateMemberTermAgreeReqDto createMemberTermAgreeReqDto) {
        
        memberService.createMemberTermAgree(
            createMemberTermAgreeReqDto.termsOfAgree(),
            createMemberTermAgreeReqDto.privacy(),
            createMemberTermAgreeReqDto.advertisement()
        );
        
        return ResponseUtil.createSuccessResponse();
    }
    
    /**
     * 닉네임의 중복 여부를 검증한다. 닉네임은 공백일 수 없다.
     *
     * @param nickname 중복 여부를 검증하려는 닉네임
     * @return 닉네임의 검증 결과
     */
    @PostMapping("/nickname")
    public ResponseEntity checkNickname(@RequestBody String nickname) {
        if (memberService.isExistingNickname(nickname)) {
            throw new RestApiException(FailResponseStatus.DUPLICATE_NICKNAME);
        } else {
            return ResponseUtil.createSuccessResponse("사용 가능한 닉네임입니다.");
        }
    }
    
    /**
     * 멤버를 탈퇴처리한다.
     *
     * @param memberId 탈퇴하려는 멤버의 id
     * @return 탈퇴 성공 여부
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity withdrawMember(@PathVariable Long memberId) {
        memberService.withdrawMember(memberId);
        
        return ResponseUtil.createSuccessResponse(SuccessResponseStatus.OK);
    }
    
}