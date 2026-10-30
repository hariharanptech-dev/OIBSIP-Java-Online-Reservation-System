package com.guessinggame.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;

/** Design tokens: colors, fonts, spacing, radii and rendering hints. */
public final class Theme {
    private Theme() { }
    public static final Color BG = new Color(0x0F172A), SURFACE = new Color(0x1E293B), RAISED = new Color(0x273449),
        PRIMARY = new Color(0x6366F1), PRIMARY_HOVER = new Color(0x818CF8), ACCENT = new Color(0x22D3EE),
        SUCCESS = new Color(0x22C55E), WARNING = new Color(0xF59E0B), DANGER = new Color(0xEF4444),
        TEXT = new Color(0xF1F5F9), MUTED = new Color(0x94A3B8), VIOLET = new Color(0x8B5CF6);
    public static final int SP = 8, CARD_R = 20, BTN_R = 12, MIN_TARGET = 44;
    private static final String FAMILY = pickFamily();
    public static final Font HERO = font(40, true), DISPLAY = font(48, true), TITLE = font(28, true),
        BODY = font(16, false), BODY_BOLD = font(16, true), CAPTION = font(13, false), INPUT = font(32, true);

    private static String pickFamily() {
        var names = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        return names.contains("Inter") ? "Inter" : names.contains("Segoe UI") ? "Segoe UI" : "SansSerif";
    }
    public static Font font(float size, boolean bold) { return new Font(FAMILY, bold ? Font.BOLD : Font.PLAIN, 1).deriveFont(size); }
    /** Applies quality rendering hints. */
    public static void hints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}
