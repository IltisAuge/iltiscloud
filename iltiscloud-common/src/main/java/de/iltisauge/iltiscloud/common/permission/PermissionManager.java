package de.iltisauge.iltiscloud.common.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import de.iltisauge.databaseapi.databases.MongoDatabase;
import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.permission.IGroup;
import de.iltisauge.iltiscloud.api.permission.IPermissionManager;

public class PermissionManager implements IPermissionManager {

	// Other
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	// Caching
	private final Map<String, IGroup> groups = new HashMap<String, IGroup>();
	
	// Database
	private static final String GROUP_COLLECTION = "groups";
	private static final String NAME_FIELD = "name";
	private static final String DISPLAY_NAME_FIELD = "displayName";
	private static final String SORTING_INDEX_FIELD = "sortingIndex";
	
	@Override
	public void initialize() {
		final MongoDatabase database = IltisCloud.getAPI().getDatabase();
		final MongoCollection<Document> groupCollection = database.getMongoDatabase().getCollection(GROUP_COLLECTION);
		final FindIterable<Document> iterable = groupCollection.find();
		for (Document document : iterable) {
			final String name = document.getString(NAME_FIELD);
			final String displayName = document.getString(DISPLAY_NAME_FIELD);
			final Integer sortingIndex = document.getInteger(SORTING_INDEX_FIELD);
			groups.put(name, new Group(name, displayName, sortingIndex));
		}
	}
	
	@Override
	public IGroup getGroup(String name) {
		lock.readLock().lock();
		try {
			return groups.get(name);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public List<IGroup> getGroups() {
		lock.readLock().lock();
		try {
			return new ArrayList<IGroup>(groups.values());
		} finally {
			lock.readLock().unlock();
		}
	}
}
