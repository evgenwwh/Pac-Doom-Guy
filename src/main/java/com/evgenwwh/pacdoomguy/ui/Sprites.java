package com.evgenwwh.pacdoomguy.ui;

import com.evgenwwh.pacdoomguy.model.Direction;
import com.evgenwwh.pacdoomguy.model.Effect;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Loads sprites from {@code /sprites/*.png} once and caches them. */
public final class Sprites {
    private Sprites() {
    }

    private static final Map<String, BufferedImage> CACHE = new ConcurrentHashMap<>();

    public static BufferedImage get(String name) {
        return CACHE.computeIfAbsent(name, Sprites::load);
    }

    public static BufferedImage player(Direction facing, int frame) {
        String d = switch (facing) {
            case UP -> "w";
            case DOWN -> "s";
            case LEFT -> "a";
            case RIGHT -> "d";
        };
        return get("player_" + d + (frame + 1));
    }

    public static BufferedImage demon(int variant) {
        return get("demon" + (variant % 3 + 1));
    }

    /** Health indicator for 1..4 lives. */
    public static BufferedImage health(int lives) {
        int idx = Math.max(1, Math.min(4, 5 - lives));
        return get("hp" + idx);
    }

    public static BufferedImage item(Effect effect) {
        return get(switch (effect) {
            case SHIELD -> "coin";
            case SPEED -> "rune";
            case KILL -> "blade";
        });
    }

    public static BufferedImage hudIcon(Effect effect) {
        return get(switch (effect) {
            case SHIELD -> "hud_coin";
            case SPEED -> "hud_rune";
            case KILL -> "hud_blade";
        });
    }

    private static BufferedImage load(String name) {
        String path = "/sprites/" + name + ".png";
        try (InputStream in = Sprites.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing sprite " + path);
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read sprite " + path, e);
        }
    }
}
