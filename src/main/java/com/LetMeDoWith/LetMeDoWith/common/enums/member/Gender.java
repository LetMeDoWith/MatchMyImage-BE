package com.LetMeDoWith.LetMeDoWith.common.enums.member;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.common.converter.member.GenderConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Member에서만 사용되기 때문에 내부에 열거형 및 converter 정의함.
@AllArgsConstructor
@Getter
@JsonDeserialize(using = GenderConverter.class)
public enum Gender implements BaseEnum {
    MALE("M", "남성"),
    FEMALE("F", "여성");
    
    public final String code;
    public final String description;
}