package flakeradar.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ищет пары тестов, которые падают в одних и тех же прогонах. Для каждого
 * теста строится бинарный вектор длины N (1 = упал, 0 = иначе), и между
 * векторами считается корреляция Пирсона.
 */
public class CorrelationAnalyzer {

    public List<TestCorrelation> findCorrelatedFailures(List<TestExecutionRecord> records, double threshold) {
        int totalRuns = records.stream().mapToInt(TestExecutionRecord::runIndex).max().orElse(-1) + 1;
        if (totalRuns == 0) {
            return List.of();
        }

        Map<String, double[]> vectors = toFailureVectors(records, totalRuns);

        List<String> testIds = new ArrayList<>(vectors.keySet());
        List<TestCorrelation> result = new ArrayList<>();

        for (int i = 0; i < testIds.size(); i++) {
            double[] vectorA = vectors.get(testIds.get(i));
            if (variance(vectorA) == 0) {
                continue;
            }
            for (int j = i + 1; j < testIds.size(); j++) {
                double[] vectorB = vectors.get(testIds.get(j));
                if (variance(vectorB) == 0) {
                    continue;
                }
                double r = pearson(vectorA, vectorB);
                if (Math.abs(r) >= threshold) {
                    result.add(new TestCorrelation(testIds.get(i), testIds.get(j), r));
                }
            }
        }

        result.sort(Comparator.comparingDouble((TestCorrelation c) -> Math.abs(c.correlation())).reversed());
        return result;
    }

    private Map<String, double[]> toFailureVectors(List<TestExecutionRecord> records, int totalRuns) {
        Map<String, double[]> vectors = new LinkedHashMap<>();
        for (TestExecutionRecord record : records) {
            double[] vector = vectors.computeIfAbsent(record.testId(), k -> new double[totalRuns]);
            vector[record.runIndex()] = record.outcome() == TestExecutionRecord.Outcome.FAILED ? 1.0 : 0.0;
        }
        return vectors;
    }

    private double variance(double[] values) {
        double mean = Arrays.stream(values).average().orElse(0);
        return Arrays.stream(values).map(v -> (v - mean) * (v - mean)).sum();
    }

    private double pearson(double[] x, double[] y) {
        double meanX = Arrays.stream(x).average().orElse(0);
        double meanY = Arrays.stream(y).average().orElse(0);

        double covariance = 0;
        double varX = 0;
        double varY = 0;
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            covariance += dx * dy;
            varX += dx * dx;
            varY += dy * dy;
        }
        return covariance / Math.sqrt(varX * varY);
    }
}
