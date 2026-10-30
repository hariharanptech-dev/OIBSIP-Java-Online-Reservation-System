package com.guessinggame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/** Session history and statistics across rounds. */
public class GameSession {
    private final List<RoundRecord> history = new ArrayList<>();
    public int nextRoundNumber() { return history.size() + 1; }
    public void add(RoundRecord r) { history.add(r); }
    public void clear() { history.clear(); }
    public List<RoundRecord> getHistory() { return Collections.unmodifiableList(history); }
    public int rounds() { return history.size(); }
    public int wins() { return (int) history.stream().filter(RoundRecord::won).count(); }
    public int winRatePercent() { return rounds() == 0 ? 0 : Math.round(100f * wins() / rounds()); }
    public OptionalInt best() { return history.stream().filter(RoundRecord::won).mapToInt(RoundRecord::attempts).min(); }
    public double averageWinAttempts() { return history.stream().filter(RoundRecord::won).mapToInt(RoundRecord::attempts).average().orElse(0); }
}
