package de.iltisauge.iltiscloud.api;

import java.util.Date;

public interface Startable {
	
	/**
	 * Starts up the Startable.
	 */
	void startUp();
	
	/**
	 * Shuts down the Startable.
	 */
	void shutDown();
	
	/**
	 * Kills the process of the Startable.
	 */
	void kill();
	
	/**
	 * 
	 * @return {@code true}, if the Startable is currently running, otherwise {@code false}.
	 */
	boolean isRunning();
	
	/**
	 * Sets the running state of the Startable.
	 */
	void setRunning(boolean value);
	
	/**
	 * 
	 * @return the {@link Date} when the Startable latestly started up.
	 */
	Date getStartedUpAt();
	
	/**
	 * 
	 * Sets the latestly start up date of the Startable.
	 */
	void setStartedUpAt(Date date);

	/**
	 * 
	 * @return the current uptime of the Startable in milliseconds.
	 */
	default long getRunningTime() {
		return System.currentTimeMillis() - getStartedUpAt().getTime();
	}
	
	public enum Type {
		
		MASTER,
		WRAPPER,
		SERVER;

	}
	
	public enum Action {
		
		START_UP,
		SHUT_DOWN,
		KILL;

	}
}
