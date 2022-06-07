package de.iltisauge.iltiscloud.common.clouduser;

import java.util.UUID;

import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.clouduser.ICloudUser;
import de.iltisauge.iltiscloud.api.permission.IGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class CloudUser implements ICloudUser {

	private final UUID userId;
	private String username;
	private String passwordHash;
	private IGroup group;
	
	@Override
	public void setUsername(String username) {
		this.username = username;
		IltisCloud.getAPI().getCloudUserManager().updateUserData(userId, CloudUserManager.USERNAME_FIELD, username);
	}

	@Override
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
		IltisCloud.getAPI().getCloudUserManager().updateUserData(userId, CloudUserManager.PASSWORD_HASH_FIELD, passwordHash);
	}
	
	@Override
	public void setGroup(String groupName) {
		setGroup(IltisCloud.getAPI().getPermissionManager().getGroup(groupName));
	}

	@Override
	public void setGroup(IGroup group) {
		this.group = group;
		IltisCloud.getAPI().getCloudUserManager().updateUserData(userId, CloudUserManager.GROUP_FIELD, group.getName());
	}
}
