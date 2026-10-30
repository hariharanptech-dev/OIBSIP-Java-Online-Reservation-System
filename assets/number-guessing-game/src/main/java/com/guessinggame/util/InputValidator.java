package com.guessinggame.util;

/** Parses and validates raw guess text. */
public final class InputValidator {
    private InputValidator() { }
    /** Result: value is non-null when valid, otherwise message explains why. */
    public record Result(Integer value, String message) { public boolean ok() { return value != null; } }

    public static Result validate(String raw, int min, int max) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) return new Result(null, "Type a number to make your guess");
        if (!s.matches("\\d{1,9}")) return new Result(null, "Digits only, please");
        int v = Integer.parseInt(s);
        if (v < min || v > max) return new Result(null, "Enter a number between " + min + " and " + max);
        return new Result(v, "");
    }
}
