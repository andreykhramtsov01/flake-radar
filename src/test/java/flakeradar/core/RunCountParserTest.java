package flakeradar.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RunCountParserTest {

    @AfterEach
    void clearSystemProperty() {
        System.clearProperty("runs");
    }

    @Test
    void usesDefaultWhenNothingProvided() {
        assertEquals(20, RunCountParser.parse(new String[0]));
    }

    @Test
    void parsesValueFromFirstArgument() {
        assertEquals(5, RunCountParser.parse(new String[] {"5"}));
    }

    @Test
    void parsesValueFromSystemPropertyWhenNoArgument() {
        System.setProperty("runs", "7");

        assertEquals(7, RunCountParser.parse(new String[0]));
    }

    @Test
    void argumentTakesPrecedenceOverSystemProperty() {
        System.setProperty("runs", "7");

        assertEquals(5, RunCountParser.parse(new String[] {"5"}));
    }

    @Test
    void rejectsZeroAndNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> RunCountParser.parse(new String[] {"0"}));
        assertThrows(IllegalArgumentException.class, () -> RunCountParser.parse(new String[] {"-3"}));
    }

    @Test
    void rejectsNonNumericValues() {
        assertThrows(IllegalArgumentException.class, () -> RunCountParser.parse(new String[] {"abc"}));
    }
}
