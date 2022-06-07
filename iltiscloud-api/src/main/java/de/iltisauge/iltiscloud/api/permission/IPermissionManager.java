package de.iltisauge.iltiscloud.api.permission;

import java.util.List;

import de.iltisauge.iltiscloud.api.IManager;

public interface IPermissionManager extends IManager {
	
	/**
	 * 
	 * @param name
	 * @return the {@link IGroup} by the given name.
	 */
	IGroup getGroup(String name);
	
	/**
	 * 
	 * @return a {@link List} containing all existing groups.
	 */
	List<IGroup> getGroups();

}
