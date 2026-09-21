package com.evgenwwh.pacdoomguy.stats;

import java.time.LocalDate;

/** One finished game. */
public record Record(String playerName, String levelName, int score, int timeSeconds, boolean won, LocalDate date) {

    public String formattedTime() {
        return String.format("%02d:%02d", timeSeconds / 60, timeSeconds % 60);
    }
}
