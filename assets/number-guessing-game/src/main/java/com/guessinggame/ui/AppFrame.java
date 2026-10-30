package com.guessinggame.ui;

import com.guessinggame.logic.GameEngine;
import com.guessinggame.model.*;
import com.guessinggame.ui.components.*;
import com.guessinggame.util.InputValidator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/** Main window: hosts the Menu, Game, Result and Scoreboard screens in a CardLayout. */
public class AppFrame extends JFrame {
    private static final String MENU = "menu", GAME = "game", RESULT = "result", SCORE = "score";
    private static final int RESULT_DELAY_MS = 700, VISIBLE_ROWS = 8;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final GameSession session = new GameSession();
    private final GameEngine engine = new GameEngine();
    private Difficulty selected = Difficulty.MEDIUM;
    private String current = MENU;

    private final RoundedButton[] diffButtons = new RoundedButton[Difficulty.values().length];
    private final JLabel menuStats = label(" ", Theme.CAPTION, Theme.MUTED), rules = label(" ", Theme.BODY, Theme.TEXT);
    private final RoundedTextField input = new RoundedTextField();
    private final JLabel chip = label(" ", Theme.CAPTION, Theme.ACCENT), attemptLbl = label(" ", Theme.BODY_BOLD, Theme.TEXT),
        errorLbl = label(" ", Theme.CAPTION, Theme.DANGER), feedback = new PopLabel(),
        hint = label(" ", Theme.BODY, Theme.MUTED), history = label(" ", Theme.BODY, Theme.TEXT);
    private final RangeBar rangeBar = new RangeBar();
    private final AttemptPips pips = new AttemptPips();
    private final CardPanel resultCard = card();
    private final JLabel resTitle = label(" ", Theme.DISPLAY, Theme.TEXT), resSub = label(" ", Theme.TITLE, Theme.TEXT),
        resNote = label(" ", Theme.BODY, Theme.MUTED), resStars = label(" ", Theme.TITLE, Theme.WARNING);
    private final JPanel scoreRows = new JPanel();
    private final JLabel scoreSummary = label(" ", Theme.BODY, Theme.TEXT);
    private RoundedButton clearBtn;
    private boolean confirmClear;
    private final Confetti confetti = new Confetti();

    public AppFrame() {
        super("Number Guessing Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setMinimumSize(new Dimension(720, 600)); setSize(860, 680);
        setLocationRelativeTo(null);
        root.add(menu(), MENU); root.add(game(), GAME); root.add(screen(buildResult()), RESULT); root.add(screen(buildScore()), SCORE);
        setContentPane(root); setGlassPane(confetti); bindKeys(); selectDifficulty(Difficulty.MEDIUM); refreshMenu();
    }

    // ---------- helpers ----------
    private static JLabel label(String t, Font f, Color c) {
        JLabel l = new JLabel(t, SwingConstants.CENTER); l.setFont(f); l.setForeground(c); l.setAlignmentX(0.5f); return l;
    }
    private static CardPanel card() {
        CardPanel p = new CardPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(40, 56, 48, 56)); return p;
    }
    private static JPanel screen(JPanel card) { Bg p = new Bg(); p.setLayout(new GridBagLayout()); p.add(card); return p; }
    private static Component gap(int h) { return Box.createVerticalStrut(h); }
    private static <T extends JComponent> T center(T c) { c.setAlignmentX(0.5f); return c; }
    private static String hex(Color c) { return String.format("#%06x", c.getRGB() & 0xFFFFFF); }
    private void showCard(String name) { current = name; cards.show(root, name); }
    private RoundedButton button(String text, boolean ghost, Runnable r) {
        RoundedButton b = center(new RoundedButton(text)); b.setGhost(ghost); b.addActionListener(e -> r.run()); return b;
    }

