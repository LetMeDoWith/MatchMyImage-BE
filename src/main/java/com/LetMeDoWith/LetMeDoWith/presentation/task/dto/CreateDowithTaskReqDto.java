package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

public record CreateDowithTaskReqDto(
    String title,
    Long categoryId,
    LocalDateTime startDateTime,
    Boolean isRoutine,
    RoutineInfo routineInfo
) {

  public static record RoutineInfo () {
    // TODO - 정의 필요
  }
}
