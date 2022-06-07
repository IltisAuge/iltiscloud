package de.iltisauge.iltiscloud.wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import de.iltisauge.iltiscloud.api.master.IMasterManager;
import de.iltisauge.iltiscloud.api.server.IServer;
import de.iltisauge.iltiscloud.api.server.ServerType;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket.ConfirmationType;
import de.iltisauge.iltiscloud.api.utils.SocketAddressConverter;
import de.iltisauge.iltiscloud.api.utils.Util;
import de.iltisauge.iltiscloud.api.wrapper.IWrapper;
import de.iltisauge.iltiscloud.common.CommonCloudAPI;
import de.iltisauge.iltiscloud.common.clouduser.CloudUserManager;
import de.iltisauge.iltiscloud.common.master.MasterManager;
import de.iltisauge.iltiscloud.common.permission.PermissionManager;
import de.iltisauge.iltiscloud.common.server.ServerManager;
import de.iltisauge.iltiscloud.common.wrapper.Wrapper;
import de.iltisauge.iltiscloud.common.wrapper.WrapperManager;
import de.iltisauge.iltiscloud.wrapper.template.TemplateManager;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.client.ClientNetworkManager;
import de.iltisauge.transport.client.NetworkClient;
import de.iltisauge.transport.network.IMessageEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CloudWrapper extends Wrapper implements IWrapper {

	private final UUID wrapperId;
	private final String name;
	private final SocketAddress address;
	private boolean isRunning;
	private final Integer maxRam;
	private final SocketAddress masterAddress;
	private final SocketAddress networkServerAddress;
	private final InetSocketAddress httpServerAddress;
	private final List<UUID> serverList = new ArrayList<UUID>();
	@Setter
	private Date startedUpAt;
	private static MongoDatabase database;
	private static IMasterManager masterManager;
	private de.iltisauge.iltiscloud.wrapper.server.ServerManager serverManager;
	
	public CloudWrapper(UUID wrapperId, SocketAddress address, String name, boolean isRunning, Integer maxRam, SocketAddress masterAddress,
			SocketAddress networkServerAddress, InetSocketAddress httpServerAddress) {
		super(wrapperId, address, name, isRunning, maxRam);
		this.wrapperId = wrapperId;
		this.address = address;
		this.name = name;
		this.isRunning = isRunning;
		this.maxRam = maxRam;
		this.masterAddress = masterAddress;
		this.networkServerAddress = networkServerAddress;
		this.httpServerAddress = httpServerAddress;
	}
	
	public static void main(String[] args) {
		final Logger logger = Logger.getLogger("iltiscloud");
		LoggingUtil.setFormatter(new ConsoleLoggingFormatter(), logger);
		IltisCloud.setLogger(logger);
		Transport.setLogger(logger);
		
		// Read wrapper.json
		logger.log(Level.INFO, "Reading config files...");
		final File configFile = createConfigFile();
		final BasicDBObject fileObject = BasicDBObject.parse(Util.readContentFromFile(configFile));
		final BasicDBObject localObject = (BasicDBObject) fileObject.get("wrapper");
		final UUID wrapperId = UUID.fromString(localObject.getString("wrapperId"));
		final String wrapperHostname = localObject.getString("hostname");
		final Integer wrapperPort = localObject.getInt("port");
		final InetSocketAddress localAddress = new InetSocketAddress(wrapperHostname, wrapperPort);
		final String wrapperName = localObject.getString("name");
		final BasicDBObject masterObject = (BasicDBObject) fileObject.get("master");
//		final String masterHostname = masterObject.getString("hostname");
//		final Integer masterPort = masterObject.getInt("port");
		final UUID masterId = UUID.fromString(masterObject.getString("masterId"));
		final BasicDBObject networkServerObject = (BasicDBObject) fileObject.get("networkServer");
		final String networkServerHostname = networkServerObject.getString("hostname");
		final Integer networkServerPort = networkServerObject.getInt("port");
		final InetSocketAddress networkServerAddress = new InetSocketAddress(networkServerHostname, networkServerPort);
		final BasicDBObject httpServerObject = (BasicDBObject) fileObject.get("httpServer");
		final String httpServerHostname = httpServerObject.getString("hostname");
		final Integer httpServerPort = httpServerObject.getInt("port");
		final InetSocketAddress httpServerAddress = new InetSocketAddress(httpServerHostname, httpServerPort);
		
		final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		final int maxRam = (int) (memoryBean.getHeapMemoryUsage().getMax() / 1024L / 1024L);
		database = new MongoDatabase(new Credential("localhost", 27017, "admin", "admin", "admin"));
		database.tryToConnect();
		masterManager = new MasterManager(masterId, database);
		masterManager.initializeDatabase();
		//final InetSocketAddress masterAddress = new InetSocketAddress(masterHostname, masterPort);
		final IMaster master = masterManager.getMaster();
		final CloudWrapper cloudWrapper = new CloudWrapper(wrapperId, localAddress, wrapperName, false, maxRam, master.getAddress(), networkServerAddress, httpServerAddress);
		cloudWrapper.startUp();
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						if (line.equals("kill") || line.equals("exit")) {
							System.exit(0);
							return;
						}
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void startUp() {
		final Logger logger = IltisCloud.getLogger();
		logger.log(Level.INFO, "Starting up CloudWrapper on " + address.toString() + "...");
		
		final String mySQLDriverClassName = "com.mysql.cj.jdbc.Driver";
		logger.log(Level.INFO, "Loading MySQL driver class " + mySQLDriverClassName + "...");
		try {
			Class.forName(mySQLDriverClassName);
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}

		logger.log(Level.INFO, "Instancing NetworkManager...");
		final ClientNetworkManager networkManager = new ClientNetworkManager();
		
		logger.log(Level.INFO, "Instancing NetworkClient...");
		final NetworkClient networkClient = new NetworkClient(networkManager, networkServerAddress) {
			
			@Override
			public void onStarted() {
				networkManager.addSubscriptions("execute-server-command");
			}
		};
		networkClient.initialize();
		networkClient.start(true);
		Transport.setClient(networkClient);

		logger.log(Level.INFO, "Instancing CommonCloudAPI...");
		final CommonCloudAPI cloudAPI = new CommonCloudAPI(masterManager, new WrapperManager(), new ServerManager(), new CloudUserManager(), new PermissionManager(), networkClient, database);
		IltisCloud.setAPI(cloudAPI);
		cloudAPI.initialize();
		
		final TemplateManager templateManager = new TemplateManager();
		templateManager.loadTemplateFile();
		templateManager.downloadTemplates();
		
		serverManager = new de.iltisauge.iltiscloud.wrapper.server.ServerManager(this);
		serverManager.initialize();
		
		networkManager.registerEvent(new IMessageEvent<UpdateStartablePacket>() {
			
			@Override
			public void onReceived(UpdateStartablePacket message) {
				final Type type = message.getStartableType();
				if (type.equals(Type.SERVER) && message.getConfirmationType().equals(ConfirmationType.UNCONFIRMED)) {
					final IServer server = cloudAPI.getServerManager().getServer(message.getUniqueId());
					if (server == null) {
						return;
					}
					final ServerType serverType = server.getServerType();
					final String serverName = server.getName();
					final File file = new File(serverType.equals(ServerType.TEMPLATE) ? "templates" : "static", serverName);
					if (!file.exists()) {
						System.out.println("Server not found in local storage.");
						return;
					}
					serverManager.startServer(file);
				}
			}
		});

		isRunning = true;
		startedUpAt = new Date();
		logger.log(Level.INFO, "Updating data in database...");
		updateWrapperDataInDB();
		serverManager.startServer(templateManager.getTemplate("lobby"));
	}
	
	private void updateWrapperDataInDB() {
		final WrapperManager wrapperManager = (WrapperManager) IltisCloud.getAPI().getWrapperManager();
		final MongoCollection<?> collection = wrapperManager.getWrapperCollection();
        BasicDBObject updateFields = new BasicDBObject();
        final InetSocketAddress inetSocketAddress = SocketAddressConverter.convertToInet(address);
        updateFields.append("wrapperId", wrapperId.toString());
        updateFields.append("address", inetSocketAddress.toString());
        updateFields.append("name", name);
        updateFields.append("isRunning", isRunning);
        updateFields.append("maxRam", maxRam);
        updateFields.append("startedUpAt", startedUpAt.getTime());

        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);
		collection.updateOne(Filters.eq("wrapperId", wrapperId.toString()), setQuery);
	}
	
	@SuppressWarnings("unchecked")
	private static File createConfigFile() {
		final File configFile = new File("wrapper.json");
		if (configFile.exists()) {
			return configFile;
		}
		final JSONObject jsonDoc = new JSONObject();
		final JSONObject wrapperObject = new JSONObject();
		wrapperObject.put("wrapperId", "PUT WRAPPER ID HERE");
		wrapperObject.put("hostname", "127.0.0.1");
		wrapperObject.put("port", 4999);
		wrapperObject.put("name", "Wrapper-01");
		final JSONObject masterObject = new JSONObject();
		masterObject.put("masterId", "PUT MASTER ID HERE");
//		masterObject.put("hostname", "127.0.0.1");
//		masterObject.put("port", 5000);
		final JSONObject networkServerObject = new JSONObject();
		networkServerObject.put("hostname", "127.0.0.1");
		networkServerObject.put("port", 7999);
		final JSONObject httpServerObject = new JSONObject();
		httpServerObject.put("hostname", "127.0.0.1");
		httpServerObject.put("port", 8000);
		jsonDoc.put("wrapper", wrapperObject);
		jsonDoc.put("master", masterObject);
		jsonDoc.put("networkServer", networkServerObject);
		jsonDoc.put("httpServer", httpServerObject);
		try {
			configFile.createNewFile();
		} catch (IOException exception) {
			IltisCloud.getLogger().log(Level.WARNING, "Error while creating config file:", exception);
		}
		Util.writeJson(configFile, jsonDoc);
		IltisCloud.getLogger().log(Level.INFO, "Created file 'wrapper.json'. Please setup all data.");
		Util.waitAndExit(3000);
		return configFile;
	}

	@Override
	public void shutDown() {
		IltisCloud.getLogger().log(Level.SEVERE, "Shutting down CloudWrapper...");
		IltisCloud.getAPI().destroy();
		Util.waitAndExit(3000);
	}

	@Override
	public void kill() {
		IltisCloud.getLogger().log(Level.SEVERE, "Killing CloudWrapper...");
		System.exit(0);
	}
}
