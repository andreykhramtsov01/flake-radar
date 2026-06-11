package flakeradar.core;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * Подвешивается на демо-тесты через @ExtendWith и пишет результат каждого
 * запуска (исход + время выполнения) в {@link ResultsCollector}.
 */
public class FlakinessExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, TestWatcher {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(FlakinessExtension.class);
    private static final String START_NANOS = "startNanos";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        context.getStore(NAMESPACE).put(START_NANOS, System.nanoTime());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        // длительность считаем здесь, а исход — в testSuccessful/testFailed/testAborted
        long startNanos = context.getStore(NAMESPACE).get(START_NANOS, Long.class);
        long durationMillis = (System.nanoTime() - startNanos) / 1_000_000;
        context.getStore(NAMESPACE).put("durationMillis", durationMillis);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        record(context, TestExecutionRecord.Outcome.PASSED, null);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        record(context, TestExecutionRecord.Outcome.FAILED, describe(cause));
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        record(context, TestExecutionRecord.Outcome.ABORTED, describe(cause));
    }

    private void record(ExtensionContext context, TestExecutionRecord.Outcome outcome, String failureMessage) {
        Long durationMillis = context.getStore(NAMESPACE).get("durationMillis", Long.class);
        ResultsCollector collector = ResultsCollector.getInstance();

        collector.record(new TestExecutionRecord(
                testId(context),
                context.getDisplayName(),
                collector.currentRun(),
                outcome,
                durationMillis == null ? 0L : durationMillis,
                failureMessage
        ));
    }

    private String testId(ExtensionContext context) {
        return context.getRequiredTestClass().getName() + "#" + context.getRequiredTestMethod().getName();
    }

    private String describe(Throwable cause) {
        if (cause == null) {
            return null;
        }
        String message = cause.getMessage();
        return message == null ? cause.getClass().getSimpleName() : message;
    }
}
