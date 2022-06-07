package de.iltisauge.iltiscloud.api.server;

public interface ITemplateServer extends IServer {
	
	/**
	 * 
	 * @return the name of the template the server is running.
	 */
	String getTemplateName();

}
