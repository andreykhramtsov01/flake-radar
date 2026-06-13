package flakeradar.demo;

/**
 * Простой калькулятор — контрольная группа для демо-сьюта, тесты на него
 * должны быть стабильны во всех прогонах.
 */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("деление на ноль");
        }
        return a / b;
    }
}
