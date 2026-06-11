package flakeradar.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static flakeradar.core.TestExecutionRecord.Outcome.FAILED;
import static flakeradar.core.TestExecutionRecord.Outcome.PASSED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorrelationAnalyzerTest {

    @Test
    void findsTestsThatFailInTheSameRuns() {
        List<TestExecutionRecord> records = List.of(
                record("a", 0, FAILED), record("b", 0, FAILED),
                record("a", 1, PASSED), record("b", 1, PASSED),
                record("a", 2, FAILED), record("b", 2, FAILED),
                record("a", 3, PASSED), record("b", 3, PASSED)
        );

        List<TestCorrelation> result = new CorrelationAnalyzer().findCorrelatedFailures(records, 0.5);

        assertEquals(1, result.size());
        assertEquals(1.0, result.get(0).correlation(), 1e-9);
    }

    @Test
    void ignoresTestsThatFailIndependently() {
        // a падает на чётных прогонах, b -- на первых четырёх: пересечений ровно
        // столько, сколько ожидалось бы при случайном совпадении -> r == 0
        List<TestExecutionRecord> records = List.of(
                record("a", 0, FAILED), record("b", 0, FAILED),
                record("a", 1, PASSED), record("b", 1, FAILED),
                record("a", 2, FAILED), record("b", 2, PASSED),
                record("a", 3, PASSED), record("b", 3, PASSED),
                record("a", 4, FAILED), record("b", 4, FAILED),
                record("a", 5, PASSED), record("b", 5, FAILED),
                record("a", 6, FAILED), record("b", 6, PASSED),
                record("a", 7, PASSED), record("b", 7, PASSED)
        );

        List<TestCorrelation> result = new CorrelationAnalyzer().findCorrelatedFailures(records, 0.5);

        assertTrue(result.isEmpty());
    }

    @Test
    void skipsTestsWithoutVarianceToAvoidDivisionByZero() {
        List<TestExecutionRecord> records = List.of(
                record("alwaysFails", 0, FAILED), record("b", 0, FAILED),
                record("alwaysFails", 1, FAILED), record("b", 1, PASSED),
                record("alwaysFails", 2, FAILED), record("b", 2, FAILED)
        );

        List<TestCorrelation> result = new CorrelationAnalyzer().findCorrelatedFailures(records, 0.1);

        assertTrue(result.isEmpty());
    }

    private static TestExecutionRecord record(String testId, int runIndex, TestExecutionRecord.Outcome outcome) {
        return new TestExecutionRecord(testId, testId, runIndex, outcome, 1, null);
    }
}
