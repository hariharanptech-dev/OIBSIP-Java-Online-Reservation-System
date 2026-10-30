package com.guessinggame.model;

/** Immutable record of one finished round. */
public record RoundRecord(int round, Difficulty difficulty, int attempts, boolean won, int secret) {
    /** Human readable summary line shown in the scoreboard. */
    public String summary() {
        return won ? "Round " + round + " - guessed in " + attempts + (attempts == 1 ? " attempt" : " attempts")
                   : "Round " + round + " - Lost (number was " + secret + ")";
    }
}