    // ---------- menu ----------
    private JPanel menu() {
        CardPanel c = card();
        c.add(new GradientLabel("Number Guessing Game"));
        c.add(label("Pick a level. Find the secret number.", Theme.BODY, Theme.MUTED)); c.add(gap(24));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); row.setOpaque(false);
        for (Difficulty d : Difficulty.values()) {
            RoundedButton b = new RoundedButton("<html><center>" + d.getLabel() + " (" + (d.ordinal() + 1) + ")<br>"
                + d.getMin() + "–" + d.getMax() + " · " + d.getMaxAttempts() + " tries</center></html>");
            b.setGhost(true); b.setToolTipText(d.getLabel() + ": " + d.getMin() + "–" + d.getMax());
            b.addActionListener(e -> selectDifficulty(d)); diffButtons[d.ordinal()] = b; row.add(b);
        }
        c.add(row); c.add(gap(24));
        c.add(button("Start Game", false, this::startRound)); c.add(gap(8));
        c.add(button("How to play", true, () -> rules.setText(rules.getText().trim().isEmpty() ? "<html><center>1. Guess a number in the range.<br>"
            + "2. You'll hear Too High, Too Low or Correct.<br>3. Find it before your attempts run out.</center></html>" : " ")));
        c.add(rules); c.add(gap(16)); c.add(menuStats);
        return screen(c);
    }
    private void selectDifficulty(Difficulty d) {
        selected = d;
        for (Difficulty x : Difficulty.values()) diffButtons[x.ordinal()].setSelected(x == d);
    }
    private void refreshMenu() {
        menuStats.setText(session.rounds() == 0 ? " " : "Rounds " + session.rounds() + "  ·  Wins " + session.wins()
            + "  ·  Best " + (session.best().isPresent() ? session.best().getAsInt() + " attempts" : "–"));
    }

    // ---------- game ----------
    private JPanel game() {
        CardPanel c = card();
        JPanel top = new JPanel(new BorderLayout(16, 0)); top.setOpaque(false);
        RoundedButton back = new RoundedButton("← Menu"); back.setGhost(true); back.setToolTipText("Back to menu (Esc)");
        back.addActionListener(e -> toMenu());
        top.add(chip, BorderLayout.WEST); top.add(attemptLbl, BorderLayout.CENTER); top.add(back, BorderLayout.EAST);
        c.add(top); c.add(gap(24));
        JPanel in = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); in.setOpaque(false);
        RoundedButton guessBtn = new RoundedButton("Guess"); guessBtn.addActionListener(e -> submit());
        input.addActionListener(e -> submit());
        in.add(input); in.add(guessBtn); c.add(in); c.add(errorLbl); c.add(gap(8));
        c.add(feedback); c.add(hint); c.add(gap(16)); c.add(center(rangeBar)); c.add(gap(8)); c.add(center(pips)); c.add(gap(16)); c.add(history);
        return screen(c);
    }
    private void startRound() {
        engine.start(selected);
        input.setText(""); input.setInvalid(false); input.setEnabled(true);
        input.setPlaceholder("Enter a number " + engine.getMin() + "-" + engine.getMax());
        feedback.setText(" "); hint.setText(" "); errorLbl.setText(" ");
        rangeBar.reset(engine.getMin(), engine.getMax());
        refreshGame(); showCard(GAME);
        SwingUtilities.invokeLater(input::requestFocusInWindow);
    }
    private void error(String msg) { errorLbl.setText(msg); input.setInvalid(true); input.shake(); }

    private void submit() {
        if (engine.isOver()) return;
        InputValidator.Result v = InputValidator.validate(input.getText(), engine.getMin(), engine.getMax());
        if (!v.ok()) { error(v.message()); return; }
        int g = v.value();
        if (engine.alreadyGuessed(g)) { error("You already tried " + g); return; }
        GuessResult r = engine.guess(g);
        errorLbl.setText(" ");
        switch (r) {
            case TOO_HIGH -> { feedback.setText("Too High!"); feedback.setForeground(Theme.DANGER); hint.setText("▼ Go lower · " + engine.temperature(g)); }
            case TOO_LOW -> { feedback.setText("Too Low!"); feedback.setForeground(Theme.WARNING); hint.setText("▲ Go higher · " + engine.temperature(g)); }
            default -> { feedback.setText("Correct!"); feedback.setForeground(Theme.SUCCESS); hint.setText("You nailed it!"); }
        }
        input.setText(""); refreshGame();
        if (engine.isOver()) {
            input.setEnabled(false);
            Timer t = new Timer(RESULT_DELAY_MS, e -> showResult()); t.setRepeats(false); t.start();
        } else input.requestFocusInWindow();
    }
    private void refreshGame() {
        Difficulty d = engine.getDifficulty();
        chip.setText(d.getLabel().toUpperCase());
        attemptLbl.setText("Attempt " + engine.attemptsUsed() + " / " + d.getMaxAttempts());
        attemptLbl.setForeground(engine.attemptsLeft() == 1 && !engine.isOver() ? Theme.DANGER : Theme.TEXT);
        List<Color> used = new ArrayList<>();
        for (GuessResult r : engine.getResults()) used.add(r == GuessResult.TOO_LOW ? Theme.WARNING : r == GuessResult.TOO_HIGH ? Theme.DANGER : Theme.SUCCESS);
        pips.set(d.getMaxAttempts(), used);
        rangeBar.update(engine.getLow(), engine.getHigh(), engine.getGuesses());
        StringBuilder sb = new StringBuilder("<html><body style='width:420px;text-align:center'>");
        for (int i = 0; i < engine.getGuesses().size(); i++) {
            GuessResult r = engine.getResults().get(i);
            sb.append("<font color='").append(hex(r == GuessResult.TOO_LOW ? Theme.WARNING : r == GuessResult.TOO_HIGH ? Theme.DANGER : Theme.SUCCESS)).append("'>")
              .append(r == GuessResult.TOO_LOW ? "▲ " : r == GuessResult.TOO_HIGH ? "▼ " : "✓ ").append(engine.getGuesses().get(i)).append("</font>&nbsp;&nbsp; ");
        }
        history.setText(engine.getGuesses().isEmpty() ? " " : sb.append("</body></html>").toString());
    }

    // ---------- result ----------
    private JPanel buildResult() {
        resultCard.add(resTitle); resultCard.add(resSub); resultCard.add(resStars); resultCard.add(resNote); resultCard.add(gap(24));
        resultCard.add(button("Play Again (N)", false, this::startRound)); resultCard.add(gap(8));
        resultCard.add(button("Change Difficulty", true, this::toMenu)); resultCard.add(gap(8));
        resultCard.add(button("Scoreboard", true, this::showScore));
        return resultCard;
    }
    private void showResult() {
        boolean won = engine.isWon(); int n = engine.attemptsUsed();
        session.add(new RoundRecord(session.nextRoundNumber(), engine.getDifficulty(), n, won, engine.getSecret()));
        if (won) {
            int stars = n <= 2 ? 3 : n <= 4 ? 2 : 1;
            resultCard.setTint(new Color(Theme.SUCCESS.getRed(), Theme.SUCCESS.getGreen(), Theme.SUCCESS.getBlue(), 28));
            resTitle.setText("Correct!"); resTitle.setForeground(Theme.SUCCESS);
            resSub.setText("You got it in " + n + (n == 1 ? " attempt" : " attempts"));
            resStars.setText("★".repeat(stars) + "☆".repeat(3 - stars));
            resNote.setText(n <= 2 ? "Legendary" : n <= 4 ? "Great" : "Nice work");
        } else {
            resultCard.setTint(new Color(Theme.DANGER.getRed(), Theme.DANGER.getGreen(), Theme.DANGER.getBlue(), 36));
            resTitle.setText("You Lost!"); resTitle.setForeground(Theme.DANGER);
            resSub.setText("The number was " + engine.getSecret()); resStars.setText(" "); resNote.setText("So close. The next one is yours!");
        }
        showCard(RESULT);
        if (won) confetti.burst();
    }

    // ---------- scoreboard ----------
    private JPanel buildScore() {
        CardPanel c = resultCardFor();
        c.add(label("Scoreboard", Theme.TITLE, Theme.TEXT)); c.add(gap(16)); c.add(scoreSummary); c.add(gap(16));
        scoreRows.setLayout(new BoxLayout(scoreRows, BoxLayout.Y_AXIS)); scoreRows.setOpaque(false); c.add(scoreRows); c.add(gap(24));
        clearBtn = button("Clear History", true, () -> {
            if (!confirmClear) { confirmClear = true; clearBtn.setText("Really clear? Click again"); return; }
            session.clear(); refreshScore(); refreshMenu();
        });
        c.add(clearBtn); c.add(gap(8)); c.add(button("Back", false, this::toMenu));
        return c;
    }
    private CardPanel resultCardFor() { return card(); }
    private void showScore() { confirmClear = false; clearBtn.setText("Clear History"); refreshScore(); showCard(SCORE); }
    private void refreshScore() {
        scoreSummary.setText(session.rounds() == 0 ? "No rounds yet. Your first win will show up here!" :
            String.format("Rounds %d  ·  Win rate %d%%  ·  Best %s  ·  Avg %.1f", session.rounds(), session.winRatePercent(),
                session.best().isPresent() ? session.best().getAsInt() : "–", session.averageWinAttempts()));
        scoreRows.removeAll();
        List<RoundRecord> h = session.getHistory();
        for (int i = Math.max(0, h.size() - VISIBLE_ROWS); i < h.size(); i++) {
            RoundRecord r = h.get(i);
            JLabel l = new JLabel("<html>" + r.summary() + " &nbsp;<font color='" + hex(r.won() ? Theme.SUCCESS : Theme.DANGER) + "'>"
                + (r.won() ? "✓ WIN" : "✗ LOSS") + "</font> <font color='" + hex(Theme.MUTED) + "'>" + r.difficulty().getLabel() + "</font></html>");
            l.setFont(Theme.BODY); l.setForeground(Theme.TEXT); l.setOpaque(true); l.setAlignmentX(0.5f);
            l.setBackground(i % 2 == 0 ? Theme.RAISED : Theme.SURFACE); l.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            scoreRows.add(l);
        }
        scoreRows.revalidate(); scoreRows.repaint();
    }

    // ---------- navigation and keys ----------
    private void toMenu() { refreshMenu(); showCard(MENU); }
    private void bindKeys() {
        bind("ESCAPE", () -> { if (!current.equals(MENU)) toMenu(); });
        bind("N", () -> { if (current.equals(RESULT)) startRound(); });
        for (int i = 1; i <= 3; i++) { final int idx = i - 1; bind(String.valueOf(i), () -> { if (current.equals(MENU)) selectDifficulty(Difficulty.values()[idx]); }); }
    }
    private void bind(String key, Runnable r) {
        JRootPane rp = getRootPane(); String id = "key-" + key;
        rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Character.isDigit(key.charAt(0)) ? "typed " + key : key.equals("N") ? "pressed N" : key), id);
        rp.getActionMap().put(id, new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { r.run(); } });
    }

    /** Deep navy base with three slowly drifting blurred color orbs. */
    private static final class Bg extends JPanel {
        private static final int FRAME_MS = 25, ORB_ALPHA = 85;
        private double phase = Math.random() * 10;
        private final Color[] orbs = { Theme.PRIMARY, Theme.ACCENT, Theme.VIOLET };
        private final Timer timer = new Timer(FRAME_MS, e -> { phase += 0.004; if (isShowing()) repaint(); });
        @Override public void addNotify() { super.addNotify(); timer.start(); }
        @Override public void removeNotify() { timer.stop(); super.removeNotify(); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create(); int w = getWidth(), h = getHeight();
            g.setColor(Theme.BG); g.fillRect(0, 0, w, h);
            float r = Math.max(w, h) * 0.45f;
            for (int i = 0; i < orbs.length; i++) {
                Color c = orbs[i];
                double cx = w * (0.5 + 0.4 * Math.sin(phase * (1 + i * 0.6) + i * 2)), cy = h * (0.5 + 0.35 * Math.cos(phase * (0.8 + i * 0.5) + i * 3));
                g.setPaint(new RadialGradientPaint(new java.awt.geom.Point2D.Double(cx, cy), r, new float[] { 0f, 1f },
                    new Color[] { new Color(c.getRed(), c.getGreen(), c.getBlue(), ORB_ALPHA), new Color(c.getRed(), c.getGreen(), c.getBlue(), 0) }));
                g.fillRect(0, 0, w, h);
            }
            g.dispose();
        }
    }
    /** Elevated rounded card with layered soft shadow and optional tint. */
    private static final class CardPanel extends JPanel {
        private static final int SHADOW_LAYERS = 6;
        private Color tint;
        CardPanel() { setOpaque(false); }
        void setTint(Color c) { tint = c; repaint(); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create(); Theme.hints(g);
            int x = 12, y = 8, w = getWidth() - 24, h = getHeight() - 24, r = Theme.CARD_R * 2;
            for (int i = SHADOW_LAYERS; i >= 1; i--) {
                g.setColor(new Color(0, 0, 0, 14)); g.fill(new RoundRectangle2D.Float(x - i, y - i + 6, w + 2 * i, h + 2 * i, r + i, r + i));
            }
            RoundRectangle2D.Float s = new RoundRectangle2D.Float(x, y, w, h, r, r);
            g.setColor(new Color(Theme.SURFACE.getRed(), Theme.SURFACE.getGreen(), Theme.SURFACE.getBlue(), 215)); g.fill(s);
            g.setPaint(new GradientPaint(0, y, new Color(255, 255, 255, 38), 0, y + 70, new Color(255, 255, 255, 0))); g.fill(s);
            if (tint != null) { g.setColor(tint); g.fill(s); }
            g.setColor(new Color(255, 255, 255, 30)); g.setStroke(new BasicStroke(1f)); g.draw(s);
            g.dispose();
        }
    }
}
