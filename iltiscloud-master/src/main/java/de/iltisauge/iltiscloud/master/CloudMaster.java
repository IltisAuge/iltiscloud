package de.iltisauge.iltiscloud.master;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import de.iltisauge.databaseapi.Credential;
import de.iltisauge.databaseapi.databases.MongoDatabase;
import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.logging.ConsoleLoggingFormatter;
import de.iltisauge.iltiscloud.api.logging.LoggingUtil;
import de.iltisauge.iltiscloud.api.master.IMaster;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket;
import de.iltisauge.iltiscloud.api.utils.SocketAddressConverter;
import de.iltisauge.iltiscloud.api.utils.Util;
import de.iltisauge.iltiscloud.common.CommonCloudAPI;
import de.iltisauge.iltiscloud.common.clouduser.CloudUserManager;
import de.iltisauge.iltiscloud.common.master.MasterManager;
import de.iltisauge.iltiscloud.common.permission.PermissionManager;
import de.iltisauge.iltiscloud.common.server.ServerManager;
import de.iltisauge.iltiscloud.common.server.packets.UpdateServerPacket;
import de.iltisauge.iltiscloud.common.wrapper.WrapperManager;
import de.iltisauge.iltiscloud.master.httpserver.CloudHttpServer;
import de.iltisauge.java.js.communication.java.JavaServer;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.client.ClientNetworkManager;
import de.iltisauge.transport.client.NetworkClient;
import de.iltisauge.transport.network.IMessageEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CloudMaster implements IMaster {

	private final UUID masterId;
	private final SocketAddress address;
	private final SocketAddress networkServerAddress;
	private final InetSocketAddress httpServerAddress;
	private boolean isRunning;
	private final int maxRam;
	@Setter
	private Date startedUpAt;
	public static JavaServer PANEL_COMMUNICATION_SERVER;

	public CloudMaster(UUID masterId, SocketAddress address, SocketAddress networkServerAddress, InetSocketAddress httpServerAddress, boolean isRunning, int maxRam) {
		this.masterId = masterId;
		this.address = address;
		this.networkServerAddress = networkServerAddress;
		this.httpServerAddress = httpServerAddress;
		this.isRunning = isRunning;
		this.maxRam = maxRam;
	}
	
	public static void main(String[] args) {
		final Logger logger = Logger.getLogger("iltiscloud");
		LoggingUtil.setFormatter(new ConsoleLoggingFormatter(), logger);
		IltisCloud.setLogger(logger);
		Transport.setLogger(logger);
		
		// Read master.json
		final File configFile = createConfigFile();
		final BasicDBObject fileObject = BasicDBObject.parse(Util.readContentFromFile(configFile));
		final BasicDBObject masterObject = (BasicDBObject) fileObject.get("master");
		final UUID masterId = UUID.fromString(masterObject.getString("masterId"));
		final String masterHostname = masterObject.getString("hostname");
		final Integer masterPort = masterObject.getInt("port");
		final InetSocketAddress masterAddress = new InetSocketAddress(masterHostname, masterPort);
		final BasicDBObject networkServerObject = (BasicDBObject) fileObject.get("networkServer");
		final String networkServerHostname = networkServerObject.getString("hostname");
		final Integer networkServerPort = networkServerObject.getInt("port");
		final InetSocketAddress networkServerAddress = new InetSocketAddress(networkServerHostname, networkServerPort);
		final BasicDBObject httpServerObject = (BasicDBObject) fileObject.get("httpServer");
		final String httpServerHostname = httpServerObject.getString("hostname");
		final Integer httpServerPort = httpServerObject.getInt("port");
		final InetSocketAddress httpServerAddress = new InetSocketAddress(httpServerHostname, httpServerPort);
		final BasicDBObject panelCommunicationServerObject = (BasicDBObject) fileObject.get("panelCommunicationServer");
		final String panelCommunicationServerHostname = panelCommunicationServerObject.getString("hostname");
		final Integer panelCommunicationServerPort = panelCommunicationServerObject.getInt("port");
		final InetSocketAddress panelCommunicationServerAddress = new InetSocketAddress(panelCommunicationServerHostname, panelCommunicationServerPort);
		
		logger.log(Level.INFO, "Starting panel communication server on address " + panelCommunicationServerAddress.toString() + "...");
		PANEL_COMMUNICATION_SERVER = new JavaServer(panelCommunicationServerAddress);
		PANEL_COMMUNICATION_SERVER.start();

		final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		final int maxRam = (int) (memoryBean.getHeapMemoryUsage().getMax() / 1024L / 1024L);
		final CloudMaster cloudMaster = new CloudMaster(masterId, masterAddress, networkServerAddress, httpServerAddress, false, maxRam);
		cloudMaster.startUp();
	}

	@Override
	public void startUp() {
		final Logger logger = IltisCloud.getLogger();
		logger.log(Level.INFO, "Starting up CloudMaster on " + address.toString() + "...");
		
		final String mySQLDriverClassName = "com.mysql.cj.jdbc.Driver";
		logger.log(Level.INFO, "Loading MySQL driver class " + mySQLDriverClassName + "...");
		try {
			Class.forName(mySQLDriverClassName);
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				logger.log(Level.INFO, "Instancing HttpServer...");
				final CloudHttpServer httpServer = new CloudHttpServer(httpServerAddress);
				httpServer.startUp();
			}
		}).run();

		logger.log(Level.INFO, "Instancing NetworkManager...");
		final ClientNetworkManager networkManager = new ClientNetworkManager();
		final NetworkClient networkClient = new NetworkClient(networkManager, networkServerAddress);
		networkClient.initialize();
		networkClient.start(true);
		Transport.setClient(networkClient);
		
		logger.log(Level.INFO, "Instancing CommonCloudAPI...");
		final MongoDatabase database = new MongoDatabase(new Credential("localhost", 27017, "admin", "admin", "admin"));
		final CommonCloudAPI cloudAPI = new CommonCloudAPI(new MasterManager(masterId, database), new WrapperManager(), new ServerManager(), new CloudUserManager(), new PermissionManager(), networkClient, database);
		IltisCloud.setAPI(cloudAPI);
		cloudAPI.initialize();

		networkManager.registerEvent(UpdateServerPacket.class, new IMessageEvent<UpdateServerPacket>() {
			
			@Override
			public void onReceived(UpdateServerPacket message) {
				final BasicDBObject obj = new BasicDBObject();
				obj.put("serverId", message.getServerId().toString());
				obj.put("action", message.getAction().name());
				obj.put("updatedField", message.getUpdatedField());
				System.out.println("Broadcast " + obj + " to panel");
				PANEL_COMMUNICATION_SERVER.broadcast(obj);
			};
		});
		networkManager.registerEvent(UpdateStartablePacket.class, new IMessageEvent<UpdateStartablePacket>() {
			
			@Override
			public void onReceived(UpdateStartablePacket message) {
				System.out.println("Incomming=" + message);
				if (!message.getStartableType().equals(Type.MASTER)) {
					return;
				}
				final Action action = message.getStartableAction();
				System.out.println("Action=" + action);
				if (action.equals(Action.START_UP)) {
					startUp();
				} else if (action.equals(Action.SHUT_DOWN)) {
					shutDown();
				} else if (action.equals(Action.KILL)) {
					kill();
				}
			}
		});
		
		isRunning = true;
		startedUpAt = new Date();
		logger.log(Level.INFO, "Updating data in database...");
		updateMasterDataInDB();
	}
	
	private void updateMasterDataInDB() {
		final MasterManager masterManager = (MasterManager) IltisCloud.getAPI().getMasterManager();
		final MongoCollection<?> collection = masterManager.getMasterCollection();
        BasicDBObject updateFields = new BasicDBObject();
        final InetSocketAddress inetSocketAddress = SocketAddressConverter.convertToInet(address);
        updateFields.append("hostname", inetSocketAddress.getHostString());
        updateFields.append("port", inetSocketAddress.getPort());
        updateFields.append("isRunning", isRunning);
        updateFields.append("maxRam", maxRam);
        updateFields.append("startedUpAt", startedUpAt.getTime());

        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);
		collection.findOneAndUpdate(Filters.eq("masterId", masterId.toString()), setQuery);
	}
	
	@SuppressWarnings("unchecked")
	private static File createConfigFile() {
		final File configFile = new File("master.json");
		if (configFile.exists()) {
			return configFile;
		}
		final JSONObject jsonDoc = new JSONObject();
		final JSONObject masterObject = new JSONObject();
		masterObject.put("hostname", "127.0.0.1");
		masterObject.put("port", 5000);
		final JSONObject networkServerObject = new JSONObject();
		networkServerObject.put("hostname", "127.0.0.1");
		networkServerObject.put("port", 7999);
		final JSONObject httpServerObject = new JSONObject();
		httpServerObject.put("hostname", "127.0.0.1");
		httpServerObject.put("port", 8000);
		jsonDoc.put("master", masterObject);
		jsonDoc.put("networkServer", networkServerObject);
		jsonDoc.put("httpServer", httpServerObject);
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

	@Override
	public void shutDown() {
		IltisCloud.getLogger().log(Level.SEVERE, "Shutting down CloudMaster...");
		IltisCloud.getAPI().destroy();
		Util.waitAndExit(3000);
	}

	@Override
	public void kill() {
		IltisCloud.getLogger().log(Level.SEVERE, "Killing CloudMaster...");
		System.exit(0);
	}

	@Override
	public void setRunning(boolean value) {
		this.isRunning = value;
	}
}
