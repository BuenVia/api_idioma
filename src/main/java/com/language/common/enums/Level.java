package com.language.common.enums;

/**
 * CEFR proficiency levels. The enum name (e.g. "B2") is the value stored in the
 * database and sent in subject create/update requests.
 */
public enum Level {
    A1("Beginner"),
    A2("Elementary"),
    B1("Intermediate"),
    B2("Upper Intermediate"),
    C1("Advanced"),
    C2("Proficient");

    private final String displayName;

    Level(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
