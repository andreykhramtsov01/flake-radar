package flakeradar.core;

/**
 * Пара тестов, которые в наших прогонах падают вместе чаще, чем можно было
 * бы ожидать при независимых падениях.
 */
public record TestCorrelation(String testIdA, String testIdB, double correlation) {
}
