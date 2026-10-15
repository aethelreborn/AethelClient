package aethel.client.screens.modernui;

import java.awt.Color;

/**
 * Central glassmorphism theme for the AethelClient ClickGui.
 * Single source of truth for colors so every surface stays cohesive.
 */
public final class GuiTheme {
    private GuiTheme() {
    }

    public static final float PANEL_RADIUS = 11.0F;
    public static final float SHELL_RADIUS = 11.0F;

    /** Soft violet accent used across cards, sliders and highlights. */
    public static final int ACCENT = rgb(180, 125, 255);
    public static final int ACCENT_SOFT = rgb(145, 115, 185);
    public static final int ACCENT_LIGHT = rgb(195, 135, 195);
    public static final int ACCENT_DEEP = rgb(125, 105, 145);

    public static final int TEXT_PRIMARY = rgb(235, 235, 245);
    public static final int TEXT_SECONDARY = rgb(150, 150, 160);

    /** Static color packs (full alpha) for quick use. */
    public static int rgb(int red, int green, int blue) {
        return new Color(red, green, blue, 255).getRGB();
    }

    public static int rgb(int red, int green, int blue, int alpha) {
        return new Color(red, green, blue, Math.max(0, Math.min(255, alpha))).getRGB();
    }

    public static int white(int alpha) {
        return rgb(255, 255, 255, alpha);
    }

    public static int black(int alpha) {
        return rgb(0, 0, 0, alpha);
    }

    /** Frosted glass surface fill (4-corner), tuned for the panel material. */
    public static int[] glassSurface(int alpha) {
        return new int[]{
                rgb(255, 255, 255, alpha),
                rgb(255, 255, 255, alpha),
                rgb(96, 96, 108, alpha),
                rgb(96, 96, 108, alpha)
        };
    }

    /** Hairline glass border: brighter on top, softer at the bottom. */
    public static int[] glassBorder(int alpha) {
        int soft = Math.min(255, (int) (alpha * 1.4F));
        return new int[]{
                rgb(255, 255, 255, alpha),
                rgb(255, 255, 255, alpha),
                rgb(140, 140, 150, soft),
                rgb(140, 140, 150, soft)
        };
    }

    /** Linear interpolation between two packed ARGB colors. */
    public static int lerpColor(int from, int to, float progress) {
        progress = Math.max(0.0F, Math.min(1.0F, progress));
        int fromA = (from >>> 24) & 0xFF;
        int fromR = (from >>> 16) & 0xFF;
        int fromG = (from >>> 8) & 0xFF;
        int fromB = from & 0xFF;
        int toA = (to >>> 24) & 0xFF;
        int toR = (to >>> 16) & 0xFF;
        int toG = (to >>> 8) & 0xFF;
        int toB = to & 0xFF;
        return ((fromA + Math.round((toA - fromA) * progress)) << 24)
                | ((fromR + Math.round((toR - fromR) * progress)) << 16)
                | ((fromG + Math.round((toG - fromG) * progress)) << 8)
                | (fromB + Math.round((toB - fromB) * progress));
    }
}
