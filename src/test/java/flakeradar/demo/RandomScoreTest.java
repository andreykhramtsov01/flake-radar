package flakeradar.demo;

import flakeradar.core.FlakinessExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Без фиксированного seed — результат каждый раз разный, и тест падает
 * примерно в 30% прогонов.
 */
@ExtendWith(FlakinessExtension.class)
class RandomScoreTest {

    @Test
    void scoreIsAboveThreshold() {
        int score = new Random().nextInt(100);

        assertTrue(score >= 30, "score = " + score);
    }
}
