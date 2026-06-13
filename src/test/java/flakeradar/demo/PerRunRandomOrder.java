package flakeradar.demo;

import org.junit.jupiter.api.MethodDescriptor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrdererContext;

import java.util.Collections;

/**
 * Встроенный MethodOrderer.Random фиксирует seed один раз на JVM, поэтому
 * при многократных launcher.execute() в одном процессе порядок всегда
 * получается одинаковым. Здесь перемешиваем заново при каждом вызове, чтобы
 * SharedCounterOrderTest реально менял порядок от прогона к прогону.
 */
public class PerRunRandomOrder implements MethodOrderer {

    @Override
    public void orderMethods(MethodOrdererContext context) {
        Collections.shuffle(context.getMethodDescriptors(), new java.util.Random());
    }
}
