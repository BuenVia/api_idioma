package com.language.common.controller;

import com.language.common.dto.LevelResponse;
import com.language.common.enums.Level;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Lists the CEFR levels, built from the {@link Level} enum so the response always
 * matches what subject create/update accept in the "level" field.
 */
@RestController
public class LevelController {

    @GetMapping("/levels")
    public List<LevelResponse> getLevels() {
        return Arrays.stream(Level.values())
                .map(LevelResponse::from)
                .toList();
    }
}
