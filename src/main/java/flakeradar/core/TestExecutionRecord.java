package flakeradar.core;

/**
 * Результат выполнения одного теста в одном прогоне.
 */
public record TestExecutionRecord(
        String testId,
        String displayName,
        int runIndex,
        Outcome outcome,
        long durationMillis,
        String failureMessage
) {

    public enum Outcome {
        PASSED,
        FAILED,
        ABORTED
    }
}
