package com.evgenwwh.pacdoomguy.model;

/** Temporary power-ups. Durations are in game ticks (60 per second). */
public enum Effect {
    /** Demons cannot hurt the player. */
    SHIELD(5 * 60),
    /** Player moves faster. */
    SPEED(10 * 60),
    /** Touching a demon kills it. */
    KILL(10 * 60);

    public final int durationTicks;

    Effect(int durationTicks) {
        this.durationTicks = durationTicks;
    }
}
