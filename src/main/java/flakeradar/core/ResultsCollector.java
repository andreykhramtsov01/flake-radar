package flakeradar.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Общее хранилище результатов на всё время работы FlakyTestRunner.
 * Раннер выставляет номер текущего прогона перед каждым запуском,
 * а FlakinessExtension дописывает сюда записи по ходу выполнения тестов.
 */
public final class ResultsCollector {

    private static final ResultsCollector INSTANCE = new ResultsCollector();

    private final List<TestExecutionRecord> records = new CopyOnWriteArrayList<>();
    private volatile int currentRun = 0;

    private ResultsCollector() {
    }

    public static ResultsCollector getInstance() {
        return INSTANCE;
    }

    public void startRun(int runIndex) {
        this.currentRun = runIndex;
    }

    public int currentRun() {
        return currentRun;
    }

    public void record(TestExecutionRecord record) {
        records.add(record);
    }

    public List<TestExecutionRecord> getRecords() {
        return List.copyOf(records);
    }

    /** Для юнит-тестов, чтобы не тащить состояние между прогонами. */
    public void clear() {
        records.clear();
        currentRun = 0;
    }
}
