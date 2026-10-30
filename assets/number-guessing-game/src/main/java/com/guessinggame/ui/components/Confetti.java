package com.guessinggame.ui.components;

import com.guessinggame.ui.Theme;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.Timer;

/** Glass-pane confetti burst with gravity, rotation and fade-out; mouse events pass through. */
public class Confetti extends JComponent {
    private static final int COUNT = 150, DURATION_MS = 2500, FADE_START_MS = 1600, FRAME_MS = 16;
    private static final double GRAVITY = 0.35, DRAG = 0.99;
    private final double[] x = new double[COUNT], y = new double[COUNT], vx = new double[COUNT], vy = new double[COUNT],
        rot = new double[COUNT], vr = new double[COUNT];
    private final Color[] col = new Color[COUNT];
    private final Color[] palette = { Theme.ACCENT, Theme.PRIMARY_HOVER, Theme.VIOLET, Theme.SUCCESS, Theme.WARNING, Theme.TEXT };
    private final Random rnd = new Random();
    private long start;
    private final Timer timer = new Timer(FRAME_MS, e -> tick());

    public Confetti() { setOpaque(false); setVisible(false); }
    @Override public boolean contains(int px, int py) { return false; }

    /** Fires a burst from the upper-center of the window. */
    public void burst() {
        setVisible(true); start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            double a = rnd.nextDouble() * 2 * Math.PI, s = 4 + rnd.nextDouble() * 9;
            x[i] = getWidth() / 2.0; y[i] = getHeight() * 0.4; vx[i] = Math.cos(a) * s; vy[i] = Math.sin(a) * s - 4;
            rot[i] = rnd.nextDouble() * 6; vr[i] = (rnd.nextDouble() - 0.5) * 0.4; col[i] = palette[rnd.nextInt(palette.length)];
        }
        timer.start();
    }
    private void tick() {
        for (int i = 0; i < COUNT; i++) { x[i] += vx[i]; y[i] += vy[i]; vy[i] += GRAVITY; vx[i] *= DRAG; rot[i] += vr[i]; }
        if (System.currentTimeMillis() - start >= DURATION_MS) { timer.stop(); setVisible(false); }
        repaint();
    }
    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create(); Theme.hints(g);
        long el = System.currentTimeMillis() - start;
        float fade = (float) Math.max(0, 1 - Math.max(0, el - FADE_START_MS) / (double) (DURATION_MS - FADE_START_MS));
        g.setComposite(AlphaComposite.SrcOver.derive(fade));
        for (int i = 0; i < COUNT; i++) {
            AffineTransform old = g.getTransform();
            g.translate(x[i], y[i]); g.rotate(rot[i]); g.setColor(col[i]); g.fillRect(-5, -3, 10, 6); g.setTransform(old);
        }
        g.dispose();
    }
}
