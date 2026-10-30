package com.guessinggame.model;

/** Difficulty levels with number range and attempt limit. */
public enum Difficulty {
    EASY("Easy", 1, 50, 10), MEDIUM("Medium", 1, 100, 7), HARD("Hard", 1, 200, 5);
    private final String label; private final int min, max, maxAttempts;
    Difficulty(String label, int min, int max, int maxAttempts) {
        this.label = label; this.min = min; this.max = max; this.maxAttempts = maxAttempts;
    }
    public String getLabel() { return label; }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public int getMaxAttempts() { return maxAttempts; }
}
