package de.iltisauge.iltiscloud.wrapper.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.IManager;
import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.server.ServerPlatform;
import de.iltisauge.iltiscloud.api.server.ServerType;
import de.iltisauge.iltiscloud.common.server.packets.ExecuteCommandPacket;
import de.iltisauge.iltiscloud.wrapper.CloudWrapper;
import de.iltisauge.java.js.communication.java.JavaServer;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.network.IMessageEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerManager implements IManager {
	
	// Other
	private final CloudWrapper wrapper;
	
	// Caching
	private final Map<UUID, ServerProcess> runningServers = new HashMap<UUID, ServerProcess>();
	
	@Override
	public void initialize() {
		Transport.getNetworkManager().registerEvent(ExecuteCommandPacket.class, new IMessageEvent<ExecuteCommandPacket>() {
			
			@Override
			public void onReceived(ExecuteCommandPacket message) {
				final UUID serverId = message.getServerId();
				if (!runningServers.containsKey(serverId)) {
					return;
				}
				final ServerProcess process = runningServers.get(serverId);
				process.log(message.getCommand());
				process.write(message.getCommand());
			}
		});
	}
	
	public void startServer(File file) {
		final String name = file.getName();
		System.out.println("Starting server " + name);
		final boolean isTemplateServer = file.getParentFile() != null && file.getParentFile().getName().equals("templates");
		System.out.println("isTemplateServer=" + isTemplateServer);
		final ServerType serverType = isTemplateServer ? ServerType.TEMPLATE : ServerType.STATIC;
		final File directory = new File(isTemplateServer ? "templates" : "static", name);
		final File startFile = new File(directory, "server.jar");
		final UUID serverId = UUID.randomUUID();
		final File serverProperties = new File(directory, "server.properties");
		BufferedReader is = null;
		try {
			is = new BufferedReader(new FileReader(serverProperties));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		final InetSocketAddress address = new InetSocketAddress("192.168.2.105", Integer.parseInt(props.getProperty("server-port")));
		IltisCloud.getAPI().getServerManager().createServer(serverId, address, name, true, serverType, ServerPlatform.SPIGOT, 1024, wrapper.getWrapperId());	
		final JavaServer javaServer = new JavaServer(new InetSocketAddress(address.getPort() + 1));
		javaServer.start();
		final ServerProcess serverProcess = new ServerProcess(directory, startFile, javaServer) {
			
			@Override
			public void onProcessStopped() {
				runningServers.remove(serverId);
				IltisCloud.getAPI().getServerManager().deleteServer(serverId);
			}
		};
		runningServers.put(serverId, serverProcess);
		serverProcess.start();
	}
	
//	public void startServer(String id) {
//		System.out.println("Starting server " + id);
//		startServer(IltisCloud.getAPI().getServerManager().getServerById(id));
//	}
	
	public void stopServer(UUID serverId) {
		System.out.println("Stopping server " + serverId);
		try {
			runningServers.get(serverId).getWriter().write("stop");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		runningServers.remove(serverId);
		IltisCloud.getAPI().getServerManager().deleteServer(serverId);
	}
	
	public void killServer(UUID serverId) {
		System.out.println("Killing server " + serverId);
		runningServers.get(serverId).getProcess().destroyForcibly();
		runningServers.remove(serverId);
	}
	
	public List<ServerProcess> getRunningProcesses() {
		return new ArrayList<ServerProcess>(runningServers.values());
	}
	
	public List<UUID> getRunningServers() {
		return new ArrayList<UUID>(runningServers.keySet());
	}
}
