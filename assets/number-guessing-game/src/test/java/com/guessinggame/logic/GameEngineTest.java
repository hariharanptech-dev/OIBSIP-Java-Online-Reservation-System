package com.guessinggame.logic;

import static org.junit.jupiter.api.Assertions.*;
import com.guessinggame.model.Difficulty;
import com.guessinggame.model.GuessResult;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameEngineTest {
    private GameEngine engine(Difficulty d, int secret) { GameEngine e = new GameEngine(new Random(1)); e.start(d); e.setSecretForTest(secret); return e; }

    @Test void correctGuessWins() { var e = engine(Difficulty.MEDIUM, 42); assertEquals(GuessResult.CORRECT, e.guess(42)); assertTrue(e.isWon()); assertTrue(e.isOver()); }
    @Test void tooHighAndLow() { var e = engine(Difficulty.MEDIUM, 42); assertEquals(GuessResult.TOO_HIGH, e.guess(60)); assertEquals(GuessResult.TOO_LOW, e.guess(10)); }
    @Test void lossAfterMaxAttempts() {
        var e = engine(Difficulty.HARD, 200);
        for (int i = 1; i <= 5; i++) e.guess(i);
        assertTrue(e.isLost()); assertThrows(IllegalStateException.class, () -> e.guess(100));
    }
    @Test void rangeNarrows() { var e = engine(Difficulty.MEDIUM, 50); e.guess(80); e.guess(20); assertEquals(21, e.getLow()); assertEquals(79, e.getHigh()); }
    @Test void duplicatesDetectedAndNotCounted() {
        var e = engine(Difficulty.EASY, 5); e.guess(10);
        assertTrue(e.alreadyGuessed(10)); assertThrows(IllegalArgumentException.class, () -> e.guess(10)); assertEquals(1, e.attemptsUsed());
    }
    @Test void outOfRangeRejected() { var e = engine(Difficulty.EASY, 5); assertThrows(IllegalArgumentException.class, () -> e.guess(51)); assertEquals(0, e.attemptsUsed()); }
    @Test void difficultySettings() {
        assertEquals(10, Difficulty.EASY.getMaxAttempts()); assertEquals(50, Difficulty.EASY.getMax());
        assertEquals(7, Difficulty.MEDIUM.getMaxAttempts()); assertEquals(100, Difficulty.MEDIUM.getMax());
        assertEquals(5, Difficulty.HARD.getMaxAttempts()); assertEquals(200, Difficulty.HARD.getMax());
    }
    @Test void secretAlwaysInRange() {
        GameEngine e = new GameEngine();
        for (Difficulty d : Difficulty.values()) for (int i = 0; i < 500; i++) { e.start(d); assertTrue(e.getSecret() >= d.getMin() && e.getSecret() <= d.getMax()); }
    }
    @Test void temperature() { var e = engine(Difficulty.MEDIUM, 50); assertEquals("Very close!", e.temperature(53)); assertEquals("Getting warm", e.temperature(60)); assertEquals("Cold", e.temperature(90)); }
}
