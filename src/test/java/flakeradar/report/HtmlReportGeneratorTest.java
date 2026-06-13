package flakeradar.report;

import flakeradar.core.TestCorrelation;
import flakeradar.core.TestStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlReportGeneratorTest {

    private static final LocalDateTime GENERATED_AT = LocalDateTime.of(2026, 6, 13, 12, 0);

    @Test
    void includesSummaryCounts() {
        List<TestStats> stats = List.of(
                stats("flaky", 0.4, 3, 2, 0),
                stats("stable", 0.0, 5, 0, 0),
                stats("alwaysFails", 0.0, 0, 5, 0)
        );

        String html = new HtmlReportGenerator().render(stats, List.of(), 5, GENERATED_AT);

        assertTrue(html.contains(">3<"), "всего тестов: 3");
        assertTrue(html.contains(">1<"), "по одному тесту в каждой категории");
    }

    @Test
    void rendersRowWithDisplayNameAndFlakinessPercentage() {
        List<TestStats> stats = List.of(stats("Flaky test #1", 0.4, 3, 2, 0));

        String html = new HtmlReportGenerator().render(stats, List.of(), 5, GENERATED_AT);

        assertTrue(html.contains("Flaky test #1"));
        assertTrue(html.contains("40.0%"));
    }

    @Test
    void includesRunCountInHeader() {
        String html = new HtmlReportGenerator().render(List.of(), List.of(), 30, GENERATED_AT);

        assertTrue(html.contains("30"));
    }

    @Test
    void rendersCorrelationsSectionWhenPresent() {
        List<TestCorrelation> correlations = List.of(new TestCorrelation("a.A#m1", "b.B#m2", 0.87));

        String html = new HtmlReportGenerator().render(List.of(), correlations, 10, GENERATED_AT);

        assertTrue(html.contains("a.A#m1"));
        assertTrue(html.contains("b.B#m2"));
        assertTrue(html.contains("0.87"));
    }

    @Test
    void omitsCorrelationsSectionWhenEmpty() {
        String html = new HtmlReportGenerator().render(List.of(), List.of(), 10, GENERATED_AT);

        assertFalse(html.contains("падают вместе"));
    }

    @Test
    void writesRenderedHtmlToFile(@TempDir Path tempDir) throws IOException {
        Path output = tempDir.resolve("flake-report/index.html");

        new HtmlReportGenerator().writeToFile("<html></html>", output);

        assertEquals("<html></html>", Files.readString(output));
    }

    private static TestStats stats(String testId, double flakinessRate, int passCount, int failCount, int abortedCount) {
        int totalRuns = passCount + failCount + abortedCount;
        return new TestStats(testId, testId, totalRuns, passCount, failCount, abortedCount, flakinessRate, 10, 11, 13);
    }
}
