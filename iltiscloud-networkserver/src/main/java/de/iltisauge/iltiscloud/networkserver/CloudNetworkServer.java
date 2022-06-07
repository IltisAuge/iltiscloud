package de.iltisauge.iltiscloud.networkserver;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;

import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.logging.ConsoleLoggingFormatter;
import de.iltisauge.iltiscloud.api.logging.LoggingUtil;
import de.iltisauge.iltiscloud.api.utils.Util;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.server.NetworkServer;
import de.iltisauge.transport.server.ServerNetworkManager;
import de.iltisauge.transport.server.SubscriptionManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloudNetworkServer {

	private final SocketAddress socketAddress;
	
	public static void main(String[] args) {
		final File configFile = createConfigFile();
		final BasicDBObject fileObject = BasicDBObject.parse(Util.readContentFromFile(configFile));
		final BasicDBObject networkServerObject = (BasicDBObject) fileObject.get("networkServer");
		final String hostname = networkServerObject.getString("hostname");
		final Integer port = networkServerObject.getInt("port");
		final SocketAddress address = new InetSocketAddress(hostname, port);
		final CloudNetworkServer networkServer = new CloudNetworkServer(address);
		networkServer.startUp();
	}
	
	private void startUp() {
		final Logger logger = Logger.getLogger("iltiscloud");
		LoggingUtil.setFormatter(new ConsoleLoggingFormatter(), logger);
		Transport.setLogger(logger);
		logger.log(Level.INFO, "Starting NetworkServer on " + socketAddress.toString() + "...");
		final SubscriptionManager subscriptionManager = new SubscriptionManager();
		final NetworkServer networkServer = new NetworkServer(new ServerNetworkManager(subscriptionManager), subscriptionManager, socketAddress);
		Transport.setServer(networkServer);
		networkServer.initialize();
		networkServer.start(true);
	}
	
	@SuppressWarnings("unchecked")
	private static File createConfigFile() {
		final File configFile = new File("networkServer.json");
		if (configFile.exists()) {
			return configFile;
		}
		final JSONObject jsonDoc = new JSONObject();
		final JSONObject networkServerObject = new JSONObject();
		networkServerObject.put("hostname", "127.0.0.1");
		networkServerObject.put("port", 7999);
		jsonDoc.put("networkServer", networkServerObject);
		try {
			configFile.createNewFile();
		} catch (IOException exception) {
			IltisCloud.getLogger().log(Level.WARNING, "Error while creating config file:", exception);
		}
		Util.writeJson(configFile, jsonDoc);
		IltisCloud.getLogger().log(Level.INFO, "Created file 'master.json'. Please setup all data.");
		Util.waitAndExit(3000);
		return configFile;
	}
}
