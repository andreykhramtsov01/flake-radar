package flakeradar.demo;

import flakeradar.core.FlakinessExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Два теста делят статический счётчик, порядок выполнения случайный
 * (PerRunRandomOrder). В каждом прогоне ровно один из них падает —
 * какой именно, зависит от порядка, поэтому их падения должны попасть в
 * секцию корреляций отчёта.
 */
@ExtendWith(FlakinessExtension.class)
@TestMethodOrder(PerRunRandomOrder.class)
class SharedCounterOrderTest {

    private static int counter;

    @BeforeAll
    static void resetCounter() {
        counter = 0;
    }

    @Test
    void stepOneBumpsCounter() {
        counter++;
        assertEquals(2, counter, "counter должен стать 2 после обоих шагов");
    }

    @Test
    void stepTwoBumpsCounter() {
        counter++;
        assertEquals(2, counter, "counter должен стать 2 после обоих шагов");
    }
}
