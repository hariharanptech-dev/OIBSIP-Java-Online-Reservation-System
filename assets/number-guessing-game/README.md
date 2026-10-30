# Number Guessing Game (Java Swing)
Dark-themed desktop game, zero UI libraries; all styling is custom Graphics2D painting.

**Prerequisites:** JDK 17+, Maven 3.8+.
**Run:** `mvn exec:java` · **Package:** `mvn clean package` then `java -jar target/number-guessing-game-1.0.0.jar` · **Test:** `mvn test`

**Controls:** Enter = guess, Esc = menu, N = new round (result screen), 1/2/3 = Easy/Medium/Hard (menu).

**Difficulty:** Easy 1-50 (10 tries), Medium 1-100 (7), Hard 1-200 (5).

**Design:** colors, fonts, spacing and radii live in `ui/Theme.java` (indigo primary, cyan accent, green/amber/red for result states, always paired with text and arrow glyphs). Components: RoundedButton, RoundedTextField (digits only, shake on invalid), RangeBar (animated remaining range), AttemptPips.
