package flakeradar.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static flakeradar.core.TestExecutionRecord.Outcome.FAILED;
import static flakeradar.core.TestExecutionRecord.Outcome.PASSED;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsAggregatorTest {

    @Test
    void marksTestAsFlakyWhenItSometimesFails() {
        List<TestExecutionRecord> records = List.of(
                record("a", 0, PASSED, 10),
                record("a", 1, FAILED, 12),
                record("a", 2, PASSED, 11),
                record("a", 3, PASSED, 9),
                record("a", 4, FAILED, 13)
        );

        TestStats stats = new StatsAggregator().aggregate(records).get(0);

        assertEquals(5, stats.totalRuns());
        assertEquals(3, stats.passCount());
        assertEquals(2, stats.failCount());
        assertEquals(0.4, stats.flakinessRate(), 1e-9);
    }

    @Test
    void stableTestHasZeroFlakiness() {
        List<TestExecutionRecord> records = List.of(
                record("a", 0, PASSED, 10),
                record("a", 1, PASSED, 12),
                record("a", 2, PASSED, 11)
        );

        TestStats stats = new StatsAggregator().aggregate(records).get(0);

        assertEquals(0.0, stats.flakinessRate());
    }

    @Test
    void alwaysFailingTestHasZeroFlakiness() {
        List<TestExecutionRecord> records = List.of(
                record("a", 0, FAILED, 10),
                record("a", 1, FAILED, 12)
        );

        TestStats stats = new StatsAggregator().aggregate(records).get(0);

        assertEquals(0.0, stats.flakinessRate());
    }

    @Test
    void computesMinAvgMaxDuration() {
        List<TestExecutionRecord> records = List.of(
                record("a", 0, PASSED, 10),
                record("a", 1, PASSED, 20),
                record("a", 2, PASSED, 30)
        );

        TestStats stats = new StatsAggregator().aggregate(records).get(0);

        assertEquals(10, stats.minDurationMillis());
        assertEquals(20, stats.avgDurationMillis());
        assertEquals(30, stats.maxDurationMillis());
    }

    @Test
    void sortsByFlakinessRateDescending() {
        List<TestExecutionRecord> records = List.of(
                record("stable", 0, PASSED, 1),
                record("stable", 1, PASSED, 1),
                record("flaky", 0, PASSED, 1),
                record("flaky", 1, FAILED, 1)
        );

        List<TestStats> stats = new StatsAggregator().aggregate(records);

        assertEquals("flaky", stats.get(0).testId());
        assertEquals("stable", stats.get(1).testId());
    }

    private static TestExecutionRecord record(String testId, int runIndex, TestExecutionRecord.Outcome outcome, long durationMillis) {
        return new TestExecutionRecord(testId, testId, runIndex, outcome, durationMillis, null);
    }
}
