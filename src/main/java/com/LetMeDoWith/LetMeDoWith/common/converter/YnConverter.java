package com.LetMeDoWith.LetMeDoWith.common.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YnConverter extends AbstractCombinedConverter<Yn> {

  public YnConverter() { super(Yn.class); }

}
