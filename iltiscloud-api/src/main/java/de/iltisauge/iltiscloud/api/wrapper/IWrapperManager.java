package de.iltisauge.iltiscloud.api.wrapper;

import java.net.SocketAddress;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.IManager;

public interface IWrapperManager extends IManager {

	/**
	 * 
	 * @param wrapperId
	 * @return an {@link IWrapper} if the wrapper exists, otherwise {@code null}.
	 */
	IWrapper getWrapper(UUID wrapperId);
	
	/**
	 * Creates a new {@link IWrapper}.
	 * @param address
	 * @param maxRam
	 */
	void createWrapper(SocketAddress address, int maxRam);

}
