package com.guessinggame.ui.components;

import com.guessinggame.ui.Theme;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** Large label that pops in with a spring scale (0.6 to 1.0, overshoot) and a color-matched glow whenever its text changes. */
public class PopLabel extends JLabel {
    private static final int FRAME_MS = 16, DURATION_MS = 450, GLOW_STEPS = 8, GLOW_RADIUS = 3;
    private double scale = 1, alpha = 1;
    private long start;
    private final Timer timer = new Timer(FRAME_MS, e -> tick());

    public PopLabel() { super(" ", SwingConstants.CENTER); setFont(Theme.DISPLAY); setForeground(Theme.TEXT); setAlignmentX(0.5f); }

    @Override public void setText(String t) {
        super.setText(t);
        if (timer != null && t != null && !t.isBlank()) { start = System.currentTimeMillis(); timer.start(); }
    }
    private void tick() {
        double t = (System.currentTimeMillis() - start) / (double) DURATION_MS;
        if (t >= 1) { scale = 1; alpha = 1; timer.stop(); }
        else { scale = 1 - 0.4 * Math.exp(-6 * t) * Math.cos(12 * t); alpha = Math.min(1, t * 6); }
        repaint();
    }
    @Override public Dimension getPreferredSize() { Dimension d = super.getPreferredSize(); return new Dimension(d.width + 48, d.height + 16); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create(); Theme.hints(g);
        g.setFont(getFont()); FontMetrics fm = g.getFontMetrics();
        g.translate(getWidth() / 2.0, getHeight() / 2.0); g.scale(scale, scale);
        int x = -fm.stringWidth(getText()) / 2, y = (fm.getAscent() - fm.getDescent()) / 2;
        Color c = getForeground();
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (38 * alpha)));
        for (int i = 0; i < GLOW_STEPS; i++) {
            double a = i * 2 * Math.PI / GLOW_STEPS;
            g.drawString(getText(), x + (int) Math.round(GLOW_RADIUS * 2 * Math.cos(a)), y + (int) Math.round(GLOW_RADIUS * 2 * Math.sin(a)));
        }
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 * alpha)));
        g.drawString(getText(), x, y);
        g.dispose();
    }
}
