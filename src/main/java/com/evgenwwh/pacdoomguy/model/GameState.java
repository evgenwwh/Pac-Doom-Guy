package com.evgenwwh.pacdoomguy.model;

public enum GameState {
    RUNNING, PAUSED, WON, LOST;

    public boolean isOver() {
        return this == WON || this == LOST;
    }
}
