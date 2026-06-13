package flakeradar.core;

/**
 * Разбирает количество прогонов из аргумента командной строки или
 * системного свойства {@code runs}. По умолчанию — 20.
 */
public final class RunCountParser {

    private static final int DEFAULT_RUNS = 20;

    private RunCountParser() {
    }

    public static int parse(String[] args) {
        String raw = args.length > 0 ? args[0] : System.getProperty("runs");
        if (raw == null) {
            return DEFAULT_RUNS;
        }

        int runs;
        try {
            runs = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Число прогонов должно быть целым числом, получено: " + raw);
        }

        if (runs <= 0) {
            throw new IllegalArgumentException("Число прогонов должно быть положительным, получено: " + runs);
        }

        return runs;
    }
}
