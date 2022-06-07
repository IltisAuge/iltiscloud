package de.iltisauge.iltiscloud.common.server;

import java.net.SocketAddress;
import java.util.Date;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.server.IServer;
import de.iltisauge.iltiscloud.api.server.ServerPlatform;
import de.iltisauge.iltiscloud.api.server.ServerType;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket.ConfirmationType;
import de.iltisauge.iltiscloud.api.wrapper.IWrapper;
import de.iltisauge.iltiscloud.common.server.packets.ExecuteCommandPacket;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Server implements IServer {

	private final UUID serverId;
	private final SocketAddress address;
	private final String name;
	@Setter
	private boolean isRunning;
	private final UUID wrapperId;
	private final ServerType serverType;
	private final ServerPlatform serverPlatform;
	private final int maxRam;
	@Setter
	private Date startedUpAt;

	public Server(UUID serverId, SocketAddress address, String name, boolean isRunning, UUID wrapperId, ServerType serverType,
			ServerPlatform serverPlatform, int maxRam) {
		this.serverId = serverId;
		this.address = address;
		this.name = name;
		this.isRunning = isRunning;
		this.wrapperId = wrapperId;
		this.serverType = serverType;
		this.serverPlatform = serverPlatform;
		this.maxRam = maxRam;
	}

	@Override
	public IWrapper getWrapper() {
		return IltisCloud.getAPI().getWrapperManager().getWrapper(wrapperId);
	}
	
	@Override
	public void startUp() {
		sendAction(Action.START_UP);
	}

	@Override
	public void shutDown() {
		sendAction(Action.SHUT_DOWN);
	}

	@Override
	public void kill() {
		sendAction(Action.KILL);
	}
	
	@Override
	public void executeCommand(String command) {
		new ExecuteCommandPacket(serverId, command).send("execute-server-command");
	}
	
	private void sendAction(Action action) {
		new UpdateStartablePacket(Type.SERVER, serverId, action, ConfirmationType.UNCONFIRMED).send();
	}
}
