package com.LetMeDoWith.LetMeDoWith.application.member.service;

import com.LetMeDoWith.LetMeDoWith.application.member.repository.FollowRepository;
import com.LetMeDoWith.LetMeDoWith.application.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.RetrieveFollowsResDto;
import com.LetMeDoWith.LetMeDoWith.domain.member.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.MemberFollow;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.FollowType;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    
    public RetrieveFollowsResDto retrieveFollows(Long memberId,
        FollowType followType,
        Pageable pageable) {

        Member member = memberRepository.getMember(memberId, MemberStatus.NORMAL)
            .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_FOLLOWER_MEMBER));

        RetrieveFollowsResDto result = null;
        switch (followType) {
            case FOLLOWER -> {
                List<MemberFollow> memberFollows = followRepository.getFollowers(member, pageable);
                result = RetrieveFollowsResDto.builder().follows(memberFollows.stream()
                                                                              .map(e -> new RetrieveFollowsResDto.Follow(
                                                                                  e.getFollowerMember()
                                                                                   .getId(),
                                                                                  e.getFollowerMember()
                                                                                   .getNickname(),
                                                                                  e.getFollowerMember()
                                                                                   .getSelfDescription(),
                                                                                  e.getFollowerMember()
                                                                                   .getProfileImageUrl()
                                                                              ))
                                                                              .toList())
                                              .build();
            }
            case FOLLOWING -> {
                List<MemberFollow> memberFollows = followRepository.getFollowings(member, pageable);
                result = RetrieveFollowsResDto.builder().follows(memberFollows.stream()
                                                                              .map(e -> new RetrieveFollowsResDto.Follow(
                                                                                  e.getFollowingMember()
                                                                                   .getId(),
                                                                                  e.getFollowingMember()
                                                                                   .getNickname(),
                                                                                  e.getFollowingMember()
                                                                                   .getSelfDescription(),
                                                                                  e.getFollowingMember()
                                                                                   .getProfileImageUrl()
                                                                              )).toList()).build();
            }
        }
        
        return result;
    }
    
    @Transactional
    public void createFollow(Long memberId, Long followingMemberId) {

        Member followerMember = memberRepository.getMember(memberId, MemberStatus.NORMAL).orElseThrow(() -> new RestApiException(
            FailResponseStatus.INVALID_FOLLOWER_MEMBER));

        Member followingMember = memberRepository.getMember(followingMemberId, MemberStatus.NORMAL).orElseThrow(() -> new RestApiException(
            FailResponseStatus.INVALID_FOLLOWING_MEMBER));
        
        followRepository.save(followerMember, followingMember);
        
    }
    
    @Transactional
    public void deleteFollow(Long memberId, Long followingMemberId) {
        
        MemberFollow memberFollow = followRepository.getFollowing(
                                                              memberId,
                                                              followingMemberId)
                                                          .orElseThrow(() -> new RestApiException(
                                                              FailResponseStatus.MEMBER_FOLLOW_NOT_EXIST));
        
        followRepository.delete(memberFollow);
        
    }
    
    
}