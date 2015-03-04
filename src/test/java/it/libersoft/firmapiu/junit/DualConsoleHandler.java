/**
 * 
 */
package it.libersoft.firmapiu.junit;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Handler per logging
 * 
 * @author dellanna
 *
 */
public class DualConsoleHandler extends StreamHandler {

    private final ConsoleHandler stderrHandler = new ConsoleHandler();

    public DualConsoleHandler() {
        super(System.out, new SimpleFormatter());
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().intValue() <= Level.INFO.intValue()) {
            super.publish(record);
            super.flush();
        } else {
            stderrHandler.publish(record);
            stderrHandler.flush();
        }
    }
}
