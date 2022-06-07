package de.iltisauge.iltiscloud.common.wrapper;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.server.IServer;
import de.iltisauge.iltiscloud.api.server.IServerManager;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket.ConfirmationType;
import de.iltisauge.iltiscloud.api.wrapper.IWrapper;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Wrapper implements IWrapper {
	
	private final UUID wrapperId;
	private final SocketAddress address;
	private final String name;
	@Setter
	private boolean isRunning;
	private final Integer maxRam;
	private final List<UUID> serverList = new ArrayList<UUID>();
	@Setter
	private Date startedUpAt;

	public Wrapper(UUID wrapperId, SocketAddress address, String name, boolean isRunning, Integer maxRam) {
		this.wrapperId = wrapperId;
		this.address = address;
		this.name = name;
		this.isRunning = isRunning;
		this.maxRam = maxRam;
	}
	
	@Override
	public List<IServer> getServers() {
		final IServerManager serverManager = IltisCloud.getAPI().getServerManager();
		return serverList.stream().map(serverId -> serverManager.getServer(serverId)).collect(Collectors.toList());
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
	
	private final void sendAction(Action action) {
		new UpdateStartablePacket(Type.WRAPPER, wrapperId, action, ConfirmationType.UNCONFIRMED).send();
	}
}
