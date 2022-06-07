package de.iltisauge.iltiscloud.api;

import de.iltisauge.databaseapi.databases.MongoDatabase;
import de.iltisauge.iltiscloud.api.clouduser.ICloudUserManager;
import de.iltisauge.iltiscloud.api.master.IMasterManager;
import de.iltisauge.iltiscloud.api.permission.IPermissionManager;
import de.iltisauge.iltiscloud.api.server.IServerManager;
import de.iltisauge.iltiscloud.api.wrapper.IWrapperManager;
import de.iltisauge.transport.client.NetworkClient;

public interface ICloudAPI {

	void initialize();
	
	void destroy();
	
	void setMasterManager(IMasterManager masterManager);
	
	IMasterManager getMasterManager();
	
	void setWrapperManager(IWrapperManager wrapperManager);
	
	IWrapperManager getWrapperManager();
	
	void setServerManager(IServerManager serverManager);
	
	IServerManager getServerManager();
	
	void setCloudUserManager(ICloudUserManager cloudUserManager);
	
	ICloudUserManager getCloudUserManager();
	
	void setPermissionManager(IPermissionManager permissionManager);
	
	IPermissionManager getPermissionManager();
	
	NetworkClient getNetworkClient();
	
	MongoDatabase getDatabase();

}
