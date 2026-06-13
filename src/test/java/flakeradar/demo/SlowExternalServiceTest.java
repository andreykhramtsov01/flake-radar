package flakeradar.demo;

import flakeradar.core.FlakinessExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Имитация внешнего вызова со случайной задержкой 80-120мс при таймауте
 * 100мс — иногда не укладывается.
 */
@ExtendWith(FlakinessExtension.class)
class SlowExternalServiceTest {

    @Test
    void respondsWithinTimeout() throws InterruptedException {
        long start = System.nanoTime();
        Thread.sleep(ThreadLocalRandom.current().nextLong(80, 121));
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(elapsedMillis <= 100, "ответ занял " + elapsedMillis + " ms");
    }
}
