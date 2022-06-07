package de.iltisauge.iltiscloud.common.clouduser;

import java.util.Collection;
import java.util.UUID;

import org.bson.Document;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import de.iltisauge.databaseapi.Credential;
import de.iltisauge.databaseapi.databases.MongoDatabase;
import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.clouduser.ICloudUser;
import de.iltisauge.iltiscloud.api.clouduser.ICloudUserManager;
import de.iltisauge.iltiscloud.api.permission.IGroup;

public class CloudUserManager implements ICloudUserManager {

	private MongoDatabase iltisCloudDatabase;
	private MongoCollection<?> cloudUserCollection;
	public static final String CLOUD_USERS_COLLECTION = "cloudUsers";
	public static final String USER_ID_FIELD = "userId";
	public static final String USERNAME_FIELD = "username";
	public static final String PASSWORD_HASH_FIELD = "passwordHash";
	public static final String GROUP_FIELD = "group";
	
	private LoadingCache<UUID, ICloudUser> cloudUserCache;
	
	@Override
	public void initialize() {
		cloudUserCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, ICloudUser>() {
			
			@Override
			public ICloudUser load(UUID key) throws Exception {
				final Document document = (Document) cloudUserCollection.find(new Document("userId", key.toString())).first();
				if (document == null) {
					return null;
				}
				final ICloudUser user = getCloudUserFromDocument(document);
				return user;
			}
		});
		iltisCloudDatabase = new MongoDatabase(new Credential("localhost", 27017, "admin", "admin", "admin"));
		iltisCloudDatabase.tryToConnect();
		cloudUserCollection = iltisCloudDatabase.getMongoDatabase().getCollection(CLOUD_USERS_COLLECTION);
	}
	
	@Override
	public ICloudUser getCloudUser(UUID userId) {
		try {
			return cloudUserCache.get(userId);
		} catch (Exception exception) {
		}
		return null;
	}
	
	@Override
	public ICloudUser getCloudUser(String username) {
		final Collection<ICloudUser> cached = cloudUserCache.asMap().values();
		for (ICloudUser cloudUser : cached) {
			if (cloudUser.getUsername().equals(username)) {
				return cloudUser;
			}
		}
		final Document document = (Document) cloudUserCollection.find(Filters.eq(USERNAME_FIELD, username)).first();
		if (document == null) {
			return null;
		}
		final ICloudUser user = getCloudUserFromDocument(document);
		return user;
	}

	@Override
	public void updateUserData(UUID uniqueId, String field, String newValue) {
		cloudUserCollection.updateOne(Filters.eq(USER_ID_FIELD, uniqueId.toString()), Updates.set(field, newValue));
	}
	
	@Override
	public void createCloudUser(String username, String passwordHash) {
		
	}
	
	public ICloudUser getCloudUserFromDocument(Document document) {
		final UUID userId = UUID.fromString(document.getString(USER_ID_FIELD));
		final String username = document.getString(USERNAME_FIELD);
		final String passwordHash = document.getString(PASSWORD_HASH_FIELD);
		final IGroup group = IltisCloud.getAPI().getPermissionManager().getGroup(document.getString(GROUP_FIELD));
		return new CloudUser(userId, username, passwordHash, group);
	}
}
