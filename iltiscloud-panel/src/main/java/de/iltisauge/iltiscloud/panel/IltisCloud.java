package de.iltisauge.iltiscloud.panel;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;

import de.iltisauge.databaseapi.Credential;
import de.iltisauge.databaseapi.databases.MongoDatabase;
import de.iltisauge.iltiscloud.api.ICloudAPI;
import de.iltisauge.iltiscloud.api.logging.ConsoleLoggingFormatter;
import de.iltisauge.iltiscloud.api.logging.LoggingUtil;
import de.iltisauge.iltiscloud.api.master.IMasterManager;
import de.iltisauge.iltiscloud.api.utils.Util;
import de.iltisauge.iltiscloud.common.CommonCloudAPI;
import de.iltisauge.iltiscloud.common.clouduser.CloudUserManager;
import de.iltisauge.iltiscloud.common.master.MasterManager;
import de.iltisauge.iltiscloud.common.permission.PermissionManager;
import de.iltisauge.iltiscloud.common.server.ServerManager;
import de.iltisauge.iltiscloud.common.wrapper.WrapperManager;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.client.ClientNetworkManager;
import de.iltisauge.transport.client.NetworkClient;

public class IltisCloud {
	
	private static ICloudAPI CLOUD_API;
	private static boolean gernerated = false;
	
	public static ICloudAPI getAPI() {
		if (!gernerated) {
			gernerated = true;
			System.out.println("Gernerating new CLOUD_API");
			final Logger logger = Logger.getLogger("iltiscloud");
			LoggingUtil.setFormatter(new ConsoleLoggingFormatter(), logger);
			de.iltisauge.iltiscloud.api.IltisCloud.setLogger(logger);
			Transport.setLogger(logger);
			// Read panel.json
			final File configFile = new File("panel.json");
			final BasicDBObject fileObject = BasicDBObject.parse(Util.readContentFromFile(configFile));
			final BasicDBObject masterObject = (BasicDBObject) fileObject.get("master");
			final UUID masterId = UUID.fromString(masterObject.getString("masterId"));
			final BasicDBObject networkServerObject = (BasicDBObject) fileObject.get("networkServer");
			final String networkServerHostname = networkServerObject.getString("hostname");
			final Integer networkServerPort = networkServerObject.getInt("port");
			final InetSocketAddress networkServerAddress = new InetSocketAddress(networkServerHostname, networkServerPort);

			final MongoDatabase database = new MongoDatabase(new Credential("localhost", 27017, "admin", "admin", "admin"));
			database.tryToConnect();
			final IMasterManager masterManager = new MasterManager(masterId, database);
			masterManager.initializeDatabase();
			final ClientNetworkManager networkManager = new ClientNetworkManager();
			final NetworkClient networkClient = new NetworkClient(networkManager, networkServerAddress);
			networkClient.initialize();
			networkClient.start();
			Transport.setClient(networkClient);
			CLOUD_API = new CommonCloudAPI(masterManager, new WrapperManager(), new ServerManager(), new CloudUserManager(), new PermissionManager(), networkClient, database);
			de.iltisauge.iltiscloud.api.IltisCloud.setAPI(CLOUD_API);
			CLOUD_API.initialize();
		}
		return CLOUD_API;
	}
}
