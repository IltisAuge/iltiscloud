package de.iltisauge.iltiscloud.api.server;

import java.net.SocketAddress;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.Startable;
import de.iltisauge.iltiscloud.api.wrapper.IWrapper;

/**
 * 
 * @author Daniel Ziegler
 *
 */
public interface IServer extends Startable {
	
	/**
	 * 
	 * @return the {@link UUID} of the server.
	 */
	UUID getServerId();
	
	/**
	 * 
	 * @return the {@link SocketAddress} of the server.
	 */
	SocketAddress getAddress();
	
	/**
	 * 
	 * @return the name of the server.
	 */
	String getName();
	
	/**
	 * 
	 * @return the {@link IWrapper} the server is located at.
	 */
	IWrapper getWrapper();
	
	/**
	 * 
	 * @return the {@link ServerType} of the server. 
	 */
	ServerType getServerType();

	/**
	 * 
	 * @return the {@link ServerPlatform} of the server. 
	 */
	ServerPlatform getServerPlatform();
	
	/**
	 * 
	 * @return the maximum of ram allocated to the server.
	 */
	int getMaxRam();
	
	/**
	 * Sends a packet to execute the given command.
	 * @param command
	 */
	void executeCommand(String command);
}
