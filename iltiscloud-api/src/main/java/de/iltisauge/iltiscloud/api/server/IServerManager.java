package de.iltisauge.iltiscloud.api.server;

import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.IManager;

public interface IServerManager extends IManager {
	
	/**
	 * 
	 * @param serverId
	 * @return an {@link IServer} if the server exists, otherwise {@code null}.
	 */
	IServer getServer(UUID serverId);
	
	/**
	 * 
	 * @return a {@link List} containing all existing servers.
	 */
	List<IServer> getServers();

	/**
	 * Creates a new {@link IServer}.
	 * @param serverId
	 * @param address
	 * @param name
	 * @param isRunning
	 * @param serverType
	 * @param serverPlatform
	 * @param maxRam
	 * @param wrapperId
	 */
	void createServer(UUID serverId, SocketAddress address, String name, boolean isRunning, ServerType serverType,
			ServerPlatform serverPlatform, int maxRam, UUID wrapperId);
	
	/**
	 * Deletes the server with the given {@link UUID}.
	 * @param serverId
	 */
	void deleteServer(UUID serverId);

}
