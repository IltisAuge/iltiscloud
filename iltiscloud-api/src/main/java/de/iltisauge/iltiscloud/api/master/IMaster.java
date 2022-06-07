package de.iltisauge.iltiscloud.api.master;

import java.net.SocketAddress;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.Startable;

public interface IMaster extends Startable {
	
	/**
	 * 
	 * @return the {@link UUID} of the master.
	 */
	UUID getMasterId();

	/**
	 * 
	 * @return the {@link SocketAddress} of the master.
	 */
	SocketAddress getAddress();
	
	/**
	 * 
	 * @return the maximum of ram allocated to the master.
	 */
	int getMaxRam();

}
