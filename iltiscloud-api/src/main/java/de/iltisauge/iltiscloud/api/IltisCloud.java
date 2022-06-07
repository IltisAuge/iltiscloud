package de.iltisauge.iltiscloud.api;

import java.util.logging.Logger;

public class IltisCloud {
	
	private static ICloudAPI ILTIS_CLOUD_API;
	private static Logger LOGGER;
	
	public static void setAPI(ICloudAPI api) {
		ILTIS_CLOUD_API = api;
	}
	
	public static ICloudAPI getAPI() {
		return ILTIS_CLOUD_API;
	}
	
	public static void setLogger(Logger logger) {
		LOGGER = logger;
	}
	
	public static Logger getLogger() {
		return LOGGER;
	}
}
