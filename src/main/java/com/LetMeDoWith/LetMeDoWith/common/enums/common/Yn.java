package com.LetMeDoWith.LetMeDoWith.common.enums.common;

import com.LetMeDoWith.LetMeDoWith.common.converter.YnConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Yn implements BaseEnum {

  TRUE("Y", "긍정"),
  FALSE("N", "부정");

  public final String code;
  public final String description;

}
