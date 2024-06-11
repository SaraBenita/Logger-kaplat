package com.javaguides.springboot.skills.Controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerLogger {
    public static final Logger requestLogger = LogManager.getLogger("request-logger");
    public static final Logger booksLogger = LogManager.getLogger("books-logger");
    private static int requestCounter = 0;

    public static synchronized int incrementRequestCounter() {
        return ++requestCounter;
    }

    public static String getLogLevel(String loggerName) {
        Logger logger = getLogger(loggerName);
        if (logger != null) {
            return logger.getLevel().toString();
        } else {
            throw new IllegalArgumentException("Invalid logger name");
        }
    }

    public static synchronized void setLogLevel(String loggerName, String loggerLevel) {
        Logger logger = getLogger(loggerName);
        if (logger != null) {
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration config = ctx.getConfiguration();
            config.getLoggerConfig(logger.getName()).setLevel(Level.toLevel(loggerLevel.toUpperCase()));
            ctx.updateLoggers();
        } else {
            throw new IllegalArgumentException("Invalid logger name");
        }
    }

    private static Logger getLogger(String loggerName) {
        if ("request-logger".equals(loggerName)) {
            return requestLogger;
        } else if ("books-logger".equals(loggerName)) {
            return booksLogger;
        } else {
            return null;
        }
    }
}
