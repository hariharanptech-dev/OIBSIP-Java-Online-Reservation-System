package com.guessinggame.ui.components;

import com.guessinggame.ui.Theme;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/** Large centered digits-only input with placeholder, focus/invalid border and a shake animation. */
public class RoundedTextField extends JTextField {
    private static final int MAX_DIGITS = 3, FRAME_MS = 16, SHAKE_FRAMES = 40;
    private String placeholder = "";
    private boolean invalid;
    private int offset, frame;
    private Timer shakeTimer;

    public RoundedTextField() {
        setHorizontalAlignment(SwingConstants.CENTER); setFont(Theme.INPUT); setOpaque(false);
        setForeground(Theme.TEXT); setCaretColor(Theme.ACCENT);
        setBorder(BorderFactory.createEmptyBorder(Theme.SP, Theme.SP * 2, Theme.SP, Theme.SP * 2));
        getAccessibleContext().setAccessibleName("Guess input");
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void insertString(FilterBypass fb, int off, String s, AttributeSet a) throws BadLocationException { replace(fb, off, 0, s, a); }
            @Override public void replace(FilterBypass fb, int off, int len, String s, AttributeSet a) throws BadLocationException {
                if (s == null || s.isEmpty()) { super.replace(fb, off, len, s, a); setInvalid(false); return; }
                String digits = s.replaceAll("\\D", "");
                int room = MAX_DIGITS - (fb.getDocument().getLength() - len);
                if (digits.isEmpty() || room <= 0) return;
                super.replace(fb, off, len, digits.substring(0, Math.min(room, digits.length())), a);
                setInvalid(false);
            }
        });
    }
    public void setPlaceholder(String p) { placeholder = p; repaint(); }
    public void setInvalid(boolean v) { invalid = v; repaint(); }

    /** Damped horizontal oscillation. */
    public void shake() {
        if (shakeTimer != null) shakeTimer.stop();
        frame = 0;
        shakeTimer = new Timer(FRAME_MS, e -> {
            frame++; offset = (int) (10 * Math.exp(-frame / 10.0) * Math.sin(frame * 0.9));
            if (frame > SHAKE_FRAMES) { offset = 0; ((Timer) e.getSource()).stop(); }
            repaint();
        });
        shakeTimer.start();
    }

    @Override public Dimension getPreferredSize() { return new Dimension(260, 64); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create(); Theme.hints(g);
        g.translate(offset, 0);
        int w = getWidth(), h = getHeight(); float arc = Theme.BTN_R * 2;
        Shape s = new RoundRectangle2D.Float(2, 2, w - 4, h - 4, arc, arc);
        g.setColor(Theme.RAISED); g.fill(s);
        g.setColor(invalid ? Theme.DANGER : isFocusOwner() ? Theme.PRIMARY : Theme.MUTED);
        g.setStroke(new BasicStroke(isFocusOwner() || invalid ? 2.5f : 1.5f)); g.draw(s);
        if (getText().isEmpty()) {
            g.setFont(Theme.BODY); g.setColor(Theme.MUTED);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(placeholder, (w - fm.stringWidth(placeholder)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
        }
        super.paintComponent(g);
        g.dispose();
    }
}
