package com.LetMeDoWith.LetMeDoWith.infrastructure.member.repository;

import com.LetMeDoWith.LetMeDoWith.application.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.common.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.domain.member.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.domain.member.MemberTermAgree;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberSocialAccountJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberTermAgreeJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    
    private final MemberJpaRepository memberJpaRepository;
    private final MemberTermAgreeJpaRepository termAgreeJpaRepository;
    private final MemberSocialAccountJpaRepository socialAccountJpaRepository;
    
    @Override
    public Optional<Member> getMember(Long id, MemberStatus memberStatus) {
        return memberJpaRepository.findByIdAndStatus(id, memberStatus);
    }
    
    @Override
    public Optional<Member> getMember(SocialProvider provider, String subject) {
        return memberJpaRepository.findByProviderAndSubject(provider, subject);
    }

    @Override
    public Optional<Member> getNormalStatusMember(Long id) {
        return memberJpaRepository.findByIdAndStatus(id, MemberStatus.NORMAL);
    }

    @Override
    public List<Member> getMembers(String nickname, List<MemberStatus> memberStatuses) {
        return memberJpaRepository.findAllByNicknameAndStatusIn(nickname, memberStatuses);
    }
    
    @Override
    public Member save(Member member) {
        if (member.getTermAgree() != null) {
            termAgreeJpaRepository.save(member.getTermAgree());
        }
        
        return memberJpaRepository.save(member);
    }
    
    @Override
    public MemberTermAgree save(MemberTermAgree memberTermAgree) {
        
        if (!memberTermAgree.isTermsOfAgree() || !memberTermAgree.isPrivacy()) {
            throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
        }
        
        return termAgreeJpaRepository.save(memberTermAgree);
    }
    
    @Override
    public void saveSocialAccount(MemberSocialAccount memberSocialAccount) {
        socialAccountJpaRepository.save(memberSocialAccount);
    }
}
