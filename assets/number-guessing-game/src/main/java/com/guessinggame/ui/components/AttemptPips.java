package com.guessinggame.ui.components;

import com.guessinggame.ui.Theme;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/** One circle per allowed attempt: used pips are filled with the guess result color. */
public class AttemptPips extends JComponent {
    private static final int D = 18, GAP = 10;
    private int total;
    private List<Color> used = new ArrayList<>();
    public AttemptPips() { setName("Attempts used"); }
    public void set(int total, List<Color> used) { this.total = total; this.used = new ArrayList<>(used); repaint(); }
    @Override public Dimension getPreferredSize() { return new Dimension(Math.max(1, total) * (D + GAP), D + 8); }
    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create(); Theme.hints(g);
        int x = (getWidth() - (total * (D + GAP) - GAP)) / 2, y = (getHeight() - D) / 2;
        for (int i = 0; i < total; i++, x += D + GAP) {
            if (i < used.size()) { g.setColor(used.get(i)); g.fillOval(x, y, D, D); }
            else { g.setColor(Theme.MUTED); g.setStroke(new BasicStroke(2f)); g.drawOval(x + 1, y + 1, D - 2, D - 2); }
        }
        g.dispose();
    }
}
