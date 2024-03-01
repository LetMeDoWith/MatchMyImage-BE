package com.LetMeDoWith.LetMeDoWith.enums.user;

import com.LetMeDoWith.LetMeDoWith.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType implements BaseEnum {
    USER("USER", "일반"),
    ADMIN("ADMIN", "관리자");

    private final String code;
    private final String description;
}
