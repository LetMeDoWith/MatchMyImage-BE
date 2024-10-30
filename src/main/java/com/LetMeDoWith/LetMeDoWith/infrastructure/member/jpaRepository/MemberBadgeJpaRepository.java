package com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.member.Badge;
import com.LetMeDoWith.LetMeDoWith.domain.member.MemberBadge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberBadgeJpaRepository extends JpaRepository<MemberBadge,Long> {
  Optional<MemberBadge> findByMemberIdAndIsMain(Long memberId, Yn isMain);
  Optional<MemberBadge> findByMemberIdAndBadge(Long memberId, Badge badge);
}
