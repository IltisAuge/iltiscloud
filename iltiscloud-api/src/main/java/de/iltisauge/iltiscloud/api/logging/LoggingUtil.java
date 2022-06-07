package de.iltisauge.iltiscloud.api.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class LoggingUtil {
	
	/**
	 * Applys the Formatter to the given {@link Logger}.
	 * All other formatters will be removed from the logger.
	 */
	public static void setFormatter(Formatter formatter, Logger logger) {
		int handlerCount = 0;
		for (Handler handler : logger.getHandlers()) {
			handler.setFormatter(formatter);
			handlerCount++;
		}
		logger.setUseParentHandlers(false);
		if (handlerCount == 0) {
			final ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(formatter);
			logger.addHandler(consoleHandler);
		}
	}
}
