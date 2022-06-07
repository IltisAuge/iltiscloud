package de.iltisauge.iltiscloud.api;

public interface IManager {
	
	default void initialize() { }
	
	default void destroy() { }
	
	default void clearCache() { }

}
