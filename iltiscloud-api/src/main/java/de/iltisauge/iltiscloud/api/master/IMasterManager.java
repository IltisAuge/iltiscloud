package de.iltisauge.iltiscloud.api.master;

import de.iltisauge.iltiscloud.api.IManager;

public interface IMasterManager extends IManager {
	
	IMaster getMaster();

	void initializeDatabase();

}
