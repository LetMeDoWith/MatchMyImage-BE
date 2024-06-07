package com.LetMeDoWith.LetMeDoWith.enums.converter.member;

import com.LetMeDoWith.LetMeDoWith.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.enums.converter.AbstractCombinedConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class SocialProviderConverter extends AbstractCombinedConverter<SocialProvider> {
    
    public SocialProviderConverter() {
        super(SocialProvider.class);
    }
}