package evolution.components.logger;

import com.google.inject.ImplementedBy;
import evolution.components.logger.impl.ConsoleLogger;

@ImplementedBy(ConsoleLogger.class)
public interface EvolutionLogger {
    void info(String message);
    void warn(String message);
    void error(String message);
    void fatal(String message);
}
