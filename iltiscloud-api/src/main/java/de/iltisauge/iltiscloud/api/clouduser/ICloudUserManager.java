package de.iltisauge.iltiscloud.api.clouduser;

import java.util.UUID;

import de.iltisauge.iltiscloud.api.IManager;

public interface ICloudUserManager extends IManager {
	
	/**
	 * 
	 * @param userId
	 * @return the {@link ICloudUser}, if the user exists, otherwise {@code null}.
	 */
	ICloudUser getCloudUser(UUID userId);
	
	/**
	 * 
	 * @param username
	 * @return the {@link ICloudUser}, if the user exists, otherwise {@code null}.
	 */
	ICloudUser getCloudUser(String username);
	
	/**
	 * Updates the given field in the database.
	 * @param uniqueId
	 * @param field
	 * @param newValue
	 */
	void updateUserData(UUID uniqueId, String field, String newValue);
	
	/**
	 * Creates a new cloud user with the given data.
	 * @param username
	 * @param passwordHash
	 */
	void createCloudUser(String username, String passwordHash);

}
