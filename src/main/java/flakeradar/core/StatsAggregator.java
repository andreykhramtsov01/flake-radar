package flakeradar.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сворачивает список {@link TestExecutionRecord} по N прогонов в статистику
 * по каждому тесту.
 */
public class StatsAggregator {

    public List<TestStats> aggregate(List<TestExecutionRecord> records) {
        Map<String, List<TestExecutionRecord>> byTest = records.stream()
                .collect(Collectors.groupingBy(TestExecutionRecord::testId, LinkedHashMap::new, Collectors.toList()));

        List<TestStats> result = new ArrayList<>();
        for (Map.Entry<String, List<TestExecutionRecord>> entry : byTest.entrySet()) {
            result.add(toStats(entry.getKey(), entry.getValue()));
        }

        result.sort(Comparator.comparingDouble(TestStats::flakinessRate).reversed());
        return result;
    }

    private TestStats toStats(String testId, List<TestExecutionRecord> records) {
        int passCount = 0;
        int failCount = 0;
        int abortedCount = 0;
        long minDuration = Long.MAX_VALUE;
        long maxDuration = Long.MIN_VALUE;
        long totalDuration = 0;

        for (TestExecutionRecord record : records) {
            switch (record.outcome()) {
                case PASSED -> passCount++;
                case FAILED -> failCount++;
                case ABORTED -> abortedCount++;
            }
            minDuration = Math.min(minDuration, record.durationMillis());
            maxDuration = Math.max(maxDuration, record.durationMillis());
            totalDuration += record.durationMillis();
        }

        int totalRuns = records.size();
        double flakinessRate = (double) Math.min(passCount, failCount) / totalRuns;

        return new TestStats(
                testId,
                records.get(0).displayName(),
                totalRuns,
                passCount,
                failCount,
                abortedCount,
                flakinessRate,
                minDuration,
                totalDuration / totalRuns,
                maxDuration
        );
    }
}
