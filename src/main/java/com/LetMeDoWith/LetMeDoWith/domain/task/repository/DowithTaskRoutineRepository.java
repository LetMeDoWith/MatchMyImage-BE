package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import java.util.List;

public interface DowithTaskRoutineRepository {

  void delete(DowithTaskRoutine dowithTaskRoutine);

  void delete(List<DowithTaskRoutine> dowithTaskRoutines);
}