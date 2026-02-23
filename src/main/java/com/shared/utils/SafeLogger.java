package com.shared.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SafeLogger {
    
    private final Logger logger;
    
    public SafeLogger(Logger logger) {
        this.logger = logger;
    }
    
    public void info(String format, Object... args) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info(String.format(format, args));
        }
    }
    
    public void severe(String format, Object... args) {
        if (logger.isLoggable(Level.SEVERE)) {
            logger.severe(String.format(format, args));
        }
    }
    
    public void warning(String format, Object... args) {
        if (logger.isLoggable(Level.WARNING)) {
            logger.warning(String.format(format, args));
        }
    }
    
    public void fine(String format, Object... args) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format(format, args));
        }
    }
    
    public void log(Level level, String format, Object... args) {
        if (logger.isLoggable(level)) {
            logger.log(level, String.format(format, args));
        }
    }
    
    public void log(Level level, String message, Throwable thrown) {
        if (logger.isLoggable(level)) {
            logger.log(level, message, thrown);
        }
    }
}