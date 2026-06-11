package flakeradar.core;

/**
 * Сводная статистика по одному тесту за все прогоны.
 */
public record TestStats(
        String testId,
        String displayName,
        int totalRuns,
        int passCount,
        int failCount,
        int abortedCount,
        double flakinessRate,
        long minDurationMillis,
        long avgDurationMillis,
        long maxDurationMillis
) {
}
