package com.language.common.dto;

import com.language.common.enums.Language;

public record LanguageResponse(String code, String name) {

    public static LanguageResponse from(Language language) {
        return new LanguageResponse(language.getCode(), language.getDisplayName());
    }
}
