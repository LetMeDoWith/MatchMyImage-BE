package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskJpaRepository extends JpaRepository<DowithTask, Long>, QDowithTaskRepository {

}
