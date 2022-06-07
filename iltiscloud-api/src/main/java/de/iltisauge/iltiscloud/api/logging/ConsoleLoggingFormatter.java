package de.iltisauge.iltiscloud.api.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * The format will be like:
 * 
 * <pre>
 * This is an error:
 * Exception in thread "main" java.lang.NullPointerException
 * ...
 * </pre>
 * 
 * @author Daniel Ziegler
 */
public class ConsoleLoggingFormatter extends Formatter {
	
	@Override
	public String format(LogRecord record) {
		return "[" + Thread.currentThread().getName() + "] " + formatMessage(record) + (record.getThrown() != null ? "\n" + ExceptionUtils.getStackTrace(record.getThrown()) : "") + "\n";
	}
}
