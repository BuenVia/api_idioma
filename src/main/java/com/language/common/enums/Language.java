package com.language.common.enums;

public enum Language {
    ES("Castilian Spanish"),
    CA("Catalan");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** The value used in URLs, e.g. "es" in /es/subject/getall. */
    public String getCode() {
        return name().toLowerCase();
    }
}
