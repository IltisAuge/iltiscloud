package de.iltisauge.iltiscloud.common.server;

import java.net.SocketAddress;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.server.ITemplateServer;
import de.iltisauge.iltiscloud.api.server.ServerPlatform;
import de.iltisauge.iltiscloud.api.server.ServerType;
import lombok.Getter;

@Getter
public class TemplateServer extends Server implements ITemplateServer {

	private final String templateName;
	
	public TemplateServer(UUID serverId, SocketAddress address, String name, boolean isRunning, UUID wrapperId,
			ServerType serverType, ServerPlatform serverPlatform, int maxRam, String templateName) {
		super(serverId, address, name, isRunning, wrapperId, serverType, serverPlatform, maxRam);
		this.templateName = templateName;
	}
}
