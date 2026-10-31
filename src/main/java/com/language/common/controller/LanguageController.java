package com.language.common.controller;

import com.language.common.dto.LanguageResponse;
import com.language.common.enums.Language;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Lists the supported languages, built from the {@link Language} enum so the
 * response always matches what the /{language} URL segment accepts.
 */
@RestController
public class LanguageController {

    @GetMapping("/languages")
    public List<LanguageResponse> getLanguages() {
        return Arrays.stream(Language.values())
                .map(LanguageResponse::from)
                .toList();
    }
}
