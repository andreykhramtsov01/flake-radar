package flakeradar;

import flakeradar.core.CorrelationAnalyzer;
import flakeradar.core.ResultsCollector;
import flakeradar.core.RunCountParser;
import flakeradar.core.StatsAggregator;
import flakeradar.core.TestCorrelation;
import flakeradar.core.TestExecutionRecord;
import flakeradar.core.TestStats;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.List;
import java.util.Locale;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

/**
 * Точка входа: гоняет демо-сьют flakeradar.demo N раз подряд и печатает
 * сводку по флакающим тестам и парам тестов, которые падают вместе.
 */
public class FlakyTestRunner {

    private static final double CORRELATION_THRESHOLD = 0.5;

    public static void main(String[] args) {
        int runs;
        try {
            runs = RunCountParser.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        ResultsCollector collector = ResultsCollector.getInstance();
        collector.clear();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("flakeradar.demo"))
                .build();
        Launcher launcher = LauncherFactory.create();

        for (int run = 1; run <= runs; run++) {
            collector.startRun(run);
            launcher.execute(request);
            System.out.printf(Locale.ROOT, "Прогон %d/%d завершён%n", run, runs);
        }

        List<TestExecutionRecord> records = collector.getRecords();
        if (records.isEmpty()) {
            System.out.println("Тесты не найдены, отчёт не построен.");
            return;
        }

        List<TestStats> stats = new StatsAggregator().aggregate(records);
        List<TestCorrelation> correlations = new CorrelationAnalyzer().findCorrelatedFailures(records, CORRELATION_THRESHOLD);

        printSummary(stats, correlations);
    }

    private static void printSummary(List<TestStats> stats, List<TestCorrelation> correlations) {
        System.out.println();
        System.out.println("=== Статистика по тестам ===");
        for (TestStats s : stats) {
            System.out.printf(Locale.ROOT, "%-60s flaky=%5.1f%%  pass=%d fail=%d aborted=%d  avg=%dms (min=%d, max=%d)%n",
                    s.displayName(),
                    s.flakinessRate() * 100,
                    s.passCount(), s.failCount(), s.abortedCount(),
                    s.avgDurationMillis(), s.minDurationMillis(), s.maxDurationMillis());
        }

        if (correlations.isEmpty()) {
            return;
        }

        System.out.println();
        System.out.println("=== Тесты, которые падают вместе ===");
        for (TestCorrelation c : correlations) {
            System.out.printf(Locale.ROOT, "%s <-> %s  r=%.2f%n", c.testIdA(), c.testIdB(), c.correlation());
        }
    }
}
