package com.guessinggame.ui.components;

import com.guessinggame.ui.Theme;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** Hero title with a cyan-to-violet gradient fill and a gentle floating motion. */
public class GradientLabel extends JLabel {
    private static final int FRAME_MS = 33, FLOAT_PX = 3;
    private double phase;
    private final Timer timer = new Timer(FRAME_MS, e -> { phase += 0.06; repaint(); });

    public GradientLabel(String text) { super(text, SwingConstants.CENTER); setFont(Theme.HERO); setAlignmentX(0.5f); }
    @Override public void addNotify() { super.addNotify(); timer.start(); }
    @Override public void removeNotify() { timer.stop(); super.removeNotify(); }
    @Override public Dimension getPreferredSize() { Dimension d = super.getPreferredSize(); return new Dimension(d.width, d.height + 12); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create(); Theme.hints(g);
        g.setFont(getFont()); FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(getText()), x = (getWidth() - w) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + (int) Math.round(FLOAT_PX * Math.sin(phase));
        g.setPaint(new GradientPaint(x, 0, Theme.ACCENT, x + w, 0, Theme.VIOLET));
        g.drawString(getText(), x, y);
        g.dispose();
    }
}
