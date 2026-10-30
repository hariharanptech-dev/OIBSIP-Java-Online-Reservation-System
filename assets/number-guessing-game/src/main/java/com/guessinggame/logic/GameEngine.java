package com.guessinggame.logic;

import com.guessinggame.model.Difficulty;
import com.guessinggame.model.GuessResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Pure game logic (no Swing). Tracks one round at a time. */
public class GameEngine {
    private static final int VERY_CLOSE = 5, WARM = 15;
    private final Random random;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private int secret, low, high;
    private boolean won;
    private final List<Integer> guesses = new ArrayList<>();
    private final List<GuessResult> results = new ArrayList<>();

    public GameEngine() { this(new Random()); }
    public GameEngine(Random random) { this.random = random; start(difficulty); }

    /** Starts a fresh round with a random secret inside the difficulty's range. */
    public void start(Difficulty d) {
        difficulty = d; low = d.getMin(); high = d.getMax(); won = false;
        guesses.clear(); results.clear();
        secret = d.getMin() + random.nextInt(d.getMax() - d.getMin() + 1);
    }

    /** True if this number was already guessed this round. */
    public boolean alreadyGuessed(int g) { return guesses.contains(g); }

    /** Evaluates a guess. Throws IllegalArgumentException for out-of-range or duplicate values. */
    public GuessResult guess(int g) {
        if (isOver()) throw new IllegalStateException("Round is over");
        if (g < difficulty.getMin() || g > difficulty.getMax()) throw new IllegalArgumentException("Out of range");
        if (alreadyGuessed(g)) throw new IllegalArgumentException("Duplicate guess");
        GuessResult r;
        if (g == secret) { won = true; r = GuessResult.CORRECT; }
        else if (g > secret) { r = GuessResult.TOO_HIGH; high = Math.min(high, g - 1); }
        else { r = GuessResult.TOO_LOW; low = Math.max(low, g + 1); }
        guesses.add(g); results.add(r);
        return r;
    }

    /** Temperature hint for a guess. */
    public String temperature(int g) {
        int d = Math.abs(g - secret);
        if (d == 0) return "You got it!";
        return d <= VERY_CLOSE ? "Very close!" : d <= WARM ? "Getting warm" : "Cold";
    }

    public Difficulty getDifficulty() { return difficulty; }
    public int getMin() { return difficulty.getMin(); }
    public int getMax() { return difficulty.getMax(); }
    public int getLow() { return low; }
    public int getHigh() { return high; }
    public int getSecret() { return secret; }
    public int attemptsUsed() { return guesses.size(); }
    public int attemptsLeft() { return difficulty.getMaxAttempts() - guesses.size(); }
    public boolean isWon() { return won; }
    public boolean isLost() { return !won && attemptsLeft() <= 0; }
    public boolean isOver() { return won || isLost(); }
    public List<Integer> getGuesses() { return Collections.unmodifiableList(guesses); }
    public List<GuessResult> getResults() { return Collections.unmodifiableList(results); }

    /** Test hook: fixes the secret number. */
    void setSecretForTest(int s) { secret = s; }
}
