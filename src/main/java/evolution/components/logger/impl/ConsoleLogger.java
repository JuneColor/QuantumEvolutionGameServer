package evolution.components.logger.impl;

import evolution.components.logger.EvolutionLogger;

import javax.inject.Singleton;

@Singleton
public class ConsoleLogger implements EvolutionLogger {
    @Override
    public void info(String message) {
        System.out.println("INFO:" + message);
    }

    @Override
    public void warn(String message) {
        System.out.println("WARN:" + message);
    }

    @Override
    public void error(String message) {
        System.out.println("ERROR:" + message);
    }

    @Override
    public void fatal(String message) {
        System.out.println("FATAL:" + message);
    }
}