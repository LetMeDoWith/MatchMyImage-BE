package com.LetMeDoWith.LetMeDoWith.infrastructure.member.repository;


import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberBadgeVO;
import com.LetMeDoWith.LetMeDoWith.application.member.repository.BadgeRepository;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.Badge;
import com.LetMeDoWith.LetMeDoWith.domain.member.MemberBadge;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.BadgeJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberBadgeJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BadgeRepositoryImpl implements BadgeRepository {

  private final BadgeJpaRepository badgeJpaRepository;
  private final MemberBadgeJpaRepository memberBadgeJpaRepository;

  @Override
  public void save(List<MemberBadge> memberBadges) {
    memberBadgeJpaRepository.saveAll(memberBadges);
  }

  @Override
  public void save(MemberBadge memberBadge) {
    memberBadgeJpaRepository.save(memberBadge);
  }

  @Override
  public List<MemberBadgeVO> getBadges(Long memberId) {
    return badgeJpaRepository.findAllJoinMemberBadge(memberId, BadgeStatus.ACTIVE);
  }

  @Override
  public Optional<MemberBadge> getMemberBadge(Long memberId, Badge badge) {
    return memberBadgeJpaRepository.findByMemberIdAndBadge(memberId, badge);
  }

  @Override
  public Optional<MemberBadge> getMainMemberBadge(Long memberId) {
    return memberBadgeJpaRepository.findByMemberIdAndIsMain(memberId, Yn.TRUE);
  }

  @Override
  public Optional<Badge> getBadge(Long badgeId, BadgeStatus badgeStatus) {
    return badgeJpaRepository.findByIdAndBadgeStatus(badgeId, badgeStatus);
  }

}
