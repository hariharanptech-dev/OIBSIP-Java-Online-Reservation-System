package com.guessinggame.ui.components;

import com.guessinggame.ui.Theme;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Timer;

/** Track showing the still-possible range (animated) with tick marks for past guesses. */
public class RangeBar extends JComponent {
    private static final int PAD = 24, TRACK_H = 16, FRAME_MS = 16;
    private static final double EASE = 0.18, EPS = 0.05;
    private int min = 1, max = 100, targetLo = 1, targetHi = 100;
    private double lo = 1, hi = 100;
    private List<Integer> guesses = new ArrayList<>();
    private final Timer timer = new Timer(FRAME_MS, e -> step());

    public RangeBar() { if (getAccessibleContext() != null) getAccessibleContext().setAccessibleName("Remaining range"); }

    public void reset(int min, int max) {
        this.min = min; this.max = max; lo = targetLo = min; hi = targetHi = max; guesses = new ArrayList<>(); repaint();
    }
    /** Animates the valid zone toward [lo, hi]. */
    public void update(int lo, int hi, List<Integer> guesses) {
        targetLo = lo; targetHi = hi; this.guesses = new ArrayList<>(guesses); timer.start();
    }
    private void step() {
        lo += (targetLo - lo) * EASE; hi += (targetHi - hi) * EASE;
        if (Math.abs(lo - targetLo) < EPS && Math.abs(hi - targetHi) < EPS) { lo = targetLo; hi = targetHi; timer.stop(); }
        repaint();
    }
    private float x(double v) { return PAD + (float) ((v - min) / Math.max(1, max - min) * (getWidth() - 2 * PAD)); }

    @Override public Dimension getPreferredSize() { return new Dimension(420, 64); }
    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create(); Theme.hints(g);
        int y = 14;
        g.setColor(Theme.RAISED); g.fill(new RoundRectangle2D.Float(PAD, y, getWidth() - 2 * PAD, TRACK_H, TRACK_H, TRACK_H));
        float x1 = x(lo), x2 = Math.max(x(hi), x1 + 2);
        g.setPaint(new GradientPaint(x1, 0, Theme.ACCENT, x2, 0, Theme.PRIMARY_HOVER));
        g.fill(new RoundRectangle2D.Float(x1, y, x2 - x1, TRACK_H, TRACK_H, TRACK_H));
        g.setColor(Theme.TEXT); g.setStroke(new BasicStroke(2f));
        for (int v : guesses) g.drawLine(Math.round(x(v)), y - 4, Math.round(x(v)), y + TRACK_H + 4);
        g.setFont(Theme.CAPTION); g.setColor(Theme.MUTED);
        FontMetrics fm = g.getFontMetrics(); int ty = y + TRACK_H + 8 + fm.getAscent();
        g.drawString(String.valueOf(min), PAD, ty);
        String m = String.valueOf(max); g.drawString(m, getWidth() - PAD - fm.stringWidth(m), ty);
        String r = "Possible: " + targetLo + " – " + targetHi; g.setColor(Theme.ACCENT);
        g.drawString(r, (getWidth() - fm.stringWidth(r)) / 2, ty);
        g.dispose();
    }
}

