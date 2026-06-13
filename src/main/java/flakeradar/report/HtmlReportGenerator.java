package flakeradar.report;

import flakeradar.core.TestCorrelation;
import flakeradar.core.TestStats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Рендерит самодостаточный HTML-отчёт по флакающим тестам — без внешних
 * CSS/JS, всё инлайново в одном файле.
 */
public class HtmlReportGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String render(List<TestStats> stats, List<TestCorrelation> correlations, int runs, LocalDateTime generatedAt) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"ru\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>FlakeRadar — отчёт</title>\n");
        html.append("<style>").append(CSS).append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<h1>FlakeRadar</h1>\n");
        html.append("<p class=\"meta\">Прогонов: ").append(runs)
                .append(" · Сформирован: ").append(generatedAt.format(TIMESTAMP_FORMAT))
                .append("</p>\n");

        appendSummary(html, stats);
        appendTestsTable(html, stats);
        appendCorrelations(html, correlations);

        html.append("</body>\n</html>\n");
        return html.toString();
    }

    public void writeToFile(String html, Path outputFile) throws IOException {
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, html);
    }

    private void appendSummary(StringBuilder html, List<TestStats> stats) {
        int total = stats.size();
        long flaky = stats.stream().filter(s -> s.flakinessRate() > 0).count();
        long stable = stats.stream().filter(s -> s.flakinessRate() == 0 && s.failCount() == 0).count();
        long alwaysFailing = stats.stream().filter(s -> s.flakinessRate() == 0 && s.passCount() == 0).count();

        html.append("<div class=\"summary\">\n");
        appendCard(html, total, "всего тестов");
        appendCard(html, flaky, "flaky");
        appendCard(html, stable, "стабильных");
        appendCard(html, alwaysFailing, "всегда падают");
        html.append("</div>\n");
    }

    private void appendCard(StringBuilder html, long value, String label) {
        html.append("<div class=\"card\"><span class=\"value\">").append(value)
                .append("</span><span class=\"label\">").append(label)
                .append("</span></div>\n");
    }

    private void appendTestsTable(StringBuilder html, List<TestStats> stats) {
        html.append("<table class=\"tests\">\n");
        html.append("<thead><tr><th>Тест</th><th>Flaky</th><th>Pass</th><th>Fail</th><th>Aborted</th>")
                .append("<th>Min</th><th>Avg</th><th>Max</th></tr></thead>\n<tbody>\n");

        for (TestStats s : stats) {
            double percent = s.flakinessRate() * 100;
            html.append("<tr>\n");
            html.append("<td>").append(escape(s.displayName())).append("</td>\n");
            html.append("<td><div class=\"bar\"><div class=\"bar-fill\" style=\"width:")
                    .append(String.format(Locale.ROOT, "%.1f", percent)).append("%\"></div>")
                    .append("<span>").append(String.format(Locale.ROOT, "%.1f%%", percent)).append("</span></div></td>\n");
            html.append("<td>").append(s.passCount()).append("</td>\n");
            html.append("<td>").append(s.failCount()).append("</td>\n");
            html.append("<td>").append(s.abortedCount()).append("</td>\n");
            html.append("<td>").append(s.minDurationMillis()).append(" ms</td>\n");
            html.append("<td>").append(s.avgDurationMillis()).append(" ms</td>\n");
            html.append("<td>").append(s.maxDurationMillis()).append(" ms</td>\n");
            html.append("</tr>\n");
        }

        html.append("</tbody>\n</table>\n");
    }

    private void appendCorrelations(StringBuilder html, List<TestCorrelation> correlations) {
        if (correlations.isEmpty()) {
            return;
        }

        html.append("<section class=\"correlations\">\n");
        html.append("<h2>Тесты, которые падают вместе</h2>\n");
        html.append("<table>\n<thead><tr><th>Тест A</th><th>Тест B</th><th>r</th></tr></thead>\n<tbody>\n");
        for (TestCorrelation c : correlations) {
            html.append("<tr><td>").append(escape(c.testIdA())).append("</td><td>")
                    .append(escape(c.testIdB())).append("</td><td>")
                    .append(String.format(Locale.ROOT, "%.2f", c.correlation())).append("</td></tr>\n");
        }
        html.append("</tbody>\n</table>\n</section>\n");
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final String CSS = """
            body { font-family: -apple-system, Segoe UI, Arial, sans-serif; margin: 24px; color: #1b1f23; }
            h1 { margin-bottom: 4px; }
            .meta { color: #6a737d; margin-top: 0; }
            .summary { display: flex; gap: 16px; margin: 16px 0 24px; }
            .card { background: #f6f8fa; border: 1px solid #e1e4e8; border-radius: 6px; padding: 12px 20px; text-align: center; }
            .card .value { display: block; font-size: 28px; font-weight: 600; }
            .card .label { color: #6a737d; font-size: 13px; }
            table { border-collapse: collapse; width: 100%; margin-bottom: 24px; }
            th, td { border: 1px solid #e1e4e8; padding: 6px 10px; text-align: left; font-size: 14px; }
            th { background: #f6f8fa; }
            .bar { position: relative; background: #eee; border-radius: 4px; min-width: 120px; height: 18px; }
            .bar-fill { position: absolute; top: 0; left: 0; height: 100%; background: #e36209; border-radius: 4px; }
            .bar span { position: relative; padding-left: 6px; line-height: 18px; }
            """;
}
