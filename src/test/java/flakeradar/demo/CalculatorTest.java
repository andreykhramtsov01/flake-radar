package flakeradar.demo;

import flakeradar.core.FlakinessExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Контрольная группа: обычные детерминированные тесты, должны быть
 * стабильны во всех прогонах FlakeRadar.
 */
@ExtendWith(FlakinessExtension.class)
class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addsTwoNumbers() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    void subtractsTwoNumbers() {
        assertEquals(1, calculator.subtract(3, 2));
    }

    @Test
    void multipliesTwoNumbers() {
        assertEquals(6, calculator.multiply(2, 3));
    }

    @Test
    void divisionByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> calculator.divide(1, 0));
    }
}
