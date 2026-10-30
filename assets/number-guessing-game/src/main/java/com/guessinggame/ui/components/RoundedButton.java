package com.guessinggame.ui.components;

import com.guessinggame.ui.Theme;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/** Custom-painted button: filled or ghost, with hover, pressed, disabled, focus and selected states. */
public class RoundedButton extends JButton {
    private boolean ghost, hover;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
        setForeground(Theme.TEXT); setFont(Theme.BODY_BOLD);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(Theme.SP, Theme.SP * 3, Theme.SP, Theme.SP * 3));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }
    /** Ghost buttons are outlined; they fill with primary when selected. */
    public void setGhost(boolean g) { ghost = g; repaint(); }

    @Override public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, Theme.MIN_TARGET * 2) + 10, Math.max(d.height, Theme.MIN_TARGET) + 10);
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create(); Theme.hints(g);
        int w = getWidth(), h = getHeight(); float arc = Theme.BTN_R * 2;
        if (getModel().isPressed() && isEnabled()) {
            g.translate(w / 2.0, h / 2.0); g.scale(0.97, 0.97); g.translate(-w / 2.0, -h / 2.0);
        }
        if (hover && isEnabled()) g.translate(0, -2);
        Shape s = new RoundRectangle2D.Float(5, 5, w - 10, h - 10, arc, arc);
        if (hover && isEnabled() && (!ghost || isSelected())) for (int i = 4; i >= 1; i--) {
            g.setColor(new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 24));
            g.fill(new RoundRectangle2D.Float(5 - i * 2, 5 - i * 2, w - 10 + 4 * i, h - 10 + 4 * i, arc + i * 4, arc + i * 4));
        }
        boolean filled = !ghost || isSelected();
        Color fill = !isEnabled() ? Theme.RAISED : filled ? (hover ? Theme.PRIMARY_HOVER : Theme.PRIMARY)
                : hover ? Theme.RAISED : new Color(0, 0, 0, 0);
        if (filled && isEnabled()) g.setPaint(new GradientPaint(0, 0, hover ? Theme.PRIMARY_HOVER : Theme.PRIMARY, w, h, Theme.VIOLET)); else g.setColor(fill);
        g.fill(s);
        if (ghost && !isSelected()) { g.setColor(hover ? Theme.PRIMARY_HOVER : Theme.MUTED); g.setStroke(new BasicStroke(1.5f)); g.draw(s); }
        super.paintComponent(g);
        if (hasFocus()) { g.setColor(Theme.ACCENT); g.setStroke(new BasicStroke(2f)); g.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc + 4, arc + 4)); }
        g.dispose();
    }
}
