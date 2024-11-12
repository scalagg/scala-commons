package me.lucko.helper.scoreboard;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public enum TeamNameGenerator {

    INSTANCE;

    private static final String UNIQUE_ID = "CNT";
    private static final AtomicInteger TEAM_ID = new AtomicInteger();

    @NotNull
    public static String generate(int sortPriority, boolean playerTag) {
        return UNIQUE_ID + "_" + getNameFromInput(sortPriority) + TEAM_ID.incrementAndGet() + (playerTag ? "+P" : "");
    }

    @NotNull
    private static String getNameFromInput(int input) {
        if (input < 0) return "Z";
        char letter = (char) ((input / 5) + 65);
        int repeat = input % 5 + 1;

        return String.valueOf(letter).repeat(repeat);
    }
}
