package com.guessinggame.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class InputValidatorTest {
    @Test void validNumber() { var r = InputValidator.validate(" 42 ", 1, 100); assertTrue(r.ok()); assertEquals(42, r.value()); }
    @Test void emptyRejected() { assertFalse(InputValidator.validate("  ", 1, 100).ok()); assertFalse(InputValidator.validate(null, 1, 100).ok()); }
    @Test void nonNumericRejected() { assertFalse(InputValidator.validate("abc", 1, 100).ok()); assertFalse(InputValidator.validate("-5", 1, 100).ok()); }
    @Test void outOfRangeRejected() { assertEquals("Enter a number between 1 and 100", InputValidator.validate("101", 1, 100).message()); assertFalse(InputValidator.validate("0", 1, 100).ok()); }
    @Test void boundariesAccepted() { assertTrue(InputValidator.validate("1", 1, 100).ok()); assertTrue(InputValidator.validate("100", 1, 100).ok()); }
}
