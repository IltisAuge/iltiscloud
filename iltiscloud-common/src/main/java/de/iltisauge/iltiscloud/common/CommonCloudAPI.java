package de.iltisauge.iltiscloud.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.iltisauge.databaseapi.databases.MongoDatabase;
import de.iltisauge.iltiscloud.api.ICloudAPI;
import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.clouduser.ICloudUserManager;
import de.iltisauge.iltiscloud.api.logging.ConsoleLoggingFormatter;
import de.iltisauge.iltiscloud.api.logging.LoggingUtil;
import de.iltisauge.iltiscloud.api.master.IMasterManager;
import de.iltisauge.iltiscloud.api.permission.IPermissionManager;
import de.iltisauge.iltiscloud.api.server.IServerManager;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket;
import de.iltisauge.iltiscloud.api.wrapper.IWrapperManager;
import de.iltisauge.iltiscloud.common.server.packets.ExecuteCommandPacket;
import de.iltisauge.iltiscloud.common.server.packets.UpdateServerPacket;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.client.NetworkClient;
import de.iltisauge.transport.network.NetworkManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommonCloudAPI implements ICloudAPI {

	private IMasterManager masterManager;
	private IWrapperManager wrapperManager;
	private IServerManager serverManager;
	private ICloudUserManager cloudUserManager;
	private IPermissionManager permissionManager;
	/**
	 * The network client can be null, if this cloud instance runs on the cloud master.
	 */
	private NetworkClient networkClient;
	private MongoDatabase database;
	
	@Override
	public void initialize() {
		Logger logger = null;
		if ((logger = IltisCloud.getLogger()) == null) {
			logger = Logger.getLogger("iltiscloud");
			LoggingUtil.setFormatter(new ConsoleLoggingFormatter(), logger);
			IltisCloud.setLogger(logger);
		}
		logger.log(Level.INFO, "Initializing CommonCloudAPI...");
		Transport.setClient(networkClient);
		registerDefaultTransportCodecs();
		database.tryToConnect();
		logger.log(Level.INFO, "Initializing managers...");
		wrapperManager.initialize();
		serverManager.initialize();
		cloudUserManager.initialize();
		permissionManager.initialize();
		masterManager.initializeDatabase();
		masterManager.initialize();
		logger.log(Level.INFO, "Initialized CommonCloudAPI.");
	}

	@Override
	public void destroy() {
		IltisCloud.getLogger().log(Level.INFO, "Destroying CommonCloudAPI...");
		unregisterDefaultTransportCodecs();
		wrapperManager.destroy();
		serverManager.destroy();
		cloudUserManager.destroy();
		permissionManager.destroy();
		masterManager.destroy();
		database.disconnect();
		IltisCloud.getLogger().log(Level.INFO, "Destroyed CommonCloudAPI.");
	}
	
	public void registerDefaultTransportCodecs() {
		IltisCloud.getLogger().log(Level.INFO, "Registering default transport codecs...");
		final NetworkManager networkManager = Transport.getNetworkManager();
		networkManager.registerCodec(UpdateStartablePacket.class, UpdateStartablePacket.CODEC);
		networkManager.registerCodec(UpdateServerPacket.class, UpdateServerPacket.CODEC);
		networkManager.registerCodec(ExecuteCommandPacket.class, ExecuteCommandPacket.CODEC);
		networkManager.registerDefaultCodecs();
	}
	
	public void unregisterDefaultTransportCodecs() {
		IltisCloud.getLogger().log(Level.INFO, "Unregistering default transport codecs...");
		final NetworkManager networkManager = Transport.getNetworkManager();
		networkManager.unregisterCodec(UpdateStartablePacket.class);
		networkManager.unregisterCodec(UpdateServerPacket.class);
		networkManager.unregisterCodec(ExecuteCommandPacket.class);
		networkManager.unregisterDefaultCodecs();
	}
}
