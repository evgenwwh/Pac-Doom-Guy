package com.evgenwwh.pacdoomguy.model.item;

import com.evgenwwh.pacdoomguy.model.Effect;
import com.evgenwwh.pacdoomguy.model.Entity;

/** A pick-up lying on the floor. Disappears if not collected in time. */
public final class Item extends Entity {
    public static final int SIZE = 34;
    public static final int LIFETIME_TICKS = 15 * 60;

    private final Effect effect;
    private int ticksLeft = LIFETIME_TICKS;

    public Item(Effect effect, double centerX, double centerY) {
        super(centerX - SIZE / 2.0, centerY - SIZE / 2.0, SIZE, SIZE);
        this.effect = effect;
    }

    public Effect effect() {
        return effect;
    }

    /** @return false once the item has expired */
    public boolean tick() {
        ticksLeft--;
        return ticksLeft > 0;
    }

    public int ticksLeft() {
        return ticksLeft;
    }
}
