package de.iltisauge.iltiscloud.api.wrapper;

import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.Startable;
import de.iltisauge.iltiscloud.api.server.IServer;

/**
 * A wrapper in this cloudsystem is a node between the cloud master and the game servers.
 * It is possible to run multiple wrappers from different maschines.
 * The cloud master can communicate with all wrappers to start and stop servers on specific wrappers.
 * @author Daniel Ziegler
 *
 */
public interface IWrapper extends Startable {
	
	/**
	 * 
	 * @return the {@link UUID} of the wrapper.
	 */
	UUID getWrapperId();

	/**
	 * 
	 * @return the {@link SocketAddress} of the wrapper.
	 */
	SocketAddress getAddress();
	
	/**
	 * 
	 * @return the name of the wrapper.
	 */
	String getName();
	
	/**
	 * 
	 * @return the maximum of ram allocated to the wrapper.
	 */
	Integer getMaxRam();
	
	/**
	 * 
	 * @return a {@link List} of all {@link IServer}s that are located on this wrapper. 
	 */
	List<IServer> getServers();

}
