package com.language.common.dto;

import com.language.common.enums.Level;

public record LevelResponse(String code, String name) {

    public static LevelResponse from(Level level) {
        return new LevelResponse(level.name(), level.getDisplayName());
    }
}
