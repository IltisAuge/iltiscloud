package de.iltisauge.iltiscloud.api.clouduser;

import java.util.UUID;

import de.iltisauge.iltiscloud.api.permission.IGroup;

public interface ICloudUser {
	
	/**
	 * 
	 * @return the {@link UUID} of the cloud user.
	 */
	UUID getUserId();

	/**
	 * 
	 * @return the username of the cloud user.
	 */
	String getUsername();
	
	/**
	 * Sets the username of the cloud user.
	 * @param username
	 */
	void setUsername(String username);

	/**
	 * 
	 * @return the hashed password of the cloud user.
	 */
	String getPasswordHash();
	
	/**
	 * Sets the password hash of the cloud user.
	 * @param passwordHash
	 */
	void setPasswordHash(String passwordHash);
	
	/**
	 * 
	 * @return the group of the cloud user.
	 */
	IGroup getGroup();
	
	/**
	 * Sets the group of the cloud user.
	 * @param groupName
	 */
	void setGroup(String groupName);
	
	/**
	 * Sets the group of the cloud user.
	 * @param group
	 */
	void setGroup(IGroup group);

}
